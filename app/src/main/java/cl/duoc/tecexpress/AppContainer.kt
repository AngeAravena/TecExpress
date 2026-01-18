package cl.duoc.tecexpress

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duoc.tecexpress.data.local.TecExpressDatabase
import cl.duoc.tecexpress.repository.ServiceRepository
import cl.duoc.tecexpress.repository.UserRepository
import cl.duoc.tecexpress.viewmodel.AuthViewModel
import cl.duoc.tecexpress.viewmodel.ServiceVM


class AppContainer(context: Context) {

    private val database = TecExpressDatabase.getDatabase(context)
    private val serviceRepository by lazy { ServiceRepository(database.serviceDao()) }
    private val userRepository by lazy { UserRepository(database.userDao()) }

    val viewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(ServiceVM::class.java) -> {
                    ServiceVM(serviceRepository) as T
                }
                modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                    AuthViewModel(userRepository) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
