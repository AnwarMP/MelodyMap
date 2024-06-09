package com.hackathon.melodymap

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hackathon.melodymap.utils.GeminiProVision
import com.hackathon.melodymap.utils.ResponseCallback

class ProcessingActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ProcessingActivity"
    }

    private var videoFilePath: String? = null
    private var details: String? = null
    private lateinit var geminiProVision: GeminiProVision

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

        geminiProVision = GeminiProVision()

        processVideo()
    }

    private fun processVideo() {
        Log.d(TAG, "Starting video processing")

        val videoProcesser = VideoProcesser()
        videoProcesser.processVideo(this, videoFilePath!!) { frames ->
            if (frames.size >= 4) {
                generateTextPrompt(frames.subList(0, 4), details ?: "")
            } else {
                Log.e(TAG, "Not enough frames extracted from video")
            }
        }
    }

    private fun generateTextPrompt(frames: List<Bitmap>, details: String) {
        val query = "Details: $details\n\nPrompt: Extract as many keywords as possible from the scene and details provided. Using these keywords, find 10 existing songs that match the context of the scene. Provide the song titles in a numbered list format (1. Title, 2. Title, etc.). Ensure the output is clear and concise. ONLY provide the song titles in the given format NO OTHER INFORMATION"
        geminiProVision.getResponse(query, frames[0], frames[1], frames[2], frames[3], object : ResponseCallback {
            override fun onResponse(response: String) {
                Log.d(TAG, "Gemini API Response: $response")
                Toast.makeText(this@ProcessingActivity, "Done processing frames", Toast.LENGTH_SHORT).show()
                // Proceed to next step or update UI
            }

            override fun onError(throwable: Throwable) {
                Log.e(TAG, "Error response from Gemini API", throwable)
                Toast.makeText(this@ProcessingActivity, "Error: ${throwable.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
