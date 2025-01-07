package com.example.engler

import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

class ApiClient {

    private val client = OkHttpClient()
    var jwtToken: String? = ""

      fun makePostRequest(url: String, jsonBody: String): String {
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

      fun makeGetRequest(url: String): String {
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

      fun addWord(url: String, wordTr: String, wordEn: String): String {
        val jsonBody = """
            {
                "word_tr": "$wordTr",
                "word_en": "$wordEn"
            }
        """.trimIndent()

        return makePostRequest(url, jsonBody)
    }

      fun getWords(url: String): String {
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

      fun getUserWords(url: String): String {
        return makeGetRequest(url)
    }

      fun updateWord(url: String, wordTr: String, wordEn: String): String {
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

      fun deleteWord(url: String): String {
        val requestBuilder = Request.Builder()
            .url(url)
            .delete()

        jwtToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        val request = requestBuilder.build()
        return executeRequest(request)
    }

    private   fun executeRequest(request: Request): String {
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

