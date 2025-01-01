package com.example.engler

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

class ApiClient {

    private val client = OkHttpClient()
    private var jwtToken: String? = null

    suspend fun makePostRequest(url: String, jsonBody: String): String {
        val body = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            jsonBody
        )

        val requestBuilder = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("accept", "*/*")
            .addHeader("Content-Type", "application/json")

        jwtToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        val request = requestBuilder.build()
        return executeRequest(request)
    }

    suspend fun makeGetRequest(url: String): String {
        val requestBuilder = Request.Builder()
            .url(url)
            .get()

        jwtToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        val request = requestBuilder.build()
        return executeRequest(request)
    }

    suspend fun registerUser(url: String, username: String, email: String, password: String): String {
        val jsonBody = """
            {
                "username": "$username",
                "email": "$email",
                "password": "$password"
            }
        """.trimIndent()

        return makePostRequest(url, jsonBody)
    }

    suspend fun loginUser(url: String, email: String, password: String): String {
        val jsonBody = """
            {
                "email": "$email",
                "password": "$password"
            }
        """.trimIndent()

        val response = makePostRequest(url, jsonBody)
        if (response.startsWith("{") && response.contains("token")) {
            val token = Regex("\"token\":\"(.*?)\"").find(response)?.groupValues?.get(1)
            jwtToken = token
            Log.d("JWT Token", "Token set: $jwtToken")
        }
        return response
    }

    suspend fun addWord(url: String, wordTr: String, wordEn: String): String {
        val jsonBody = """
            {
                "word_tr": "$wordTr",
                "word_en": "$wordEn"
            }
        """.trimIndent()

        return makePostRequest(url, jsonBody)
    }

    suspend fun getWords(url: String): String {
        return makeGetRequest(url)
    }

    suspend fun addUserWord(url: String, wordId: Int, score: Int, definition: String): String {
        val jsonBody = """
            {
                "word_id": $wordId,
                "score": $score,
                "definition": "$definition"
            }
        """.trimIndent()

        return makePostRequest(url, jsonBody)
    }

    suspend fun getUserWords(url: String): String {
        return makeGetRequest(url)
    }

    suspend fun updateWord(url: String, wordTr: String, wordEn: String): String {
        val jsonBody = """
            {
                "word_tr": "$wordTr",
                "word_en": "$wordEn"
            }
        """.trimIndent()

        val body = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            jsonBody
        )

        val requestBuilder = Request.Builder()
            .url(url)
            .put(body)
            .addHeader("accept", "*/*")
            .addHeader("Content-Type", "application/json")

        jwtToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        val request = requestBuilder.build()
        return executeRequest(request)
    }

    suspend fun deleteWord(url: String): String {
        val requestBuilder = Request.Builder()
            .url(url)
            .delete()

        jwtToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        val request = requestBuilder.build()
        return executeRequest(request)
    }

    private suspend fun executeRequest(request: Request): String {
        return try {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.string() ?: "Empty response"
            } else {
                "Error: ${response.code}"
            }
        } catch (e: Exception) {
            "Exception: ${e.message}"
        }
    }
}

class MainActivity : AppCompatActivity() {

    private val apiClient = ApiClient()

    private fun callAllEndpoints() {
        val baseUrl = "http://10.0.2.2:3000"

        CoroutineScope(Dispatchers.IO).launch {
            // Register
            val registerResult = apiClient.registerUser("$baseUrl/register", "newuser", "newuser@example.com", "password123")
            Log.d("Register Result", registerResult)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Register: $registerResult", Toast.LENGTH_LONG).show()
            }

            // Login
            val loginResult = apiClient.loginUser("$baseUrl/login", "newuser@example.com", "password123")
            Log.d("Login Result", loginResult)
            withContext(Dispatchers.Main) {
                if (loginResult.contains("Error")) {
                    Log.e("Login Error", "Login failed: $loginResult")
                    return@withContext
                } else {
                    Toast.makeText(this@MainActivity, "Login Successful", Toast.LENGTH_LONG).show()
                }
            }

            // Add Word
            val addWordResult = apiClient.addWord("$baseUrl/words", "elma", "apple")
            Log.d("Add Word Result", addWordResult)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Add Word: $addWordResult", Toast.LENGTH_LONG).show()
            }

            // Get Words
            val getWordsResult = apiClient.getWords("$baseUrl/words")
            Log.d("Get Words Result", getWordsResult)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Get Words: $getWordsResult", Toast.LENGTH_LONG).show()
            }

            // Add User Word
            val addUserWordResult = apiClient.addUserWord("$baseUrl/userwords", 1, 5, "A fruit")
            Log.d("Add User Word Result", addUserWordResult)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Add User Word: $addUserWordResult", Toast.LENGTH_LONG).show()
            }

            // Get User Words
            val getUserWordsResult = apiClient.getUserWords("$baseUrl/userwords")
            Log.d("Get User Words Result", getUserWordsResult)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Get User Words: $getUserWordsResult", Toast.LENGTH_LONG).show()
            }

            // Update Word
            val updateWordResult = apiClient.updateWord("$baseUrl/words/1", "armut", "pear")
            Log.d("Update Word Result", updateWordResult)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Update Word: $updateWordResult", Toast.LENGTH_LONG).show()
            }

            // Delete Word
            val deleteWordResult = apiClient.deleteWord("$baseUrl/words/1")
            Log.d("Delete Word Result", deleteWordResult)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Delete Word: $deleteWordResult", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        callAllEndpoints()
    }
}
