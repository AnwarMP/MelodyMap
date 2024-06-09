package com.hackathon.melodymap.network

import retrofit2.Call
import retrofit2.http.*

interface SpotifyService {
    @POST("v1/users/{user_id}/playlists")
    fun createPlaylist(
        @Path("user_id") userId: String,
        @Header("Authorization") token: String,
        @Body body: PlaylistRequest
    ): Call<PlaylistResponse>

    @POST("v1/playlists/{playlist_id}/tracks")
    fun addTracksToPlaylist(
        @Path("playlist_id") playlistId: String,
        @Header("Authorization") token: String,
        @Body body: TracksRequest
    ): Call<Void>

    @GET("v1/search")
    fun searchTrack(
        @Query("q") query: String,
        @Query("type") type: String,
        @Header("Authorization") token: String
    ): Call<SearchResponse>
}

data class PlaylistRequest(val name: String, val description: String, val public: Boolean)
data class PlaylistResponse(val id: String)
data class TracksRequest(val uris: List<String>)
data class SearchResponse(val tracks: Tracks)
data class Tracks(val items: List<Track>)
data class Track(val uri: String)
