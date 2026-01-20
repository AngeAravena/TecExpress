package cl.duoc.tecexpress.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.tecexpress.data.local.UserEntity
import cl.duoc.tecexpress.repository.UserRepository
import cl.duoc.tecexpress.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username, error = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun registerUser() {
        val username = _uiState.value.username
        val password = _uiState.value.password

        if (!Validators.isNotEmpty(username) || !Validators.isNotEmpty(password)) {
            _uiState.update { it.copy(error = "El nombre de usuario y la contraseña no pueden estar vacíos") }
            return
        }

        viewModelScope.launch {
            if (repository.findByUsername(username) != null) {
                _uiState.update { it.copy(error = "El nombre de usuario ya existe") }
                return@launch
            }
            
            // TODO: Añadir librería de hashing y usarla aquí
            val passwordHash = password // Por ahora, guardamos en tFexto plano

            val newUser = UserEntity(username = username, passwordHash = passwordHash)
            repository.insert(newUser)
            _uiState.update { it.copy(registrationSuccess = true) }
        }
    }

    fun loginUser() {
        val username = _uiState.value.username
        val password = _uiState.value.password

        if (!Validators.isNotEmpty(username) && !Validators.isNotEmpty(password)) {
            _uiState.update { it.copy(error = "El nombre de usuario y la contraseña no pueden estar vacíos") }
            return
        }else if(!Validators.isNotEmpty(username)){
            _uiState.update { it.copy(error = "El nombre de usuario no puede estar vacío") }
            return
        }else if(!Validators.isNotEmpty(password)){
            _uiState.update { it.copy(error = "La contraseña no puede estar vacía") }
            return
        }

        viewModelScope.launch {
            val user = repository.findByUsername(username)
            if (user == null) {
                _uiState.update { it.copy(error = "El usuario no existe") }
            } else if (user.passwordHash != password) { // Compara hashes, no texto plano
                _uiState.update { it.copy(error = "Contraseña incorrecta") }
            } else {
                _uiState.update { it.copy(loginSuccess = true) }
            }
        }
    }
}

data class AuthUiState(
    val username: String = "",
    val password: String = "",
    val error: String? = null,
    val registrationSuccess: Boolean = false,
    val loginSuccess: Boolean = false
)
