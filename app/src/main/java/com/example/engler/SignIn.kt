package com.example.engler

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.engler.data.MyAppDatabase
import com.example.engler.data.entities.User
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.engler.ApiClient


class SignIn : AppCompatActivity() {
    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin)

        // Find views
        val btnBack: ImageButton = findViewById(R.id.btnBack)
        val btnCreateAccount: Button = findViewById(R.id.btnSignIn)
        val username: EditText = findViewById(R.id.etUserName)
        val password: EditText = findViewById(R.id.etPassword)
        val date: EditText = findViewById(R.id.etBirthDate)
        val gender: Spinner = findViewById(R.id.spGender)

        // Set back button behavior
        btnBack.setOnClickListener {
            onBackPressed()
        }

        // Set create account button behavior
        btnCreateAccount.setOnClickListener {
            val userNameText = username.text.toString()
            val passwordText = password.text.toString()
            val birthDateText = date.text.toString()
            val genderText = gender.selectedItem.toString()

            createAccount(userNameText, passwordText, birthDateText, genderText)
        }

        // Update title
        val textView: TextView = findViewById(R.id.tvTitle)
        textView.text = "Hoş Geldiniz!"
    }

    // Validate and create a new account
    private fun createAccount(username: String, password: String, birthDate: String, gender: String) {

        // Validate inputs
        if (username.isEmpty()) {
            showMessage("Please enter a username")
            return
        }

        if (password.isEmpty() || password.length < 6) {
            showMessage("Password must be at least 6 characters")
            return
        }

        if (birthDate.isEmpty()) {
            showMessage("Please enter your birth date")
            return
        }

        // Validate date format "yyyy-MM-dd"
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        try {
            dateFormat.isLenient = false
            val dateParsed = dateFormat.parse(birthDate)
            if (dateParsed == null) {
                showMessage("Invalid birth date format")
                return
            }
        } catch (e: Exception) {
            showMessage("Invalid birth date format")
            return
        }

        // Validate gender selection
        if (gender.isEmpty() || (gender != "male" && gender != "female" && gender != "prefer not to say")) {
            showMessage("Please select a valid gender")
            return
        }

        // Create user object
        val user = User(
            userId = 0,
            username = username,
            email = password, // You may want to store an email separately, but using password here as a placeholder // Ensure that the User entity has a password field
            totalScore = 0,
            createdAt = System.currentTimeMillis(),
            jwtToken = ""
        )

        //LOCAL REGISTER
        val db = MyAppDatabase.getInstance(applicationContext)
        val userDao = db.userDao

        // Use lifecycleScope for database operation in an Activity
        lifecycleScope.launch {
            userDao.insertUser(user)

            showMessage("Account created successfully!")

            super.onBackPressed()
        }

        //CRUD REGISTER

        val baseUrl = "https://api.ifisnot.com"
        val apiClient = ApiClient()


        CoroutineScope(Dispatchers.IO).launch {
            // Register
            val registerResult = apiClient.registerUser(
                "$baseUrl/register",
                username,
                "${username}@example.com",
                password,

            )
            Log.d("Register Result", registerResult)
            withContext(Dispatchers.Main) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SignIn, "Register: $registerResult", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Helper function to show a toast message
    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Override the back button behavior if necessary

}
