package com.example.e4_a1

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.e4_a1.ui.screens.DashboardScreen
import com.example.e4_a1.ui.screens.PermissionsScreen
import com.example.e4_a1.ui.theme.E4A1Theme
import com.example.e4_a1.ui.viewmodel.DrivingViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            E4A1Theme {
                SafeDriveApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeDriveApp(viewModel: DrivingViewModel = viewModel()) {
    var showPermissionsScreen by remember { mutableStateOf(true) }
    val monitoringState by viewModel.monitoringState.collectAsStateWithLifecycle()
    
    // Verificar permisos al iniciar
    LaunchedEffect(Unit) {
        viewModel.checkSystemStatus()
        showPermissionsScreen = !monitoringState.hasLocationPermission
    }
    
    if (showPermissionsScreen && !monitoringState.hasLocationPermission) {
        PermissionsScreen(
            onPermissionsGranted = {
                showPermissionsScreen = false
                viewModel.checkSystemStatus()
            }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            "SafeDrive Guardian",
                            style = MaterialTheme.typography.titleLarge
                        ) 
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    actions = {
                        IconButton(onClick = { viewModel.checkSystemStatus() }) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Actualizar"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            DashboardScreen(
                state = monitoringState,
                onToggleMonitoring = { viewModel.toggleMonitoring() },
                onResetStats = { viewModel.resetStatistics() },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}