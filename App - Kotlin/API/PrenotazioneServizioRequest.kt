package com.example.albergo.API


data class PrenotazioneServizioRequest(
    val user_id: Int,
    val tipo_servizio: String,
    val data_servizio: String,
    val orario: String,
    val numero_persone: Int,
)