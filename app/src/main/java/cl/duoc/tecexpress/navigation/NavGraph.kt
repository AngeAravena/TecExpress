package cl.duoc.tecexpress.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.tecexpress.TecExpressApplication
import cl.duoc.tecexpress.ui.admin.AdminScreen
import cl.duoc.tecexpress.ui.login.LoginScreen
import cl.duoc.tecexpress.ui.login.RegisterScreen
import cl.duoc.tecexpress.ui.service.ServiceFormScreen
import cl.duoc.tecexpress.ui.service.ServiceScreen
import cl.duoc.tecexpress.ui.splash.SplashScreen
import cl.duoc.tecexpress.viewmodel.AuthViewModel

@Composable
fun NavGraph(app: TecExpressApplication) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel(factory = app.appContainer.viewModelFactory)

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController = navController)
        }
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLogin = { isAdmin ->
                    val route = if (isAdmin) "admin_screen" else "service_list"
                    navController.navigate(route) { popUpTo("login") { inclusive = true } }
                },
                onRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = { navController.popBackStack() },
                onBackToLogin = { navController.popBackStack() }
            )
        }
        composable("service_list") {
            ServiceScreen(
                app = app,
                authViewModel = authViewModel,
                onAddService = { navController.navigate("service_form") },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable("service_form") {
            ServiceFormScreen(
                app = app,
                onSave = { navController.popBackStack() }
            )
        }
        composable("admin_screen") {
            AdminScreen(
                app = app,
                authViewModel = authViewModel,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
