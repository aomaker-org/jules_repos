package com.example.sunlightdisplaytuner.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import java.util.Locale

class BatteryTemperatureReceiver(
    private val onTemperatureChanged: (String) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
            val tempExtra = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
            if (tempExtra != -1) {
                val temperatureCelsius = tempExtra / 10.0f
                // val temperatureFahrenheit = temperatureCelsius * 9 / 5 + 32
                // For this app, Celsius is likely fine, or make it a setting later.
                val formattedTemperature = String.format(Locale.getDefault(), "%.1f°C", temperatureCelsius)
                onTemperatureChanged(formattedTemperature)
            } else {
                onTemperatureChanged("N/A")
            }
        }
    }
}
