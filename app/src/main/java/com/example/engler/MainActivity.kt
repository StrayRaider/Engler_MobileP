package com.example.engler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val monitoringManager by lazy { MonitoringManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        monitoringManager.initialize()
    }

    override fun onResume() {
        super.onResume()
        monitoringManager.startMonitoring()
    }

    override fun onPause() {
        super.onPause()
        monitoringManager.stopMonitoring()
    }
}

// MonitoringManager.kt
