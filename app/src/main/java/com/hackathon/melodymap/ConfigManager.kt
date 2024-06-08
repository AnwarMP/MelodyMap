package com.hackathon.melodymap

import android.content.Context
import java.util.Properties

object ConfigManager {
    private const val SECRETS_FILE = "secrets.properties"
    private val properties = Properties()

    fun load(context: Context) {
        val inputStream = context.assets.open(SECRETS_FILE)
        properties.load(inputStream)
    }

    fun getSpotifyClientId(): String {
        return properties.getProperty("SPOTIFY_CLIENT_ID") ?: ""
    }
}
