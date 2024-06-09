package com.hackathon.melodymap

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProcessingActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ProcessingActivity"
    }

    private var videoFilePath: String? = null
    private var details: String? = null

    private lateinit var doneButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_processing)

        Log.d(TAG, "onCreate called")

        doneButton = findViewById(R.id.doneButton)
        progressBar = findViewById(R.id.progressBar)

        videoFilePath = intent.getStringExtra("VIDEO_FILE_PATH")
        details = intent.getStringExtra("DETAILS")

        if (videoFilePath == null) {
            Log.e(TAG, "No video file path provided in intent")
            Toast.makeText(this, "No video file path provided", Toast.LENGTH_SHORT).show()
            finish()
        }

        Log.d(TAG, "Video file path: $videoFilePath")
        Log.d(TAG, "Details: $details")

        // Start processing the video
        processVideo()

        doneButton.setOnClickListener {
            if (doneButton.isEnabled) {
                finish()
            }
        }
    }

    private fun processVideo() {
        Log.d(TAG, "Starting video processing")
        val helper = VideoProcesser()
        helper.processVideo(this, videoFilePath!!) { frames ->
            Log.d(TAG, "Processed ${frames.size} frames")
            Toast.makeText(this, "Done processing frames", Toast.LENGTH_SHORT).show()
            doneButton.isEnabled = true
            doneButton.setBackgroundColor(Color.GREEN)
        }
    }
}
