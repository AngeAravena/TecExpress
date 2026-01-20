package cl.duoc.tecexpress.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.tecexpress.TecExpressApplication
import cl.duoc.tecexpress.ui.login.LoginScreen
import cl.duoc.tecexpress.ui.login.RegisterScreen
import cl.duoc.tecexpress.ui.service.ServiceFormScreen
import cl.duoc.tecexpress.ui.service.ServiceScreen
import cl.duoc.tecexpress.ui.splash.SplashScreen

@Composable
fun NavGraph(app: TecExpressApplication) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") { // La app empieza en el login
        composable("splash") {
            SplashScreen(navController = navController)
        }
        composable("login") {
            LoginScreen(
                app = app, // Pasamos la app
                onLogin = { 
                    navController.navigate("service_list") { popUpTo("login") { inclusive = true } } // Navega y limpia el stack
                },
                onRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                app = app, // Pasamos la app
                onRegisterSuccess = { navController.popBackStack() }, // Vuelve al login
                onBackToLogin = { navController.popBackStack() } // Vuelve al login
            )
        }
        composable("service_list") {
            ServiceScreen(
                app = app,
                onAddService = { navController.navigate("service_form") }
            )
        }
        composable("service_form") {
            ServiceFormScreen(
                app = app,
                onSave = { navController.popBackStack() }
            )
        }


    }
}
