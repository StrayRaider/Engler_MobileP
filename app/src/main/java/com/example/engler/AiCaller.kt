import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class AiCaller {

    private val client = OkHttpClient()

    // Suspend function to make the API request with a custom AI prompt
    suspend fun makeApiRequest(prompt: String): String {
        val url =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyBRsAq9DfkSjc2IQP18HT5CkESnGhQEQno"

        // JSON payload for the request
        val jsonPayload = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt) // Use the input string as the AI prompt
                        })
                    })
                })
            })
        }

        // Create the request body
        val mediaType = "application/json".toMediaType()
        val requestBody = RequestBody.create(mediaType, jsonPayload.toString())

        // Build the request
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        // Execute the request and return the response
        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                // Parse the response JSON and extract the "text" part
                val responseBody = response.body?.string() ?: throw IOException("Empty response body")
                val jsonResponse = JSONObject(responseBody)

                // Extract the text from the nested structure
                val candidates = jsonResponse.optJSONArray("candidates") // Get the "candidates" array
                val content = candidates?.optJSONObject(0)?.optJSONObject("content") // Get the "content" object
                val parts = content?.optJSONArray("parts") // Get the "parts" array
                val text = parts?.optJSONObject(0)?.optString("text") // Extract the "text" field
                text ?: throw IOException("Response does not contain 'text' field")
            } else {
                throw IOException("Request failed with code: ${response.code}")
            }
        }
    }
}
