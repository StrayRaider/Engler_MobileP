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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject




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



        //REST LOGIN
        val baseUrl = "https://api.ifisnot.com"
        val apiClient = ApiClient()

        var token: String =""
        CoroutineScope(Dispatchers.IO).launch {
            val loginResult = apiClient.loginUser("$baseUrl/login", "${userName.text.toString()}@example.com", password.text.toString() )
                         withContext(Dispatchers.Main) {
                            if (loginResult.contains("Error")) {
                                 return@withContext
                             } else {
                                 Toast.makeText(context, "Login Successful", Toast.LENGTH_LONG).show()
                                val jsonObject = JSONObject(loginResult)
                                val token = jsonObject.getString("token")
                                 showMessage(token)
                                 isUser=true
                             }
                        }
        }
        //LOCAL LOGIN
        lifecycleOwner.lifecycleScope.launch {
            // Veritabanında kullanıcıyı sorgulama
            val user = userDao.getUserByUsername(userName.text.toString())

            if (user != null && user.email == password.text.toString()) {
                isUser = true

                if (token != null) {
                    userDao.updateJwt(userName.text.toString(), password.text.toString(), token)
                }

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
