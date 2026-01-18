package cl.duoc.tecexpress.ui.service

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.tecexpress.TecExpressApplication
import cl.duoc.tecexpress.model.Service
import cl.duoc.tecexpress.viewmodel.ServiceVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceScreen(
    app: TecExpressApplication,
    onAddService: () -> Unit
) {
    val viewModel: ServiceVM = viewModel(factory = app.appContainer.viewModelFactory)
    val services by viewModel.allServices.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Servicios Técnicos TecExpress") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddService) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar Servicio")
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(services) {
                service -> ServiceItem(service = service)
            }
        }
    }
}

@Composable
fun ServiceItem(service: Service) {
    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = service.title, style = androidx.compose.material3.MaterialTheme.typography.headlineSmall)
            Text(text = service.description, style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
            Text(text = "Estado: ${service.status.name}", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
        }
    }
}