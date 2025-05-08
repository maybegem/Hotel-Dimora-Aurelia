package com.example.albergo

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.albergo.API.ApiService
import com.example.albergo.API.RegisterRequest
import com.example.albergo.API.ApiResponse
import com.example.albergo.API.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // Configura la Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Abilita il pulsante "indietro"
        supportActionBar?.title = "Registrazione"

        // Inizializza i campi della UI
        val nomeField = findViewById<EditText>(R.id.etNome)
        val cognomeField = findViewById<EditText>(R.id.etCognome)
        val emailField = findViewById<EditText>(R.id.etEmail)
        val passwordField = findViewById<EditText>(R.id.etPassword)
        val registerButton = findViewById<Button>(R.id.btnRegister)

        // Azione del bottone Registrati
        registerButton.setOnClickListener {
            val nome = nomeField.text.toString().trim() //rimuovono gli spazi bianchi
            val cognome = cognomeField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Tutti i campi sono obbligatori", Toast.LENGTH_SHORT).show()
            } else {
                performRegistration(nome, cognome, email, password)
            }
        }
    }

    // Funzione per gestire la registrazione con API
    private fun performRegistration(nome: String, cognome: String, email: String, password: String) {
        val api = RetrofitClient.instance.create(ApiService::class.java)
        val request = RegisterRequest(nome, cognome, email, password)

        api.register(request).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.success == true) {
                        Toast.makeText(this@RegisterActivity, "Registrazione completata con successo!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@RegisterActivity, apiResponse?.message ?: "Errore durante la registrazione", Toast.LENGTH_SHORT).show()
                    }
                } 
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("RegisterError", "Errore di connessione: ${t.message}")
                Toast.makeText(this@RegisterActivity, "Errore di connessione: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Gestione della navigazione indietro dalla Toolbar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

