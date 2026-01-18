package cl.duoc.tecexpress.model

data class Service(
    val id: Int = 0,
    val title: String,
    val description: String,
    val status: ServiceStatus,
    val userId: Long // AÑADIMOS el userId aquí también
)
