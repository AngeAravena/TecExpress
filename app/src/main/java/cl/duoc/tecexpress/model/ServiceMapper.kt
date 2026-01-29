package cl.duoc.tecexpress.model

import cl.duoc.tecexpress.data.local.ServiceEntity

object ServiceMapper {

    fun fromEntity(entity: ServiceEntity): Service {
        return Service(
            id = entity.id,
            serviceType = entity.serviceType,
            description = entity.description,
            price = entity.price,
            os = entity.os,
            category = entity.category,
            status = ServiceStatus.valueOf(entity.status),
            userId = entity.userId,
            imageUrl = entity.imageUrl
        )
    }

    fun toEntity(service: Service): ServiceEntity {
        return ServiceEntity(
            id = service.id,
            serviceType = service.serviceType,
            description = service.description,
            price = service.price,
            os = service.os,
            category = service.category,
            status = service.status.name,
            userId = service.userId,
            imageUrl = service.imageUrl
        )
    }
}
