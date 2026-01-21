package cl.duoc.tecexpress.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.tecexpress.data.local.ServiceCategory
import cl.duoc.tecexpress.model.Service
import cl.duoc.tecexpress.model.ServiceStatus
import cl.duoc.tecexpress.repository.ServiceRepository
import cl.duoc.tecexpress.repository.UserRepository
import cl.duoc.tecexpress.util.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ServiceVM(
    private val serviceRepository: ServiceRepository,
    private val notificationHelper: NotificationHelper,
    private val userRepository: UserRepository,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _formUiState = MutableStateFlow(ServiceFormUiState())
    val formUiState: StateFlow<ServiceFormUiState> = _formUiState.asStateFlow()

    val allServices: StateFlow<List<Service>> = authViewModel.uiState.flatMapLatest { authState ->
        serviceRepository.getServicesForUser(authState.currentUser?.id ?: 0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val serviceTemplates: StateFlow<List<Service>> = serviceRepository.serviceTemplates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onServiceTypeChange(service: Service) {
        _formUiState.update { it.copy(selectedService = service, finalPrice = service.price, isServiceError = false) }
        if (service.category != ServiceCategory.BATTERY) {
            _formUiState.update { it.copy(selectedOs = null) }
        }
    }

    fun onOsChange(os: String) {
        val currentService = _formUiState.value.selectedService
        if (currentService != null && currentService.category == ServiceCategory.BATTERY) {
            val newPrice = if (os == "iOS (exclusivo en iPhone)") {
                currentService.price * 1.4
            } else {
                currentService.price
            }
            _formUiState.update { it.copy(selectedOs = os, finalPrice = newPrice) }
        }
    }

    fun onDescriptionChange(description: String) {
        _formUiState.update { it.copy(description = description) }
    }

    fun saveService() {
        val selectedService = _formUiState.value.selectedService

        if (selectedService == null) {
            _formUiState.update { it.copy(isServiceError = true) }
            return
        }

        viewModelScope.launch {
            val description = _formUiState.value.description
            val finalPrice = _formUiState.value.finalPrice
            val os = _formUiState.value.selectedOs
            val userId = authViewModel.uiState.value.currentUser?.id ?: return@launch
            val finalDescription = if (description.isBlank()) "Sin descripción" else description

            val newService = Service(
                serviceType = selectedService.serviceType,
                description = finalDescription,
                price = finalPrice,
                os = os,
                category = selectedService.category,
                status = ServiceStatus.PENDING,
                userId = userId
            )
            serviceRepository.insert(newService)

            authViewModel.uiState.value.currentUser?.let {
                notificationHelper.sendNotification(
                    title = "Solicitud Creada",
                    message = "${it.username}, se ha creado la solicitud exitosamente"
                )
            }
            _formUiState.update { it.copy(saveSuccess = true) }
        }
    }

    fun onSaveComplete() {
        _formUiState.value = ServiceFormUiState()
    }
}

data class ServiceFormUiState(
    val selectedService: Service? = null,
    val description: String = "",
    val selectedOs: String? = null,
    val finalPrice: Double = 0.0,
    val isServiceError: Boolean = false,
    val saveSuccess: Boolean = false
)
