package cl.duoc.tecexpress.ui.admin

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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
import cl.duoc.tecexpress.viewmodel.AuthViewModel
import coil.compose.SubcomposeAsyncImage

enum class AdminView { SERVICES, USERS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    app: TecExpressApplication,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
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
                TopAppBar(
                    title = { Text("Panel de Administrador") },
                    actions = {
                        IconButton(onClick = {
                            authViewModel.logout()
                            onLogout()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Cerrar Sesión",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
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
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            
            // LOG PARA DEBUG
            LaunchedEffect(service.imageUrl) {
                Log.d("CoilDebug", "Intentando cargar: ${service.imageUrl}")
            }

            SubcomposeAsyncImage(
                model = service.imageUrl ?: "",
                contentDescription = "Imagen",
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop,
                loading = { 
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp)) 
                    }
                },
                error = { 
                    Icon(Icons.Default.ImageNotSupported, null, Modifier.padding(16.dp), tint = Color.Gray)
                }
            )
            
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = "ID: ${service.id} | ${service.serviceType}", style = MaterialTheme.typography.titleMedium, color = Color.Black)
                Text(text = "Usuario ID: ${service.userId}", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                Text(text = "Descripción: ${service.description}", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                Text(text = "Estado: ${service.status}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        Text(
                            text = "Cambiar estado", 
                            modifier = Modifier.clickable { expanded = true },
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            ServiceStatus.entries.forEach { status ->
                                DropdownMenuItem(text = { Text(status.name) }, onClick = { onStatusChange(status); expanded = false })
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { viewModel.startEditing(service) }, modifier = Modifier.height(36.dp), contentPadding = PaddingValues(horizontal = 12.dp)) {
                            Text("Editar", style = MaterialTheme.typography.labelMedium)
                        }
                        Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error), modifier = Modifier.height(36.dp), contentPadding = PaddingValues(horizontal = 12.dp)) {
                            Text("Eliminar", style = MaterialTheme.typography.labelMedium)
                        }
                    }
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
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = uiState.imageUrl, onValueChange = { viewModel.onFormFieldChange(imageUrl = it) }, label = { Text("URL de la Imagen (Firebase)") }, modifier = Modifier.fillMaxWidth())
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
