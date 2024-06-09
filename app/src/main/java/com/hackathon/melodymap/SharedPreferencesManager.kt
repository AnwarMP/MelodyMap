package com.hackathon.melodymap

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesManager {

    private const val PREF_NAME = "SpotifyPrefs"
    private const val ACCESS_TOKEN = "AccessToken"
    private const val REFRESH_TOKEN = "RefreshToken"
    private const val KEY_USERNAME = "KeyUserName"

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

    fun saveUsername(context: Context, username: String) {
        val prefs = getSharedPreferences(context)
        prefs.edit().putString(KEY_USERNAME, username).apply()
    }

    fun getUsername(context : Context) : String? {
        return getSharedPreferences(context).getString(KEY_USERNAME, null)
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
