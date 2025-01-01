package com.example.engler

import android.content.Context
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.engler.data.MyAppDatabase
import kotlinx.coroutines.launch

class Login(
    private val context: Context,
    private val button: Button,
    private val userName: EditText,
    private val password: EditText,
    private val btnSignIn: Button,
    private val lifecycleOwner: LifecycleOwner // Activity veya Fragment'in lifecycleOwner'ını ekleyin
) {

    fun checkUser(): Boolean {
        val db = MyAppDatabase.getInstance(context)
        val userDao = db.userDao
        var isUser = false

        lifecycleOwner.lifecycleScope.launch {
            // Veritabanında kullanıcıyı sorgulama
            val user = userDao.getUserByUsername(userName.text.toString())
            btnSignIn.text=userName.text.toString()+ password.text.toString()
            if (user != null && user.email== password.text.toString()) {
                isUser = true
                showMessage("Login successful!")
            } else {
                showMessage("Invalid username or password!")
            }
        }

        return isUser
    }

    fun setupButtonClickListener() {
        button.setOnClickListener {
            if (checkUser()) {
                // Kullanıcı girişi başarılı
                val intent = Intent(context, WordList::class.java)
                context.startActivity(intent)
            } else {
                button.text = "Try again"
            }
        }
    }

    fun signInButtonClickListener() {
        btnSignIn.setOnClickListener {
            val intent = Intent(context, SignIn::class.java)
            context.startActivity(intent)
        }
    }

    // showMessage fonksiyonu tanımlaması
    private fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
