package com.example.albergo.API

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5000/" // così l'emulatore riconosce il pc

    //private const val BASE_URL = "http://127.0.0.1:5000/"

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Converte JSON in oggetti Kotlin
            .build()
    }
}