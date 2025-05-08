package com.example.albergo.API

data class PrenotazioneRequest(
    val user_id: Int,
    val check_in: String,
    val check_out: String,
    val adulti: Int,
    val bambini: Int,
    val colazione: String,
)
