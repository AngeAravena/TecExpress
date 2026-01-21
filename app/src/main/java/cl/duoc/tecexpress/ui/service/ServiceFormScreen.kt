package cl.duoc.tecexpress.ui.service

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import cl.duoc.tecexpress.viewmodel.ServiceVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceFormScreen(
    app: TecExpressApplication,
    onSave: () -> Unit
) {
    val viewModel: ServiceVM = viewModel(factory = app.appContainer.viewModelFactory)
    val uiState by viewModel.formUiState.collectAsState()
    val serviceTemplates by viewModel.serviceTemplates.collectAsState()

    var serviceExpanded by remember { mutableStateOf(false) }
    var osExpanded by remember { mutableStateOf(false) }
    val osOptions = listOf("Android", "iOS (exclusivo en iPhone)")

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onSave()
            viewModel.onSaveComplete()
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
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Elige tu tipo de servicio técnico") },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
                Text("Tipo de servicio")
                ExposedDropdownMenuBox(
                    expanded = serviceExpanded,
                    onExpandedChange = { serviceExpanded = !serviceExpanded }
                ) {
                    OutlinedTextField(
                        value = uiState.selectedService?.serviceType ?: "Seleccione un servicio",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serviceExpanded) },
                        modifier = Modifier.fillMaxWidth(),
                        isError = uiState.isServiceError
                    )
                    ExposedDropdownMenu(
                        expanded = serviceExpanded,
                        onDismissRequest = { serviceExpanded = false }
                    ) {
                        serviceTemplates.forEach { service ->
                            DropdownMenuItem(
                                text = { Text("${service.serviceType} - $${service.price}") },
                                onClick = {
                                    viewModel.onServiceTypeChange(service)
                                    serviceExpanded = false
                                }
                            )
                        }
                    }
                }

                if (uiState.selectedService?.category == ServiceCategory.BATTERY) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Sistema Operativo del Móvil")
                    ExposedDropdownMenuBox(
                        expanded = osExpanded,
                        onExpandedChange = { osExpanded = !osExpanded }
                    ) {
                        OutlinedTextField(
                            value = uiState.selectedOs ?: "Seleccione un SO",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = osExpanded) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = osExpanded,
                            onDismissRequest = { osExpanded = false }
                        ) {
                            osOptions.forEach { os ->
                                DropdownMenuItem(
                                    text = { Text(os) },
                                    onClick = {
                                        viewModel.onOsChange(os)
                                        osExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.onDescriptionChange(it) },
                    label = { Text("Detalles adicionales (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("Precio Final: $${uiState.finalPrice}")
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.saveService() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Servicio")
                }
            }
        }
    }
}
