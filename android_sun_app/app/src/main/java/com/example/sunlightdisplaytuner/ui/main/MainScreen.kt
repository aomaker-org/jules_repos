package com.example.sunlightdisplaytuner.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sunlightdisplaytuner.R
import com.example.sunlightdisplaytuner.ui.theme.SunlightDisplayTunerTheme
import com.example.sunlightdisplaytuner.utils.PermissionUtils
import kotlinx.coroutines.launch
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Intent
import android.provider.Settings
import android.net.Uri
import android.content.IntentFilter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.Color
import com.example.sunlightdisplaytuner.receiver.BatteryTemperatureReceiver


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(mainViewModel: MainViewModel = viewModel()) {
    // val greetingText by mainViewModel.greetingText.collectAsState() // Removed if not used
    val brightnessSliderPosition by mainViewModel.brightnessSliderPosition.collectAsState()
    val systemBrightness by mainViewModel.systemBrightness.collectAsState() // For display
    val isAdaptiveEnabled by mainViewModel.isAdaptiveBrightnessEnabled.collectAsState()
    val deviceTemperature by mainViewModel.deviceTemperature.collectAsState()

    val context = LocalContext.current
    var hasWriteSettingsPermission by remember { mutableStateOf(PermissionUtils.canWriteSettings(context)) }

    // Temperature Receiver Logic
    DisposableEffect(context, mainViewModel) {
        val receiver = BatteryTemperatureReceiver { tempStr ->
            mainViewModel.updateTemperature(tempStr)
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(receiver, filter)

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        // Callback after returning from settings screen
        hasWriteSettingsPermission = PermissionUtils.canWriteSettings(context)
        if (hasWriteSettingsPermission) {
            // Permission granted, reload initial brightness or apply pending changes
            mainViewModel.loadInitialBrightness() // Re-fetch to ensure UI is consistent
        } else {
            // Permission denied, UI should reflect this (e.g., disable slider)
            Log.w("MainScreen", "WRITE_SETTINGS permission not granted after returning from settings.")
        }
    }

    fun requestPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        settingsLauncher.launch(intent)
    }


    SunlightDisplayTunerTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.app_name)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start, // Changed to Start for better layout
                verticalArrangement = Arrangement.spacedBy(16.dp) // Increased spacing
            ) {
                // Text(text = greetingText) // Removed if not used
                // Spacer(modifier = Modifier.height(10.dp)) // Adjusted spacing

                BrightnessControl(
                    sliderPosition = brightnessSliderPosition,
                    systemBrightness = systemBrightness, // Pass system brightness for display
                    onSliderChange = { newPosition ->
                        if (hasWriteSettingsPermission) {
                            mainViewModel.onBrightnessSliderChanged(newPosition)
                        } else {
                            requestPermission()
                        }
                    },
                    enabled = hasWriteSettingsPermission
                )

                if (!hasWriteSettingsPermission) {
                    PermissionRequestUI(onGrantPermissionClick = { requestPermission() })
                }

                AdaptiveBrightnessControl(
                    isAdaptiveEnabled = isAdaptiveEnabled,
                    onCheckedChange = { enabled ->
                        if (hasWriteSettingsPermission) {
                            mainViewModel.setAdaptiveBrightnessEnabled(enabled)
                        } else {
                            requestPermission() // Request permission if trying to toggle
                        }
                    },
                    enabled = hasWriteSettingsPermission
                )

                DeviceTemperatureDisplay(temperature = deviceTemperature)

                SettingsShortcutButton(
                    buttonText = "Open Developer Options",
                    action = Settings.ACTION_DEVELOPER_SETTINGS
                )

                SettingsShortcutButton(
                    buttonText = "Open Display Color Settings",
                    action = Settings.ACTION_DISPLAY_SETTINGS
                )
            }
        }
    }
}

@Composable
fun AdaptiveBrightnessControl(
    isAdaptiveEnabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Adaptive Brightness")
        Switch(
            checked = isAdaptiveEnabled,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
    if (!enabled) {
        Text(
            "Permission required to change adaptive brightness setting.",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun DeviceTemperatureDisplay(temperature: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Device Temperature: ", style = MaterialTheme.typography.bodyLarge)
        Text(temperature, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun SettingsShortcutButton(buttonText: String, action: String) {
    val context = LocalContext.current
    Button(
        onClick = {
            try {
                val intent = Intent(action)
                // Check if an activity can handle this intent
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    // Optionally: Inform the user that the settings screen cannot be opened.
                    // This can happen if Developer Options are not enabled, for example.
                    Log.w("SettingsShortcut", "No activity found to handle action: $action. Developer options might be disabled.")
                    // You could show a Toast here:
                    // Toast.makeText(context, "Cannot open $buttonText. Ensure Developer Options are enabled.", Toast.LENGTH_LONG).show()
                }
            } catch (e: SecurityException) {
                // This is less likely for ACTION_DEVELOPER_SETTINGS but good practice for general settings intents.
                Log.e("SettingsShortcut", "SecurityException for action: $action", e)
                // Optionally: Inform user about security restriction.
            } catch (e: Exception) {
                // Catch any other unexpected errors during intent launching.
                Log.e("SettingsShortcut", "Exception launching intent for action: $action", e)
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(buttonText)
    }
}

@Composable
fun BrightnessControl(
    sliderPosition: Float,
    systemBrightness: Int, // To display the 0-255 value or percentage
    onSliderChange: (Float) -> Unit,
    enabled: Boolean
) {
    Column(horizontalAlignment = Alignment.Start) {
        Text("Master Brightness: ${ (sliderPosition * 100).toInt() }% (System: $systemBrightness)")
        Slider(
            value = sliderPosition,
            onValueChange = onSliderChange,
            valueRange = 0f..1f,
            steps = 100, // Optional: for discrete steps
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )
        if (!enabled) {
            Text("Permission required to change brightness.", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun PermissionRequestUI(onGrantPermissionClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "This app needs permission to change screen brightness.",
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = onGrantPermissionClick) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Grant")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SunlightDisplayTunerTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            BrightnessControl(sliderPosition = 0.5f, systemBrightness = 128, onSliderChange = {}, enabled = true)
            Spacer(Modifier.height(16.dp))
            AdaptiveBrightnessControl(isAdaptiveEnabled = true, onCheckedChange = {}, enabled = true)
            Spacer(Modifier.height(16.dp))
            BrightnessControl(sliderPosition = 0.7f, systemBrightness = 178, onSliderChange = {}, enabled = false)
            Spacer(Modifier.height(16.dp))
            AdaptiveBrightnessControl(isAdaptiveEnabled = false, onCheckedChange = {}, enabled = false)
            Spacer(Modifier.height(16.dp))
            DeviceTemperatureDisplay(temperature = "25.3°C")
            Spacer(Modifier.height(16.dp))
            SettingsShortcutButton(buttonText = "Preview Developer Options", action = "PREVIEW_ACTION_DEV_OPTIONS")
            Spacer(Modifier.height(16.dp))
            SettingsShortcutButton(buttonText = "Preview Display Settings", action = "PREVIEW_ACTION_DISPLAY_SETTINGS")
            Spacer(Modifier.height(16.dp))
            PermissionRequestUI {}
        }
    }
}
