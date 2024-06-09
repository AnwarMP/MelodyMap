package com.hackathon.melodymap

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class PlaylistActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PLAYLIST_URL = "playlist_url"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)

        val playlistUrl = intent.getStringExtra(EXTRA_PLAYLIST_URL)
        val webView = findViewById<WebView>(R.id.webView)
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true

        playlistUrl?.let {
            webView.loadUrl(it)
        }
    }
}
