package cl.duoc.tecexpress.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val serviceType: String,
    val description: String,
    val price: Double,
    val os: String? = null,
    val category: ServiceCategory,
    val status: String,
    val userId: Long,
    val imageUrl: String? = null
)
