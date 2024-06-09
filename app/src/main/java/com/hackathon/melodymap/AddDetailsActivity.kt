package com.hackathon.melodymap

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddDetailsActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "AddDetailsActivity"
    }

    private lateinit var detailsInput: EditText
    private lateinit var submitButton: Button
    private var videoFilePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_details)

        Log.d(TAG, "onCreate called")

        detailsInput = findViewById(R.id.detailsEditText)
        submitButton = findViewById(R.id.submitButton)

        videoFilePath = intent.getStringExtra("VIDEO_FILE_PATH")
        if (videoFilePath == null) {
            Log.e(TAG, "No video file path provided in intent")
            Toast.makeText(this, "No video file path provided", Toast.LENGTH_SHORT).show()
            finish()
        }

        submitButton.setOnClickListener {
            val details = detailsInput.text.toString()
            if (details.isEmpty()) {
                Toast.makeText(this, "Please enter details", Toast.LENGTH_SHORT).show()
            } else {
                Log.d(TAG, "Details entered: $details")
                val intent = Intent(this, ProcessingActivity::class.java)
                intent.putExtra("VIDEO_FILE_PATH", videoFilePath)
                intent.putExtra("DETAILS", details)
                startActivity(intent)
            }
        }
    }
}
