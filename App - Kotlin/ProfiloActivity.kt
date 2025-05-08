package com.example.albergo

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.albergo.API.ApiService
import com.example.albergo.API.RetrofitClient
import com.example.albergo.API.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfiloActivity : AppCompatActivity() {

    private lateinit var textViewNome: TextView
    private lateinit var textViewCognome: TextView
    private lateinit var textViewEmail: TextView
    private lateinit var btnModificaPassword: Button
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profilo)

        // Configura la Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Profilo Utente"

        // Recupera ID utente loggato
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userId = sharedPref.getInt("USER_ID", -1)

        if (userId == -1) {
            Toast.makeText(this, "Errore: ID utente non trovato", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Inizializza i campi della UI
        textViewNome = findViewById(R.id.textViewNome)
        textViewCognome = findViewById(R.id.textViewCognome)
        textViewEmail = findViewById(R.id.textViewEmail)
        btnModificaPassword = findViewById(R.id.btnModificaPassword)

        // Carica i dati utente (offline o online)
        caricaDatiUtente()

        // Imposta l'azione per il pulsante "Modifica Password"
        btnModificaPassword.setOnClickListener {
            mostraDialogModificaPassword()
        }
    }

    private fun caricaDatiUtente() {
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Carica i dati salvati nelle SharedPreferences
        val nomeSalvato = sharedPref.getString("USER_NOME", null)
        val cognomeSalvato = sharedPref.getString("USER_COGNOME", null)
        val emailSalvata = sharedPref.getString("USER_EMAIL", null)

        if (nomeSalvato != null && cognomeSalvato != null && emailSalvata != null) {
            // Se i dati sono salvati, mostra i dati offline
            textViewNome.text = "Nome: $nomeSalvato"
            textViewCognome.text = "Cognome: $cognomeSalvato"
            textViewEmail.text = "Email: $emailSalvata"
        }

        // Prova a recuperare i dati online
        val api = RetrofitClient.instance.create(ApiService::class.java)
        api.getUtente(userId).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val utente = response.body()
                    if (utente != null) {
                        // Aggiorna i campi della UI con le descrizioni
                        textViewNome.text = "Nome: ${utente.nome}"
                        textViewCognome.text = "Cognome: ${utente.cognome}"
                        textViewEmail.text = "Email: ${utente.email}"

                        // Salva i dati offline
                        sharedPref.edit().apply {
                            putString("USER_NOME", utente.nome)
                            putString("USER_COGNOME", utente.cognome)
                            putString("USER_EMAIL", utente.email)
                            apply()
                        }
                    } else {
                        Toast.makeText(this@ProfiloActivity, "Errore nel caricamento del profilo", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ProfiloActivity, "Errore: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@ProfiloActivity, "Errore di connessione: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostraDialogModificaPassword() {
        val dialogView = layoutInflater.inflate(R.layout.modifica_password, null)
        val oldPasswordInput = dialogView.findViewById<EditText>(R.id.editTextOldPassword)
        val newPasswordInput = dialogView.findViewById<EditText>(R.id.editTextNewPassword)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Modifica Password")
            .setView(dialogView)
            .setPositiveButton("Conferma") { _, _ ->
                val oldPassword = oldPasswordInput.text.toString()
                val newPassword = newPasswordInput.text.toString()

                if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                    Toast.makeText(this, "Compila tutti i campi", Toast.LENGTH_SHORT).show()
                } else {
                    modificaPassword(oldPassword, newPassword)
                }
            }
            .setNegativeButton("Annulla", null)
            .create()

        dialog.show()
    }

    private fun modificaPassword(oldPassword: String, newPassword: String) {
        val api = RetrofitClient.instance.create(ApiService::class.java)
        api.modificaPassword(userId, oldPassword, newPassword).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProfiloActivity, "Password modificata con successo", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("ProfiloActivity", "Errore nel cambio password: ${response.errorBody()?.string()}")
                    Toast.makeText(this@ProfiloActivity, "Vecchia password non corretta", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("ProfiloActivity", "Errore di connessione: ${t.message}")
                Toast.makeText(this@ProfiloActivity, "Errore di connessione", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

