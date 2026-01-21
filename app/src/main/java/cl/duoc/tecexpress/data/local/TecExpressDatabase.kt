package cl.duoc.tecexpress.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Converters {
    @TypeConverter
    fun fromCategory(value: ServiceCategory) = value.name

    @TypeConverter
    fun toCategory(value: String) = enumValueOf<ServiceCategory>(value)
}

@Database(entities = [ServiceEntity::class, UserEntity::class], version = 8, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TecExpressDatabase : RoomDatabase() {

    abstract fun serviceDao(): ServiceDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: TecExpressDatabase? = null

        fun getDatabase(context: Context): TecExpressDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TecExpressDatabase::class.java,
                    "tecexpress_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            CoroutineScope(Dispatchers.IO).launch {
                val cursor = db.query("SELECT COUNT(*) FROM services WHERE userId = -1")
                cursor.moveToFirst()
                val count = cursor.getInt(0)
                cursor.close()

                if (count == 0) {
                    db.beginTransaction()
                    try {
                        db.execSQL("INSERT INTO services (serviceType, description, price, category, status, userId) VALUES ('Limpieza de PC de Escritorio', '', 30000.0, 'PC', 'PENDING', -1)")
                        db.execSQL("INSERT INTO services (serviceType, description, price, category, status, userId) VALUES ('Limpieza interna de móviles', '', 20000.0, 'MOBILE', 'PENDING', -1)")
                        db.execSQL("INSERT INTO services (serviceType, description, price, category, status, userId) VALUES ('Cambio de batería', '', 40000.0, 'BATTERY', 'PENDING', -1)")
                        db.execSQL("INSERT INTO services (serviceType, description, price, category, status, userId) VALUES ('Formateo de PC', '', 25000.0, 'PC', 'PENDING', -1)")
                        db.execSQL("INSERT INTO services (serviceType, description, price, category, status, userId) VALUES ('Instalación de Software', '', 15000.0, 'OTHER', 'PENDING', -1)")
                        db.setTransactionSuccessful()
                    } finally {
                        db.endTransaction()
                    }
                }
            }
        }
    }
}
