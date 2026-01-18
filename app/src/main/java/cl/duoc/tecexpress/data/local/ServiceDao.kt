package cl.duoc.tecexpress.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(service: ServiceEntity)

    @Query("SELECT * FROM services WHERE userId = :userId")
    fun getServicesForUser(userId: Long): Flow<List<ServiceEntity>>

    // Dejamos esta función por si el admin la necesita en el futuro
    @Query("SELECT * FROM services")
    fun getAll(): Flow<List<ServiceEntity>>
}
