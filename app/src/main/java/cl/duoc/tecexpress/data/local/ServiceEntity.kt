package cl.duoc.tecexpress.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import cl.duoc.tecexpress.model.ServiceStatus



@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val status: String
    //En la base de datos se almacena como String(ServiceEntity)
    //En la app se almacena como enum (ServiceStatus)
    //ServiceMapper convierte entre ambos formatos
    //Status queda sellado a 3 estados (pending, in_progress, completed)
)
