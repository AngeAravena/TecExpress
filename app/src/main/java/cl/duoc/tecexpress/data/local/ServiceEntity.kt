package cl.duoc.tecexpress.data.local

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "services",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE // Si un usuario se elimina, sus servicios también
        )
    ]
)
data class ServiceEntity(
    @androidx.room.PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val serviceType: String,
    val description: String,
    val price: Double,
    val os: String? = null, // Nullable para servicios no móviles
    val category: ServiceCategory,
    val status: String,
    //En la app se almacena como enum (ServiceStatus)
    //ServiceMapper convierte entre ambos formatos
    //Status queda sellado a 3 estados (pending, in_progress, completed)
    val userId: Long // La columna que asocia el servicio con un usuario
)
