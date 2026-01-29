package cl.duoc.tecexpress

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duoc.tecexpress.data.UserPreferencesRepository
import cl.duoc.tecexpress.data.local.TecExpressDatabase
import cl.duoc.tecexpress.repository.ServiceRepository
import cl.duoc.tecexpress.repository.UserRepository
import cl.duoc.tecexpress.util.NotificationHelper
import cl.duoc.tecexpress.viewmodel.AdminViewModel
import cl.duoc.tecexpress.viewmodel.AuthViewModel
import cl.duoc.tecexpress.viewmodel.ServiceVM

class AppContainer(private val context: Context) {

    private val database by lazy { TecExpressDatabase.getDatabase(context) }
    private val serviceRepository by lazy { ServiceRepository(database.serviceDao()) }
    private val userRepository by lazy { UserRepository(database.userDao()) }
    private val notificationHelper by lazy { NotificationHelper(context) }
    private val userPreferencesRepository by lazy { UserPreferencesRepository(context) }

    private val authViewModel: AuthViewModel by lazy {
        AuthViewModel(userRepository, userPreferencesRepository)
    }

    val viewModelFactory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(ServiceVM::class.java) -> {
                    ServiceVM(
                        serviceRepository,
                        notificationHelper,
                        userRepository,
                        authViewModel
                    ) as T
                }

                modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                    authViewModel as T
                }

                modelClass.isAssignableFrom(AdminViewModel::class.java) -> {
                    AdminViewModel(
                        serviceRepository,
                        userRepository,
                        notificationHelper,
                        authViewModel
                    ) as T
                }

                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
