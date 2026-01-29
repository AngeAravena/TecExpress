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

@Database(entities = [ServiceEntity::class, UserEntity::class], version = 11, exportSchema = false)
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
                        val pcImageUrl = "https://firebasestorage.googleapis.com/v0/b/appmoviles-cd8ea.firebasestorage.app/o/limpiar-ordenador-sobremesa-por-dentro.webp?alt=media&token=615e0daa-d91d-43db-802b-3a42318543d0"
                        val linternamovilUrl = "https://firebasestorage.googleapis.com/v0/b/appmoviles-cd8ea.firebasestorage.app/o/limpieza%20interna%20movil.webp?alt=media&token=72f6e3e9-697c-40e2-b1b7-d11aef894222"
                        val cambiobateriaUrl = "https://firebasestorage.googleapis.com/v0/b/appmoviles-cd8ea.firebasestorage.app/o/Cambio%20de%20bateria.jpg?alt=media&token=e11a7342-6790-4fd8-8eeb-1bdecacbcfff"
                        val formateopcUrl = "https://firebasestorage.googleapis.com/v0/b/appmoviles-cd8ea.firebasestorage.app/o/formateo%20pc.webp?alt=media&token=d2df4209-777c-4e05-bb13-688dbdf782e5"
                        val instalacionsoftwareUrl = "https://firebasestorage.googleapis.com/v0/b/appmoviles-cd8ea.firebasestorage.app/o/instalacion%20software.jpg?alt=media&token=06a4aeab-d05b-4d19-bb93-06415f41e3cb"
                        
                        db.execSQL("INSERT INTO services (serviceType, description, price, category, status, userId, imageUrl) VALUES ('Limpieza de PC de Escritorio', '', 30000.0, 'PC', 'PENDING', -1, '$pcImageUrl')")
                        db.execSQL("INSERT INTO services (serviceType, description, price, category, status, userId, imageUrl) VALUES ('Limpieza interna de móviles', '', 20000.0, 'MOBILE', 'PENDING', -1, '$linternamovilUrl')")
                        db.execSQL("INSERT INTO services (serviceType, description, price, category, status, userId, imageUrl) VALUES ('Cambio de batería', '', 40000.0, 'BATTERY', 'PENDING', -1, '$cambiobateriaUrl')")
                        db.execSQL("INSERT INTO services (serviceType, description, price, category, status, userId, imageUrl) VALUES ('Formateo de PC', '', 25000.0, 'PC', 'PENDING', -1, '$formateopcUrl')")
                        db.execSQL("INSERT INTO services (serviceType, description, price, category, status, userId, imageUrl) VALUES ('Instalación de Software', '', 15000.0, 'OTHER', 'PENDING', -1, '$instalacionsoftwareUrl')")
                        db.setTransactionSuccessful()
                    } finally {
                        db.endTransaction()
                    }
                }
            }
        }
    }
}
