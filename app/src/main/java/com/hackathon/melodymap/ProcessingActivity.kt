package com.hackathon.melodymap

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class ProcessingActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var doneButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_processing)

        progressBar = findViewById(R.id.progressBar)
        doneButton = findViewById(R.id.doneButton)

        // Simulate processing with a delay
        Handler(Looper.getMainLooper()).postDelayed({
            // Processing complete
            doneButton.isEnabled = true
            doneButton.setBackgroundColor(getColor(R.color.colorPrimary)) // Change button color to green
        }, 5000) // Simulate a 5 second processing time

        doneButton.setOnClickListener {
            if (doneButton.isEnabled) {
                // Navigate to AddDetailsActivity
                val intent = Intent(this, AddDetailsActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
