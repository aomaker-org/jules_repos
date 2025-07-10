package com.example.sunlightdisplaytuner.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext

object PermissionUtils {

    fun canWriteSettings(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.System.canWrite(context)
        } else {
            // On older versions, if permission is in manifest, it's granted at install.
            // However, WRITE_SETTINGS is special and usually needs this check anyway.
            // For simplicity, assume true for <M if declared, but modern Android is >= M.
            true // Or a more robust check if targeting very old APIs
        }
    }

    fun requestWriteSettingsPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:${context.packageName}")
            // It's important to check if activity can handle this intent
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                // Handle case where settings screen is not available (highly unlikely)
            }
        }
        // For older versions, this permission is granted at install time if declared in Manifest.
    }
}

/**
 * A Composable function to manage the WRITE_SETTINGS permission request lifecycle.
 * It checks for permission and triggers a side effect to request it if not granted.
 *
 * @param onPermissionResult Callback invoked with `true` if permission is granted (or already was),
 *                           `false` if the user navigates back from settings without granting.
 *                           Note: This callback might be tricky to get a direct result from
 *                           ACTION_MANAGE_WRITE_SETTINGS as it doesn't use a standard activity result.
 *                           It's better to re-check canWriteSettings() when the app regains focus.
 */
@Composable
fun ManageWriteSettingsPermission(
    initiallyGranted: Boolean,
    onPermissionCheckCompleted: (Boolean) -> Unit
) {
    val context = LocalContext.current

    // This launcher is for starting an activity, not getting a direct result like other permissions.
    // After returning from the settings screen, the app should re-check Settings.System.canWrite(context).
    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        // This block is called when the user returns from the settings activity.
        // We should re-check the permission status here.
        onPermissionCheckCompleted(PermissionUtils.canWriteSettings(context))
    }

    // If permission is not granted, launch the settings screen.
    // SideEffect ensures this is run after composition.
    SideEffect {
        if (!initiallyGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                if (intent.resolveActivity(context.packageManager) != null) {
                    settingsLauncher.launch(intent)
                } else {
                    // Unable to launch settings, treat as permission not granted
                    onPermissionCheckCompleted(false)
                }
            } else {
                // For API < 23, if declared, it's granted.
                // However, canWriteSettings is the definitive check.
                onPermissionCheckCompleted(PermissionUtils.canWriteSettings(context))
            }
        }
    }
}
