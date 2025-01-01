package com.example.engler

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
class MonitoringWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    override fun doWork(): Result {
        val batteryLevel = BatteryUtils.getBatteryLevel(applicationContext)
        if (NetworkUtils.isConnected(applicationContext) && batteryLevel > 50) {
            NotificationHelper(applicationContext).showNotification(batteryLevel)
        }
        return Result.success()
    }
}
