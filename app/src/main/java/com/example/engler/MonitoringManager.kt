package com.example.engler

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.ComponentActivity
import androidx.work.*
import java.util.concurrent.TimeUnit
class MonitoringManager(private val context: Context) {
    private val powerReceiver = PowerReceiver()
    private val networkReceiver = NetworkReceiver()
    private val notificationHelper = NotificationHelper(context)
    private val permissionHelper = PermissionHelper(context as ComponentActivity)

    fun initialize() {
        notificationHelper.createNotificationChannel()
        permissionHelper.checkAndRequestPermissions { startBackgroundWork() }
    }

    fun startMonitoring() {
        context.registerReceiver(powerReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        context.registerReceiver(networkReceiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
    }

    fun stopMonitoring() {
        try {
            context.unregisterReceiver(powerReceiver)
            context.unregisterReceiver(networkReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startBackgroundWork() {
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "monitoring_work",
            ExistingPeriodicWorkPolicy.UPDATE,
            createWorkRequest()
        )
    }

    private fun createWorkRequest() = PeriodicWorkRequestBuilder<MonitoringWorker>(15, TimeUnit.MINUTES)
        .setConstraints(Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build())
        .build()
}