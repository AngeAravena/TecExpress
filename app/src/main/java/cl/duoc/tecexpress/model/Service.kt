package cl.duoc.tecexpress.model

import cl.duoc.tecexpress.data.local.ServiceCategory

data class Service(
    val id: Int = 0,
    val serviceType: String,
    val description: String,
    val price: Double,
    val os: String? = null,
    val category: ServiceCategory,
    val status: ServiceStatus,
    val userId: Long,
    val imageUrl: String? = null
)
