package com.example.albergo.API

data class RegisterRequest(
    val nome: String,
    val cognome: String,
    val email: String,
    val password: String
)
