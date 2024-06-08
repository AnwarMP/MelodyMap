package com.hackathon.melodymap


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REDIRECT_URI = "melodymap://callback"
        private const val REQUEST_CODE = 1337
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            val builder = AuthorizationRequest.Builder(ConfigManager.getSpotifyClientId(), AuthorizationResponse.Type.TOKEN, REDIRECT_URI)
            builder.setScopes(arrayOf("user-read-private", "playlist-read", "playlist-read-private"))
            val request = builder.build()
            AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    val accessToken = response.accessToken
                    // Save the access token and use it to make API calls
                    SharedPreferencesManager.saveAccessToken(this, accessToken)
                    Toast.makeText(this, "Authentication successful!", Toast.LENGTH_SHORT).show()
                }
                AuthorizationResponse.Type.ERROR -> {
                    // Handle error
                    Toast.makeText(this, "Authentication error: ${response.error}", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Handle other cases
                    Toast.makeText(this, "Authentication cancelled.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null && intent.data != null) {
            val uri = intent.data
            val response = AuthorizationResponse.fromUri(uri)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    val accessToken = response.accessToken
                    // Save the access token and use it to make API calls
                    SharedPreferencesManager.saveAccessToken(this, accessToken)
                    Toast.makeText(this, "Authentication successful!", Toast.LENGTH_SHORT).show()
                }
                AuthorizationResponse.Type.ERROR -> {
                    // Handle error
                    Toast.makeText(this, "Authentication error: ${response.error}", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Handle other cases
                    Toast.makeText(this, "Authentication cancelled.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
