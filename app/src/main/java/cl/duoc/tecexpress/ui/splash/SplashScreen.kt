package cl.duoc.tecexpress.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import cl.duoc.tecexpress.R
import cl.duoc.tecexpress.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, authViewModel: AuthViewModel) {
    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(key1 = uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            val route = if (uiState.isAdmin) "admin_screen" else "service_list"
            navController.navigate(route) {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            delay(2000) // Wait for auto-login to attempt
            if (!uiState.loginSuccess) { // Check again after delay
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_init),//direccion del logo de la empresa
            contentDescription = "Logo",
            modifier = Modifier.clip(CircleShape)
        )
    }
}
