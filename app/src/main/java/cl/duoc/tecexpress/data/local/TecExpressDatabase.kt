package cl.duoc.tecexpress.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ServiceEntity::class, UserEntity::class], version = 2, exportSchema = false) // 1. AÑADIMOS UserEntity Y SUBIMOS LA VERSIÓN
abstract class TecExpressDatabase : RoomDatabase() {

    abstract fun serviceDao(): ServiceDao
    abstract fun userDao(): UserDao // 2. AÑADIMOS EL DAO DEL USUARIO

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
                .fallbackToDestructiveMigration() // 3. AÑADIMOS UNA ESTRATEGIA DE MIGRACIÓN SIMPLE
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
