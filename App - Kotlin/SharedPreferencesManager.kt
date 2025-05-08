package com.example.albergo

import android.content.Context
import android.content.SharedPreferences
import com.example.albergo.API.PrenotazioneRequest
import com.example.albergo.API.PrenotazioneServizioRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("PrenotazioniPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Salva prenotazioni stanze per un utente specifico
    fun salvaPrenotazioniStanze(userId: Int, prenotazioni: List<PrenotazioneRequest>) {
        val json = gson.toJson(prenotazioni)
        sharedPreferences.edit().putString("prenotazioni_stanze_$userId", json).apply()
    }

    // Recupera prenotazioni stanze per un utente specifico
    fun caricaPrenotazioniStanze(userId: Int): List<PrenotazioneRequest> {
        val json = sharedPreferences.getString("prenotazioni_stanze_$userId", null)
        return if (json != null) {
            val type = object : TypeToken<List<PrenotazioneRequest>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    // Salva prenotazioni servizi per un utente specifico
    fun salvaPrenotazioniServizi(userId: Int, prenotazioni: List<PrenotazioneServizioRequest>) {
        val json = gson.toJson(prenotazioni)
        sharedPreferences.edit().putString("prenotazioni_servizi_$userId", json).apply()
    }

    // Recupera prenotazioni servizi per un utente specifico
    fun caricaPrenotazioniServizi(userId: Int): List<PrenotazioneServizioRequest> {
        val json = sharedPreferences.getString("prenotazioni_servizi_$userId", null)
        return if (json != null) {
            val type = object : TypeToken<List<PrenotazioneServizioRequest>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }
}
