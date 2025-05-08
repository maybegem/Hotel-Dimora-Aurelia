package com.example.albergo.API


data class User(
    val id: Int,
    val nome: String,   // Campo "nome" dal server
    val cognome: String, // Campo "cognome" dal server
    val email: String   // Campo "email" dal server
)
