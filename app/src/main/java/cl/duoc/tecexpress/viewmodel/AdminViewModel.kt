package cl.duoc.tecexpress.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.tecexpress.data.local.ServiceCategory
import cl.duoc.tecexpress.data.local.UserEntity
import cl.duoc.tecexpress.model.Service
import cl.duoc.tecexpress.model.ServiceStatus
import cl.duoc.tecexpress.repository.ServiceRepository
import cl.duoc.tecexpress.repository.UserRepository
import cl.duoc.tecexpress.util.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminViewModel(
    private val serviceRepository: ServiceRepository,
    private val userRepository: UserRepository,
    private val notificationHelper: NotificationHelper,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    val allServices: StateFlow<List<Service>> = serviceRepository.allServices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = userRepository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteService(service: Service) {
        viewModelScope.launch {
            serviceRepository.delete(service)
        }
    }

    fun onFormFieldChange(
        serviceType: String = _uiState.value.serviceType,
        description: String = _uiState.value.description,
        userId: String = _uiState.value.userId,
        imageUrl: String = _uiState.value.imageUrl
    ) {
        _uiState.update { it.copy(
            serviceType = serviceType,
            description = description,
            userId = userId,
            imageUrl = imageUrl,
            error = null
        ) }
    }

    fun startEditing(service: Service) {
        _uiState.update {
            it.copy(
                editingService = service,
                serviceType = service.serviceType,
                description = service.description,
                userId = service.userId.toString(),
                imageUrl = service.imageUrl ?: ""
            )
        }
    }

    fun stopEditing() {
        _uiState.update { it.copy(editingService = null, serviceType = "", description = "", userId = "", imageUrl = "", error = null) }
    }

    fun saveService() {
        viewModelScope.launch {
            val userIdLong = _uiState.value.userId.toLongOrNull()
            if (userIdLong == null || userRepository.findById(userIdLong) == null) {
                _uiState.update { it.copy(error = "ERROR: el numero identificador(ID usuario) no existe o es incorrecto") }
                return@launch
            }

            val serviceToSave = _uiState.value.editingService?.copy(
                serviceType = _uiState.value.serviceType,
                description = _uiState.value.description,
                userId = userIdLong,
                category = _uiState.value.category,
                imageUrl = _uiState.value.imageUrl.takeIf { it.isNotBlank() }
            ) ?: Service(
                serviceType = _uiState.value.serviceType,
                description = _uiState.value.description,
                userId = userIdLong,
                price = 0.0,
                status = ServiceStatus.PENDING,
                category = _uiState.value.category,
                imageUrl = _uiState.value.imageUrl.takeIf { it.isNotBlank() }
            )

            serviceRepository.insert(serviceToSave)
            stopEditing()
        }
    }

    fun updateServiceStatus(service: Service, newStatus: ServiceStatus) {
        viewModelScope.launch {
            val updatedService = service.copy(status = newStatus)
            serviceRepository.insert(updatedService)

            val user = userRepository.findById(service.userId)
            user?.let {
                val title = "Actualización de tu Servicio"
                val message = when (newStatus) {
                    ServiceStatus.PENDING -> "Hola ${it.username}, tu servicio \"${service.serviceType}\" ha cambiado a PENDIENTE."
                    ServiceStatus.IN_PROGRESS -> "Hola ${it.username}, tu servicio \"${service.serviceType}\" ha entrado EN PROGRESO."
                    ServiceStatus.COMPLETED -> "¡Hola ${it.username}! Tu servicio \"${service.serviceType}\" ya está TERMINADO. ¡Puedes venir a retirarlo!"
                }
                notificationHelper.sendNotification(title, message)
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class AdminUiState(
    val serviceType: String = "",
    val description: String = "",
    val userId: String = "",
    val imageUrl: String = "",
    val error: String? = null,
    val editingService: Service? = null,
    val category: ServiceCategory = ServiceCategory.OTHER
)
