package com.hackathon.melodymap

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

class PlaylistActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)

        val playlistId = intent.getStringExtra("PLAYLIST_ID")
        val webView = findViewById<WebView>(R.id.playlistWebView)

        if (playlistId != null) {
            val embedUrl = "https://open.spotify.com/embed/playlist/$playlistId"
            webView.settings.javaScriptEnabled = true
            webView.loadUrl(embedUrl)
        }
    }
}
