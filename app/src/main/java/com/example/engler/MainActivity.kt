package com.example.engler

import com.example.engler.AiCaller
import JwtStorage
import com.example.engler.data.viewmodel.WordsViewModel
import com.example.engler.data.MyAppDatabase
import com.example.engler.data.factory.WordsViewModelFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.content.Intent
import android.util.Log
import android.widget.EditText

import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.EdgeEffectFactory.EdgeDirection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val monitoringManager by lazy { MonitoringManager(this) }
    private val viewModel: WordsViewModel by viewModels {
        WordsViewModelFactory(MyAppDatabase.getInstance(applicationContext).wordDao)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        monitoringManager.initialize()
        // Set up observers
        setupObservers()
        
        val userName = findViewById<EditText>(R.id.etMail)
        val password = findViewById<EditText>(R.id.etPassword)
        val submitButton = findViewById<Button>(R.id.btnSubmit)
        val signInBtn = findViewById<Button>(R.id.btnSignIn)


        // Create an instance of JwtStorage
        val jwtStorage = JwtStorage(this)

        // Save a JWT token
        val jwtToken = "emre"
        jwtStorage.saveJwtToken(jwtToken)

        // Retrieve and log the JWT token
        val retrievedToken = jwtStorage.getJwtToken()
        println("JWT Token: $retrievedToken")

        // Clear the token if needed
        jwtStorage.clearJwtToken()

        val loginUser = Login(this, submitButton, userName, password, signInBtn,this)





        loginUser.setupButtonClickListener()
        loginUser.signInButtonClickListener()

    }

    override fun onResume() {
        super.onResume()
        monitoringManager.startMonitoring()
     

    }

    private fun setupObservers() {
        viewModel.wordsString.observe(this, Observer { words -> })
    }

    override fun onPause() {
        super.onPause()
        monitoringManager.stopMonitoring()
    }
}

// MonitoringManager.kt
