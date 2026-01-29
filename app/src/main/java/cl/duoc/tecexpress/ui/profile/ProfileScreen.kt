package cl.duoc.tecexpress.ui.profile

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cl.duoc.tecexpress.viewmodel.AuthViewModel
import coil.compose.SubcomposeAsyncImage
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(authViewModel: AuthViewModel, onBack: () -> Unit) {
    val authState by authViewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            val uri = saveBitmapToInternalStorage(context, it)
            authViewModel.updateProfileImage(authState.currentUser!!.id, uri.toString())
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            takePictureLauncher.launch(null)
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            authViewModel.updateProfileImage(authState.currentUser!!.id, it.toString())
        }
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Cambiar icono", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { 
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                showDialog = false 
                            }
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Cámara", modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("Cámara")
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { pickImageLauncher.launch("image/*"); showDialog = false }
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = "Galería", modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("Galería")
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = authState.currentUser?.username ?: "")
            Spacer(modifier = Modifier.height(16.dp))

            SubcomposeAsyncImage(
                model = authState.currentUser?.profileImageUri,
                contentDescription = "Profile Icon",
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentScale = ContentScale.Crop,
                error = {
                    Image(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Default Profile Icon",
                        modifier = Modifier.size(128.dp)
                    )
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { showDialog = true }) {
                Text(text = "Cambiar icono")
            }
        }
    }
}

private fun saveBitmapToInternalStorage(context: Context, bitmap: android.graphics.Bitmap): Uri {
    val file = File(context.filesDir, "profile_image_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use {
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, it)
    }
    return Uri.fromFile(file)
}
