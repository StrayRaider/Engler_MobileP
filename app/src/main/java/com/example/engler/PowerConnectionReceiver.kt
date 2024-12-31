package com.example.engler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager

class PowerConnectionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BATTERY_CHANGED -> {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val batteryPct = level * 100 / scale.toFloat()

                // Update monitoring service
                Intent(context, MonitoringService::class.java).also { serviceIntent ->
                    serviceIntent.putExtra("battery_level", batteryPct.toInt())
                    context.startService(serviceIntent)
                }
            }
        }
    }
}