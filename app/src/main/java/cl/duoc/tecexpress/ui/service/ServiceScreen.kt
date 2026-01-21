package cl.duoc.tecexpress.ui.service

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.tecexpress.R
import cl.duoc.tecexpress.TecExpressApplication
import cl.duoc.tecexpress.model.Service
import cl.duoc.tecexpress.viewmodel.AuthViewModel
import cl.duoc.tecexpress.viewmodel.ServiceVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceScreen(
    app: TecExpressApplication,
    authViewModel: AuthViewModel, // Recibe AuthViewModel
    onAddService: () -> Unit
) {
    val viewModel: ServiceVM = viewModel(factory = app.appContainer.viewModelFactory)
    val services by viewModel.allServices.collectAsState()
    val currentUser by authViewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.logo_init),
            contentDescription = "Background",
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.2f), // aplica transparency
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Blue.copy(alpha = 0.1f)))
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Servicios de ${currentUser.currentUser?.username ?: ""}") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onAddService) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar Servicio")
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(services) {
                        service -> ServiceItem(service = service)
                }
            }
        }
    }
}

@Composable
fun ServiceItem(service: Service) {
    Card(modifier = Modifier.padding(8.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = service.serviceType, style = androidx.compose.material3.MaterialTheme.typography.headlineSmall)
            Text(text = service.description, style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
            Text(text = "Precio: $${service.price}", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
            Text(text = "Estado: ${service.status.name}", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
        }
    }
}
