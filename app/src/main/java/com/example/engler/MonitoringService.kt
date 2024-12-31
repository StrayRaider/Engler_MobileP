package com.example.engler


import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class MonitoringService : Service() {
    private var batteryLevel: Int = 0
    private var isConnected: Boolean = false

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Update state based on received intents
        intent?.let {
            if (it.hasExtra("battery_level")) {
                batteryLevel = it.getIntExtra("battery_level", 0)
            }
            if (it.hasExtra("is_connected")) {
                isConnected = it.getBooleanExtra("is_connected", false)
            }
        }

        // Check conditions and show notification if needed
        checkConditionsAndNotify()

        return START_STICKY
    }

    private fun checkConditionsAndNotify() {
        if (isConnected && batteryLevel > 50) {
            showNotification()
        }
    }

    private fun showNotification() {
        val intent = Intent(this, QuizActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, "QUIZ_CHANNEL")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Quiz Time!")
            .setContentText("Time to take a quiz!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }
}
