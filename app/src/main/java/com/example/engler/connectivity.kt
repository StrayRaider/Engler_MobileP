package com.example.engler

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class Connectivity {

    companion object {
        private const val TAG = "Connectivity"
        private const val API_URL = "https://api.ifisnot.com/"

        // Method to check internet connectivity
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnected
        }

        // Method to check connectivity and call the API if connected
        fun checkAndCallApi(context: Context) {
            if (isNetworkAvailable(context)) {
                callApi()
            } else {
                Log.e(TAG, "No internet connection available.")
            }
        }

        // Make API call
        private fun callApi() {
            val client = OkHttpClient()

            // Create the request to call the API
            val request = Request.Builder()
                .url(API_URL)
                .build()

            // Make the API call asynchronously
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "Error calling API: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseText = response.body?.string()
                        Log.d(TAG, "API Response: $responseText")
                    } else {
                        Log.e(TAG, "Request failed with status code: ${response.code}")
                    }
                }
            })
        }
    }
}
