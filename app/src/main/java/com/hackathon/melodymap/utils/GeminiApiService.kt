package com.hackathon.melodymap.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class GeminiRequest(val contents: List<Part>)
data class Part(val text: String)
data class GeminiResponse(val text: String)

interface GeminiApiService {
    @POST("models/gemini-1.5-flash-latest:generateContent")
    fun generateContent(@Body request: GeminiRequest): Call<GeminiResponse>
}