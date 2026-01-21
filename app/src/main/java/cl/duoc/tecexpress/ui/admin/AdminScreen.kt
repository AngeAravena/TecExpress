package cl.duoc.tecexpress.ui.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.tecexpress.R
import cl.duoc.tecexpress.TecExpressApplication
import cl.duoc.tecexpress.data.local.ServiceCategory
import cl.duoc.tecexpress.data.local.UserEntity
import cl.duoc.tecexpress.model.Service
import cl.duoc.tecexpress.model.ServiceStatus
import cl.duoc.tecexpress.viewmodel.AdminViewModel

enum class AdminView { SERVICES, USERS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(app: TecExpressApplication) {
    val viewModel: AdminViewModel = viewModel(factory = app.appContainer.viewModelFactory)
    val uiState by viewModel.uiState.collectAsState()
    val services by viewModel.allServices.collectAsState()
    val users by viewModel.allUsers.collectAsState()

    var currentView by remember { mutableStateOf(AdminView.SERVICES) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Service?>(null) }
    var serviceToUpdate by remember { mutableStateOf<Service?>(null) }
    var newStatusToUpdate by remember { mutableStateOf<ServiceStatus?>(null) }

    if (showStatusDialog && serviceToUpdate != null && newStatusToUpdate != null) {
        ConfirmationDialog(
            title = "Confirmar Cambio de Estado",
            text = "¿Estás seguro de que quieres modificar el estado de este servicio?",
            onConfirm = {
                viewModel.updateServiceStatus(serviceToUpdate!!, newStatusToUpdate!!)
                showStatusDialog = false
            },
            onDismiss = { showStatusDialog = false }
        )
    }

    showDeleteDialog?.let {
        ConfirmationDialog(
            title = "Confirmar Eliminación",
            text = "¿Estás seguro de que quieres eliminar este servicio? Esta acción no se puede deshacer.",
            onConfirm = { viewModel.deleteService(it); showDeleteDialog = null },
            onDismiss = { showDeleteDialog = null }
        )
    }

    uiState.error?.let {
        ErrorDialog(error = it, onDismiss = { viewModel.clearError() })
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = R.drawable.logo_init), contentDescription = "Background", modifier = Modifier.fillMaxSize().alpha(0.2f), contentScale = ContentScale.Crop)
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF4A148C).copy(alpha = 0.3f)))
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(title = { Text("Panel de Administrador") }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent))
            }
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { currentView = AdminView.SERVICES }) { Text("Ver Servicios") }
                    Button(onClick = { currentView = AdminView.USERS }) { Text("Ver Usuarios") }
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.editingService != null) {
                    CreateOrEditServiceForm(viewModel = viewModel)
                } else if (currentView == AdminView.SERVICES) {
                    Button(onClick = { viewModel.startEditing(Service(id = 0, serviceType = "", description = "", userId = 0L, price = 0.0, category = ServiceCategory.OTHER, status = ServiceStatus.PENDING)) }) {
                        Text("Crear Nuevo Servicio")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                when (currentView) {
                    AdminView.SERVICES -> ServiceList(
                        services = services,
                        viewModel = viewModel,
                        onDeleteRequest = { showDeleteDialog = it },
                        onStatusChangeRequested = { service, newStatus ->
                            serviceToUpdate = service
                            newStatusToUpdate = newStatus
                            showStatusDialog = true
                        }
                    )
                    AdminView.USERS -> UserList(users = users)
                }
            }
        }
    }
}

@Composable
fun ServiceList(services: List<Service>, viewModel: AdminViewModel, onDeleteRequest: (Service) -> Unit, onStatusChangeRequested: (Service, ServiceStatus) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(services) { service ->
            ServiceItem(service = service, viewModel = viewModel, onStatusChange = { onStatusChangeRequested(service, it) }, onDelete = { onDeleteRequest(service) })
        }
    }
}

@Composable
fun UserList(users: List<UserEntity>) {
    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(users) { user -> UserItem(user = user) }
    }
}

@Composable
fun ServiceItem(service: Service, viewModel: AdminViewModel, onStatusChange: (ServiceStatus) -> Unit, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("ID: ${service.id} | Tipo: ${service.serviceType}")
            Text("Usuario ID: ${service.userId}")
            Text("Descripción: ${service.description}")
            Text("Estado: ${service.status}")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box {
                    Text("Cambiar estado", modifier = Modifier.clickable { expanded = true })
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        ServiceStatus.values().forEach { status ->
                            DropdownMenuItem(text = { Text(status.name) }, onClick = { onStatusChange(status); expanded = false })
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { viewModel.startEditing(service) }) { Text("Editar") }
                    Button(onClick = onDelete) { Text("Eliminar") }
                }
            }
        }
    }
}

@Composable
fun UserItem(user: UserEntity) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("ID de Usuario: ${user.id}")
            Text("Nombre: ${user.username}")
            Text(text = if (user.isAdmin) "Rol: Administrador" else "Rol: Usuario")
        }
    }
}

@Composable
fun CreateOrEditServiceForm(viewModel: AdminViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    Column(modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth()) {
        Text(if (uiState.editingService?.id == 0) "Crear Nuevo Servicio" else "Editando Servicio #${uiState.editingService?.id}", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = uiState.serviceType, onValueChange = { viewModel.onFormFieldChange(serviceType = it) }, label = { Text("Tipo de Servicio") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = uiState.description, onValueChange = { viewModel.onFormFieldChange(description = it) }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = uiState.userId, onValueChange = { viewModel.onFormFieldChange(userId = it) }, label = { Text("ID de Usuario") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.saveService() }) { Text("Guardar") }
            Button(onClick = { viewModel.stopEditing() }) { Text("Cancelar") }
        }
    }
}

@Composable
fun ConfirmationDialog(title: String, text: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Confirmar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun ErrorDialog(error: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Error") },
        text = { Text(error) },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Aceptar") } }
    )
}
