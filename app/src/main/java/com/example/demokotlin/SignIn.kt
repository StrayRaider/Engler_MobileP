package com.example.demokotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class SignIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin)

        // Geri tuşu butonunu buluyoruz
        val btnBack: ImageButton = findViewById(R.id.btnBack)
        val btnCreateAccount: Button=findViewById(R.id.btnSignIn)
        val username: EditText=findViewById(R.id.etUserName)
        val password:EditText= findViewById(R.id.etPassword)
        val date:EditText=findViewById(R.id.etBirthDate)
        val gender: Spinner = findViewById(R.id.spGender)

        // Geri tuşuna tıklandığında önceki sayfaya dönüyoruz
        btnBack.setOnClickListener {
            onBackPressed()  // Geri tuşuna basma davranışını tetikler
        }

        btnCreateAccount.setOnClickListener {
            createAccount(btnCreateAccount,username.text.toString(),password.text.toString(),date.text.toString(),gender.selectedItem.toString())
        }

        // Diğer işlemler
        val textView: TextView = findViewById(R.id.tvTitle)
        textView.text = "Hoş Geldiniz!"
    }

    // Geri tuşu davranışını override ediyorsak, super.onBackPressed() çağrısını unutmuyoruz
    override fun onBackPressed() {
        super.onBackPressed()  // Varsayılan geri tuşu davranışını çağırıyoruz
    }

    fun createAccount(button: Button, username: String, password: String, birthDate: String, gender: String) {
        // Validate the inputs

        // Check if username is not empty
        if (username.isEmpty()) {
            button.text = "Please enter a username"
            return
        }

        // Check if password is not empty and meets length requirements (e.g., at least 6 characters)
        if (password.isEmpty() || password.length < 6) {
            button.text = "Password must be at least 6 characters"
            return
        }

        // Check if the birthDate is in a valid date format (e.g., "yyyy-MM-dd")
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateFormat.isLenient = false
        try {
            // Attempt to parse the birthDate string into a Date object
            val date = dateFormat.parse(birthDate)
            if (date == null) {
                button.text = "Invalid birth date format"
                return
            }
        } catch (e: Exception) {
            button.text = "Please enter a valid birth date (dd/MM/yyyy)"
            return
        }

        // Check if gender is selected (assuming gender can be "male", "female", or "prefer not to say")
        if (gender.isEmpty() || (gender != "male" && gender != "female" && gender != "prefer not to say")) {
            button.text = "Please select a valid gender"
            return
        }

        // If all validations pass, update button text to indicate account creation
        button.text = "Account Created"
        onBackPressed()
    }
}
