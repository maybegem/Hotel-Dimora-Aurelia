
package com.example.albergo.API

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("login") // Indica che il backend accetta richieste POST
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("users") // Indica che il backend accetta richieste GET
    fun getUsers(): Call<List<User>>

    @GET("info_albergo")
    fun getInfoAlbergo(): Call<AlbergoInfo>

    @POST("register")
    fun register(@Body request: RegisterRequest): Call<ApiResponse>

    @GET("/utente/{id}")
    fun getUtente(@Path("id") userId: Int): Call<User>


    @POST("/utente/modificaPassword")
    fun modificaPassword(
        @Query("id") userId: Int,
        @Query("old_password") oldPassword: String,
        @Query("new_password") newPassword: String
    ): Call<Void>


    @POST("prenotazioni")
    fun creaPrenotazione(@Body prenotazione: PrenotazioneRequest): Call<Void>

    @GET("servizi")
    fun getServizi(): Call<ServiziResponse>

        @POST("/prenotazione_servizio")
        fun creaPrenotazioneServizio(@Body prenotazione: PrenotazioneServizioRequest): Call<Void>

    @GET("/prenotazioni/stanze")
    fun getPrenotazioniStanze(
        @Query("user_id") userId: Int
    ): Call<List<PrenotazioneRequest>>

    @GET("/prenotazioni/servizi")
    fun getPrenotazioniServizi(
        @Query("user_id") userId: Int
    ): Call<List<PrenotazioneServizioRequest>>
    
}