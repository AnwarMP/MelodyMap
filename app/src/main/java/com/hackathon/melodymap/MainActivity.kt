package com.hackathon.melodymap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hackathon.melodymap.network.RetrofitClient
import com.hackathon.melodymap.spotifyutils.UserProfile
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REDIRECT_URI = "melodymap://callback"
        private const val REQUEST_CODE = 1337
        private const val REQUEST_CODE_PERMISSIONS = 1001
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Request permissions on startup
        if (allPermissionsGranted()) {
            initializeApp()
        } else {
            requestPermissions()
        }

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            performSpotifyLogin()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                initializeApp()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initializeApp() {
        // Permissions are granted, proceed with login
        performSpotifyLogin()
    }

    private fun performSpotifyLogin() {
        val builder = AuthorizationRequest.Builder(
            ConfigManager.getSpotifyClientId(),
            AuthorizationResponse.Type.TOKEN,
            REDIRECT_URI
        )
        builder.setScopes(arrayOf("user-read-private", "playlist-read", "playlist-read-private", "playlist-modify-private", "playlist-modify-public"))
        val request = builder.build()
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request)
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
                    // Navigate to VideoCaptureActivity
                    val videoCaptureIntent = Intent(this, VideoCaptureActivity::class.java)
                    startActivity(videoCaptureIntent)
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

    private fun fetchSpotifyUserProfile(accessToken: String) {
        val spotifyApiService = RetrofitClient.spotifyApiService
        val call = spotifyApiService.getCurrentUserProfile("Bearer $accessToken")
        call.enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                if (response.isSuccessful) {
                    val userProfile = response.body()
                    userProfile?.let {
                        SharedPreferencesManager.saveUsername(this@MainActivity, it.display_name)
                        Log.d("Login Activity", "username: ${it.display_name} ")
                        Toast.makeText(this@MainActivity, "Welcome, ${it.display_name}!", Toast.LENGTH_SHORT).show()
                        // Navigate to VideoCaptureActivity
                        val videoCaptureIntent = Intent(this@MainActivity, VideoCaptureActivity::class.java)
                        startActivity(videoCaptureIntent)
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Failed to fetch user profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error fetching user profile: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
                    Log.d("Login Activity", "Fetching Profile")
                    fetchSpotifyUserProfile(accessToken)
                    Toast.makeText(this, "Authentication successful!", Toast.LENGTH_SHORT).show()
                    // Navigate to VideoCaptureActivity
                    val videoCaptureIntent = Intent(this, VideoCaptureActivity::class.java)
                    startActivity(videoCaptureIntent)
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
