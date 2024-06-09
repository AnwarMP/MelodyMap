package com.hackathon.melodymap.network

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/"

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val apiKeyInterceptor = ApiKeyInterceptor("AIzaSyBFFrpUl5WIHt79HywrmoQnBJUXjMmGcU8")
    private val client = OkHttpClient.Builder()
        .addInterceptor(apiKeyInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val geminiApiService: GeminiApiService = retrofit.create(GeminiApiService::class.java)
}
