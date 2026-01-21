package cl.duoc.tecexpress.ui.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cl.duoc.tecexpress.R
import cl.duoc.tecexpress.viewmodel.AuthViewModel
import androidx.compose.material3.OutlinedTextFieldDefaults

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.registrationSuccess) {
        if (uiState.registrationSuccess) {
            Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
            onRegisterSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.logo_init),
            contentDescription = "Background",
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.2f),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Blue.copy(alpha = 0.1f)))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Crear Nueva Cuenta", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = uiState.username,
                onValueChange = { viewModel.onUsernameChange(it) },
                label = { Text("Nombre de Usuario") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.error != null && uiState.username.isEmpty()            )
            Spacer(modifier = Modifier.height(16.dp))

            // Lógica de colores para la contraseña
            val isPasswordEmpty = uiState.password.isEmpty()
            val passwordColor = if (isPasswordEmpty) {
                MaterialTheme.colorScheme.outline // Color gris/normal por defecto
            } else if (uiState.isPasswordValid) {
                Color(0xFF4CAF50) // Verde
            }else if (uiState.password.count { it.isDigit() } >= 2 && uiState.password.count { it.isLetter() } >= 2){
                Color(0xFFFBC02D) // Amarillo
            }
            else {
                Color.Red // Rojo
            }


            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Contraseña (min. 3 letras y 3 números)") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = passwordColor,
                    unfocusedBorderColor = passwordColor,
                    focusedLabelColor = passwordColor,
                    unfocusedLabelColor = passwordColor
                )
            )

            uiState.error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(32.dp))


            Button(
                onClick = { viewModel.registerUser() },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isPasswordValid && uiState.username.isNotEmpty()
            ) {
                Text("Registrarse")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onBackToLogin, modifier = Modifier.fillMaxWidth()) {
                Text("Volver a Inicio")
            }
        }
    }
}
