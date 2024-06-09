package com.hackathon.melodymap.utils

import android.content.Context
import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.vertexai.VertexAI
import com.google.cloud.vertexai.generativeai.GenerativeModel
import com.google.cloud.vertexai.generativeai.ResponseHandler
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.Executors

class GeminiCallingHelper(private val context: Context) {

    companion object {
        private const val TAG = "GeminiCallingHelper"
        private const val projectId = "able-door-425901-q8"
        private const val location = "us-central1"
        private const val modelName = "gemini-1.0-pro-002"
    }

    interface GeminiCallback {
        fun onSuccess(result: String)
        fun onError(errorMessage: String)
    }

    fun generateTextPrompt(textPrompt: String, callback: GeminiCallback) {
        Executors.newSingleThreadExecutor().execute {
            try {
                val output: String = textInput(projectId, location, modelName, textPrompt)
                Log.d(TAG, "Generated Text: $output")
                callback.onSuccess(output)
            } catch (e: IOException) {
                Log.e(TAG, "Error generating text: ${e.message}")
                callback.onError(e.message ?: "Unknown error")
            }
        }
    }

    @Throws(IOException::class)
    private fun textInput(projectId: String?, location: String?, modelName: String?, textPrompt: String?): String {
        val credentialsStream: InputStream = context.assets.open("able-door-425901-q8-e8dc0c79e57f.json")
        val googleCredentials = GoogleCredentials.fromStream(credentialsStream)

        VertexAI(projectId, location).use { vertexAI ->
            val model = GenerativeModel(modelName, vertexAI)
            val response = model.generateContent(textPrompt)
            return ResponseHandler.getText(response)
        }
    }
}
