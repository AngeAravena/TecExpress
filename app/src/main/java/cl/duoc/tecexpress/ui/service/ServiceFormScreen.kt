package cl.duoc.tecexpress.ui.service

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.tecexpress.TecExpressApplication
import cl.duoc.tecexpress.viewmodel.ServiceVM // <-- ESTE ES EL IMPORT QUE FALTABA

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceFormScreen(
    app: TecExpressApplication,
    onSave: () -> Unit
) {
    val viewModel: ServiceVM = viewModel(factory = app.appContainer.viewModelFactory)
    val uiState by viewModel.formUiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Nuevo Servicio") })
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.onTitleChange(it) },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.isTitleError,
                supportingText = { if (uiState.isTitleError) Text("El título no puede estar vacío") else null },
                trailingIcon = { if (uiState.isTitleError) Icon(Icons.Filled.Error, "Error", tint = Color.Red) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.onDescriptionChange(it) },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.isDescriptionError,
                supportingText = { if (uiState.isDescriptionError) Text("La descripción no puede estar vacía") else null },
                trailingIcon = { if (uiState.isDescriptionError) Icon(Icons.Filled.Error, "Error", tint = Color.Red) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.saveService()
                    // Solo navegamos hacia atrás si la validación fue exitosa
                    // Esto lo podríamos mejorar más adelante observando un evento desde el VM
                    if (uiState.title.isNotEmpty() && uiState.description.isNotEmpty()) {
                        onSave()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}
