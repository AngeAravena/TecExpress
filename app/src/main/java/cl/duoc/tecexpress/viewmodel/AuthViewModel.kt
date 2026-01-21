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

    init {
        createSuperUser()
    }

    private fun createSuperUser() {
        viewModelScope.launch {
            val superuser = repository.findByUsername("pepitogod")
            if (superuser == null) {
                val superuserEntity = UserEntity(
                    username = "pepitogod",
                    passwordHash = "pepitogod123",
                    isAdmin = true
                )
                repository.insert(superuserEntity)
            }
        }
    }

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
        val username = _uiState.value.username.trim().lowercase()
        val password = _uiState.value.password.trim().lowercase()

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

            val newUser = UserEntity(username = username, passwordHash = password)
            repository.insert(newUser)
            _uiState.update { it.copy(registrationSuccess = true) }
        }
    }

    fun onRegistrationComplete() {
        _uiState.update { it.copy(registrationSuccess = false, username = "", password = "", error = null, isPasswordValid = false) }
    }

    fun loginUser() {
        val username = _uiState.value.username.trim().lowercase()
        val password = _uiState.value.password.trim().lowercase()

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
                _uiState.update { it.copy(loginSuccess = true, isAdmin = user.isAdmin, currentUser = user) }
            }
        }
    }

    fun onLoginComplete() {
        _uiState.update { it.copy(loginSuccess = false) }
    }
}

data class AuthUiState(
    val username: String = "",
    val password: String = "",
    val error: String? = null,
    val registrationSuccess: Boolean = false,
    val loginSuccess: Boolean = false,
    val isAdmin: Boolean = false,
    val isPasswordValid: Boolean = false,
    val currentUser: UserEntity? = null
)
