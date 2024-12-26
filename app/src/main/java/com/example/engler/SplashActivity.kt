package com.example.engler

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // This is correct if the XML file exists

        // Wait for 3 seconds and then navigate to MainActivity
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()  // Close the SplashActivity so the user can't go back to it
        }, 3000)  // 3000 milliseconds = 3 seconds
    }
}
