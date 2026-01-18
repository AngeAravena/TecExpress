package cl.duoc.tecexpress.model

import cl.duoc.tecexpress.data.local.ServiceEntity

object ServiceMapper {

    fun fromEntity(entity: ServiceEntity): Service {
        return Service(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            status = ServiceStatus.valueOf(entity.status),
            //Conversion del status como String en la bd a enum para la app
            userId = entity.userId // AÑADIMOS LA CONVERSIÓN
        )
    }

    fun toEntity(service: Service): ServiceEntity {
        return ServiceEntity(
            id = service.id,
            title = service.title,
            description = service.description,
            status = service.status.name,
            userId = service.userId // AÑADIMOS LA CONVERSIÓN
        )
    }
}
