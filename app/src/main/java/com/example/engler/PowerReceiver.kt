package com.example.engler
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
class PowerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val batteryLevel = BatteryUtils.getBatteryLevel(intent)
        if (batteryLevel > 50 && NetworkUtils.isConnected(context)) {
            NotificationHelper(context).showNotification(batteryLevel)
        }
    }
}