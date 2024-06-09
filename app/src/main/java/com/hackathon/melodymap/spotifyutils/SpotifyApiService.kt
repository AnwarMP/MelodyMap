package com.hackathon.melodymap.spotifyutils

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

data class UserProfile(
    val display_name: String
)

interface SpotifyApiService {
    @GET("me")
    fun getCurrentUserProfile(
        @Header("Authorization") authHeader: String
    ): Call<UserProfile>
}
