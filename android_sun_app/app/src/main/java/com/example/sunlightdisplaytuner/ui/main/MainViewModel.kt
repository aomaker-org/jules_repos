package com.example.sunlightdisplaytuner.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import android.provider.Settings
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val contentResolver = application.contentResolver

    // Example StateFlow for a piece of data - can be removed if not used
    // private val _greetingText = MutableStateFlow("Hello from ViewModel!")
    // val greetingText: StateFlow<String> = _greetingText.asStateFlow()

    // Brightness state: Stored as a float 0.0f to 1.0f for the slider
    private val _brightnessSliderPosition = MutableStateFlow(0.5f) // Default to 50%
    val brightnessSliderPosition: StateFlow<Float> = _brightnessSliderPosition.asStateFlow()

    // Represents actual system brightness (0-255) for display or direct manipulation if needed
    private val _systemBrightness = MutableStateFlow(128)
    val systemBrightness: StateFlow<Int> = _systemBrightness.asStateFlow()

    private val _isAdaptiveBrightnessEnabled = MutableStateFlow(false)
    val isAdaptiveBrightnessEnabled: StateFlow<Boolean> = _isAdaptiveBrightnessEnabled.asStateFlow()

    private val _deviceTemperature = MutableStateFlow("N/A")
    val deviceTemperature: StateFlow<String> = _deviceTemperature.asStateFlow()

    init {
        loadInitialBrightness()
        loadInitialAdaptiveBrightnessState()
    }

    private fun loadInitialBrightness() {
        viewModelScope.launch {
            try {
                val currentSystemBrightness = Settings.System.getInt(
                    contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS
                )
                _systemBrightness.value = currentSystemBrightness
                _brightnessSliderPosition.value = systemBrightnessToSliderPosition(currentSystemBrightness)
            } catch (e: Settings.SettingNotFoundException) {
                // Handle error - e.g., use a default value
                _systemBrightness.value = 128 // Default fallback
                _brightnessSliderPosition.value = systemBrightnessToSliderPosition(128)
                // Log or inform user if necessary
            }
        }
    }

    fun onBrightnessSliderChanged(sliderValue: Float) {
        _brightnessSliderPosition.value = sliderValue
        val systemValue = sliderPositionToSystemBrightness(sliderValue)
        _systemBrightness.value = systemValue
        // This requires WRITE_SETTINGS permission
        try {
            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, systemValue)
        } catch (e: SecurityException) {
            // Handle lack of permission, e.g., notify UI to prompt user
            // This would typically be checked before calling this method from UI
            // Or, this method could return a boolean indicating success/failure
        }
    }

    private fun systemBrightnessToSliderPosition(systemValue: Int): Float {
        return systemValue / 255.0f
    }

    private fun sliderPositionToSystemBrightness(sliderValue: Float): Int {
        return (sliderValue * 255).toInt().coerceIn(0, 255)
    }

    private fun loadInitialAdaptiveBrightnessState() {
        viewModelScope.launch {
            try {
                val mode = Settings.System.getInt(
                    contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE
                )
                _isAdaptiveBrightnessEnabled.value = mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
            } catch (e: Settings.SettingNotFoundException) {
                _isAdaptiveBrightnessEnabled.value = false // Default to manual if not found
                // Log or inform user
            }
        }
    }

    fun setAdaptiveBrightnessEnabled(enabled: Boolean) {
        // Requires WRITE_SETTINGS permission
        val newMode = if (enabled) {
            Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
        } else {
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        }
        try {
            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, newMode)
            _isAdaptiveBrightnessEnabled.value = enabled
            // If switching to manual, re-fetch current brightness as it might have changed
            if (!enabled) {
                loadInitialBrightness()
            }
        } catch (e: SecurityException) {
            // Handle lack of permission (UI should ideally prevent this call if no permission)
            // Or signal UI to request permission
        }
    }


    fun updateTemperature(temp: String) {
        // Called by BroadcastReceiver in Feature 3
        _deviceTemperature.value = temp
    }

    // Functions to launch system settings - these might just be called from UI directly
    // or via activity/context provided to ViewModel if absolutely necessary.
}
