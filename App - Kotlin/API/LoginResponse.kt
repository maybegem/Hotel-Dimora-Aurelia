package com.example.albergo.API

import com.example.albergo.API.User

data class LoginResponse(
    val success: Boolean,   // Indica se il login è riuscito
    val message: String?,   // Messaggio del server (es. errore)
    val user: User?         // Informazioni sull'utente loggato
)
