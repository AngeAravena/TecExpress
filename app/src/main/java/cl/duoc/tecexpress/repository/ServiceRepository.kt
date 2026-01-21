package cl.duoc.tecexpress.repository

import cl.duoc.tecexpress.data.local.ServiceDao
import cl.duoc.tecexpress.model.Service
import cl.duoc.tecexpress.model.ServiceMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ServiceRepository(private val dao: ServiceDao) {

    // Este método será para el admin en el futuro
    val allServices: Flow<List<Service>> = dao.getAll().map {
        it.map(ServiceMapper::fromEntity)
    }

    // Nuevo método para obtener servicios de un usuario específico
    fun getServicesForUser(userId: Long): Flow<List<Service>> {
        return dao.getServicesForUser(userId).map {
            it.map(ServiceMapper::fromEntity)
        }
    }

    // Nueva función para obtener las plantillas de servicios
    val serviceTemplates: Flow<List<Service>> = dao.getServiceTemplates().map {
        it.map(ServiceMapper::fromEntity)
    }

    suspend fun insert(service: Service) {
        dao.insert(ServiceMapper.toEntity(service))
    }

    suspend fun delete(service: Service) {
        dao.delete(ServiceMapper.toEntity(service))
    }
}
