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
        val digitCount = password.count { it.isDigit() }
        val letterCount = password.count { it.isLetter() }
        val isValid = digitCount >= 3 && letterCount >= 3
        _uiState.update {
            it.copy(
                password = password,
                error = null,
                isPasswordValid = isValid
            )
        }
    }

    fun registerUser() {
        val username = _uiState.value.username
        val password = _uiState.value.password

        if (!Validators.isNotEmpty(username) || !Validators.isNotEmpty(password)) {
            _uiState.update { it.copy(error = "El nombre de usuario y la contraseña no pueden estar vacíos") }
            return
        }

        if (!_uiState.value.isPasswordValid) {
            _uiState.update { it.copy(error = "La contraseña debe tener al menos 3 letras y 3 números") }
            return
        }

        viewModelScope.launch {
            if (repository.findByUsername(username) != null) {
                _uiState.update { it.copy(error = "El nombre de usuario ya existe") }
                return@launch
            }
            
            val passwordHash = password 

            val newUser = UserEntity(username = username, passwordHash = passwordHash)
            repository.insert(newUser)
            _uiState.update { it.copy(registrationSuccess = true) }
        }
    }

    fun loginUser() {
        val username = _uiState.value.username
        val password = _uiState.value.password

        if (!Validators.isNotEmpty(username) || !Validators.isNotEmpty(password)) {
            _uiState.update { it.copy(error = "El nombre de usuario y la contraseña no pueden estar vacíos") }
            return
        }

        viewModelScope.launch {
            val user = repository.findByUsername(username)
            if (user == null) {
                _uiState.update { it.copy(error = "El usuario no existe") }
            } else if (user.passwordHash != password) {
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
    val loginSuccess: Boolean = false,
    val isPasswordValid: Boolean = false
)
