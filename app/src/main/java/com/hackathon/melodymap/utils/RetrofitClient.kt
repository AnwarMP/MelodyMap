package com.hackathon.melodymap.network

import com.google.gson.GsonBuilder
import com.hackathon.melodymap.spotifyutils.SpotifyApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val SPOTIFY_BASE_URL = "https://api.spotify.com/v1/"

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(SPOTIFY_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val spotifyApiService: SpotifyApiService = retrofit.create(SpotifyApiService::class.java)
}
