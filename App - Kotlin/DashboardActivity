package com.example.albergo

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Recupera i dati dell'utente da SharedPreferences
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("USER_ID", -1)

        // Chiude la DashBoard in caso l'utente non sia loggato
        if (userId == -1) {
            Toast.makeText(this, "Errore: ID utente non trovato.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Imposta il messaggio di benvenuto
        val welcomeTextView = findViewById<TextView>(R.id.tvWelcome)
        welcomeTextView.text = "Benvenuto!"

        // Pulsanti per le varie sezioni
        val btnPrenotazioni = findViewById<Button>(R.id.btnPrenotazioni)
        val btnNuovaPrenotazione = findViewById<Button>(R.id.btnNuovaPrenotazione)
        val btnServiziExtra = findViewById<Button>(R.id.btnServiziExtra)
        val btnInfoAlbergo = findViewById<Button>(R.id.btnInfoAlbergo)
        val btnProfilo = findViewById<Button>(R.id.btnProfilo)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // Navigazione
        btnPrenotazioni.setOnClickListener {
            val intent = Intent(this, ListaPrenotazioniActivity::class.java)
            startActivity(intent)
        }

        btnNuovaPrenotazione.setOnClickListener {
            val intent = Intent(this, NuovaPrenotazioneActivity::class.java)
            intent.putExtra("USER_ID", userId)  // Passa l'ID utente
            startActivity(intent)
        }

        btnServiziExtra.setOnClickListener {
            val intent = Intent(this, ServiziExtraActivity::class.java)
            intent.putExtra("USER_ID", userId)  // Passa l'ID utente
            startActivity(intent)
        }

        btnInfoAlbergo.setOnClickListener {
            val intent = Intent(this, InfoAlbergoActivity::class.java)
            startActivity(intent)
        }

        btnProfilo.setOnClickListener {
            val intent = Intent(this, ProfiloActivity::class.java)
            startActivity(intent)
        }


        btnLogout.setOnClickListener {
            mostraConfermaLogout(sharedPref)
        }
    }
    // Gestione del pulsante di logout
    private fun mostraConfermaLogout(sharedPref: SharedPreferences) {
        AlertDialog.Builder(this)
            .setTitle("Conferma Logout")
            .setMessage("Sei sicuro di voler effettuare il logout?")
            .setPositiveButton("Sì") { _, _ ->
                effettuaLogout(sharedPref)
            }
            .setNegativeButton("Annulla", null)
            .show()
    }

    private fun effettuaLogout(sharedPref: SharedPreferences) {
        // Cancella i dati salvati nelle SharedPreferences
        sharedPref.edit().clear().apply()

        // Naviga alla schermata di login
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}





