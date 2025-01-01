package com.example.engler

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class AiCaller : AppCompatActivity() {
    private val client = OkHttpClient()
    private val chatMessages = mutableListOf<String>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chatbot)

        val promptEditText: EditText = findViewById(R.id.promptEditText)
        val btnSend: Button = findViewById(R.id.sendButton)
        val chatRecyclerView: RecyclerView = findViewById(R.id.chatRecyclerView)

        // Set up RecyclerView
        chatAdapter = ChatAdapter(chatMessages)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter

        btnSend.setOnClickListener {
            val prompt = promptEditText.text.toString().trim()
            if (prompt.isNotEmpty()) {
                // Add user's input to chat and clear the EditText
                chatMessages.add("You: $prompt")
                chatAdapter.notifyItemInserted(chatMessages.size - 1)
                chatRecyclerView.scrollToPosition(chatMessages.size - 1)
                promptEditText.text.clear()

                // Make API request and display the response
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val response = makeApiRequest(prompt)
                        chatMessages.add("AI: $response")
                        chatAdapter.notifyItemInserted(chatMessages.size - 1)
                        chatRecyclerView.scrollToPosition(chatMessages.size - 1)
                    } catch (e: Exception) {
                        chatMessages.add("Error: ${e.message}")
                        chatAdapter.notifyItemInserted(chatMessages.size - 1)
                        chatRecyclerView.scrollToPosition(chatMessages.size - 1)
                    }
                }
            }
        }
    }

    // Suspend function to make the API request with a custom AI prompt
    private suspend fun makeApiRequest(prompt: String): String {
        val url =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyBRsAq9DfkSjc2IQP18HT5CkESnGhQEQno"

        val jsonPayload = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
        }

        val mediaType = "application/json".toMediaType()
        val requestBody = RequestBody.create(mediaType, jsonPayload.toString())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: throw IOException("Empty response body")
                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.optJSONArray("candidates")
                val content = candidates?.optJSONObject(0)?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                parts?.optJSONObject(0)?.optString("text") ?: throw IOException("Response does not contain 'text'")
            } else {
                throw IOException("Request failed with code: ${response.code}")
            }
        }
    }
}
