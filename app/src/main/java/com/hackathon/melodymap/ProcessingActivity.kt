package com.hackathon.melodymap

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hackathon.melodymap.utils.GeminiPro
import com.hackathon.melodymap.utils.ResponseCallback

class ProcessingActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ProcessingActivity"
    }

    private var videoFilePath: String? = null
    private var details: String? = null
    private lateinit var geminiPro: GeminiPro

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_processing)

        Log.d(TAG, "onCreate called")

        videoFilePath = intent.getStringExtra("VIDEO_FILE_PATH")
        details = intent.getStringExtra("DETAILS")

        if (videoFilePath == null) {
            Log.e(TAG, "No video file path provided in intent")
            Toast.makeText(this, "No video file path provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        Log.d(TAG, "Video file path: $videoFilePath")
        Log.d(TAG, "Details: $details")

        // Initialize the Gemini helper
        geminiPro = GeminiPro()

        // Start processing the video
        processVideo()
    }

    private fun processVideo() {
        Log.d(TAG, "Starting video processing")

        // Create the prompt from video frames and details
        val textPrompt = "Analyze these images and details: $details"

        // Replace with actual logic to extract frames and append to prompt
        // val frames = extractFrames(videoFilePath)
        // for (frame in frames) {
        //     textPrompt += "\nImage: [Base64-encoded image data]"
        // }

        generateTextPrompt(textPrompt)
    }

    private fun generateTextPrompt(prompt: String) {
        geminiPro.getResponse(prompt, object : ResponseCallback {
            override fun onResponse(response: String) {
                Log.d(TAG, "Gemini API Response: $response")
                Toast.makeText(this@ProcessingActivity, "Done processing frames", Toast.LENGTH_SHORT).show()
                // Proceed to next step or update UI
            }

            override fun onError(throwable: Throwable) {
                Log.e(TAG, "Error response from Gemini API: ${throwable.message}")
                Toast.makeText(this@ProcessingActivity, "Error: ${throwable.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
