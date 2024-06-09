package com.hackathon.melodymap

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddDetailsActivity : AppCompatActivity() {

    private lateinit var detailsEditText: EditText
    private val PREFS_NAME = "com.hackathon.melodymap"
    private val DETAILS_KEY = "details_key"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_details)

        val homeButton = findViewById<ImageButton>(R.id.homeButton)
        detailsEditText = findViewById(R.id.detailsEditText)
        val submitButton = findViewById<Button>(R.id.submitButton)

        // Load saved details if they exist
        loadDetails()

        homeButton.setOnClickListener {
            finish() // Go back to the previous activity
        }

        submitButton.setOnClickListener {
            val details = detailsEditText.text.toString()
            if (details.isNotBlank()) {
                // Save the details
                saveDetails(details)
                Toast.makeText(this, "Details submitted and saved!", Toast.LENGTH_SHORT).show()

                // Navigate to ProcessingActivity
                val intent = Intent(this, ProcessingActivity::class.java)
                startActivity(intent)

            } else {
                Toast.makeText(this, "Please add some details before submitting.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveDetails(details: String) {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(DETAILS_KEY, details)
        editor.apply()
    }

    private fun loadDetails() {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedDetails = sharedPreferences.getString(DETAILS_KEY, "")
        if (!savedDetails.isNullOrBlank()) {
            detailsEditText.setText(savedDetails)
        }
    }
}
