package com.example.engler
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
class NetworkReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val batteryLevel = BatteryUtils.getBatteryLevel(context)
        if (NetworkUtils.isConnected(context) && batteryLevel > 50) {
            NotificationHelper(context).showNotification(batteryLevel)
        }
    }

}