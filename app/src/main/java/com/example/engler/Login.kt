package com.example.engler

import android.content.Context
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity

class Login (private val context:Context, private val button:Button, private val userName: EditText,private val password: EditText,private val btnSignIn: Button){

    fun checkUser():Boolean {
        var isUser: Boolean= false
        if (userName.text.toString()== "elif" && password.text.toString()=="123"){
            isUser= true;
        }
        return isUser
    }
    fun setupButtonClickListener(){
        button.setOnClickListener{
            if(checkUser() == true){
                //kullanıcı girişi olacak
                val intent= Intent(context,WordList::class.java)
                context.startActivity(intent)
            }else{
                button.text="try again"
            }
        }
    }

    fun signInButtonClickListener(){
        btnSignIn.setOnClickListener{
            val intent = Intent(context, SignIn::class.java)
            context.startActivity(intent)
        }
    }


}