package com.example.engler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Start the monitoring service after device boot
            Intent(context, MonitoringService::class.java).also { serviceIntent ->
                context.startService(serviceIntent)
            }
        }
    }
}