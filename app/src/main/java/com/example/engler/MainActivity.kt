package com.example.engler

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView.EdgeEffectFactory.EdgeDirection

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userName = findViewById<EditText>(R.id.etMail)
        val password = findViewById<EditText>(R.id.etPassword)
        val submitButton = findViewById<Button>(R.id.btnSubmit)
        val signInBtn = findViewById<Button>(R.id.btnSignIn)
        val cameraBtn = findViewById<Button>(R.id.btnCamera)

        val loginUser = Login(this, submitButton, userName, password, signInBtn, cameraBtn)

        loginUser.setupButtonClickListener()
        loginUser.signInButtonClickListener()
        loginUser.cameraButtonClickListener()
    }
}