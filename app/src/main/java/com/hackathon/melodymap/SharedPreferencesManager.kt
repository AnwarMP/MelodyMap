package com.hackathon.melodymap

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesManager {

    private const val PREF_NAME = "SpotifyPrefs"
    private const val ACCESS_TOKEN = "AccessToken"
    private const val REFRESH_TOKEN = "RefreshToken"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveAccessToken(context: Context, token: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(ACCESS_TOKEN, token)
        editor.apply()
    }

    fun getAccessToken(context: Context): String? {
        return getSharedPreferences(context).getString(ACCESS_TOKEN, null)
    }

    fun saveRefreshToken(context: Context, token: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(REFRESH_TOKEN, token)
        editor.apply()
    }

    fun getRefreshToken(context: Context): String? {
        return getSharedPreferences(context).getString(REFRESH_TOKEN, null)
    }

    fun clearTokens(context: Context) {
        val editor = getSharedPreferences(context).edit()
        editor.remove(ACCESS_TOKEN)
        editor.remove(REFRESH_TOKEN)
        editor.apply()
    }
}
