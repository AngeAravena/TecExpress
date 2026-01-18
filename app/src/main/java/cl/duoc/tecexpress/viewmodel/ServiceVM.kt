package cl.duoc.tecexpress.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.tecexpress.model.Service
import cl.duoc.tecexpress.model.ServiceStatus
import cl.duoc.tecexpress.repository.ServiceRepository
import cl.duoc.tecexpress.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ServiceVM(private val repository: ServiceRepository) : ViewModel() {

    // --- SIMULACIÓN DE SESIÓN ---
    // Más adelante, este ID vendrá de la pantalla de Login.
    private val currentUserId = MutableStateFlow(1L) // Asumimos que el usuario 1 es el que ha iniciado sesión.

    // --- State for the Service List ---
    // Modificamos `allServices` para que dependa del usuario actual.
    val allServices: StateFlow<List<Service>> = repository.getServicesForUser(currentUserId.value)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- State for the Service Form ---
    private val _formUiState = MutableStateFlow(ServiceFormUiState())
    val formUiState: StateFlow<ServiceFormUiState> = _formUiState.asStateFlow()

    fun onTitleChange(title: String) {
        _formUiState.update { it.copy(title = title, isTitleError = false) }
    }

    fun onDescriptionChange(description: String) {
        _formUiState.update { it.copy(description = description, isDescriptionError = false) }
    }

    fun saveService() {
        val title = _formUiState.value.title
        val description = _formUiState.value.description

        val isTitleValid = Validators.isNotEmpty(title)
        val isDescriptionValid = Validators.isNotEmpty(description)

        if (!isTitleValid || !isDescriptionValid) {
            _formUiState.update { it.copy(isTitleError = !isTitleValid, isDescriptionError = !isDescriptionValid) }
            return
        }

        viewModelScope.launch {
            val newService = Service(
                title = title,
                description = description,
                status = ServiceStatus.PENDING,
                userId = currentUserId.value // AÑADIMOS EL ID DEL USUARIO
            )
            repository.insert(newService)
            // Reset form state after saving
            _formUiState.value = ServiceFormUiState()
        }
    }
}

data class ServiceFormUiState(
    val title: String = "",
    val description: String = "",
    val isTitleError: Boolean = false,
    val isDescriptionError: Boolean = false
)
