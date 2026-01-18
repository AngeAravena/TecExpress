package cl.duoc.tecexpress.data.local

@Database(entities = [ServiceEntity::class], version = 1)

abstract class TecExpressDatabase : RoomDatabase(){

    abstract fun serviceDao(): ServiceDao
}

