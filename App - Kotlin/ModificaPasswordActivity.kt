package com.example.albergo

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.albergo.API.ApiService
import com.example.albergo.API.RetrofitClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModificaPasswordActivity : AppCompatActivity() {

    private lateinit var editTextOldPassword: EditText
    private lateinit var editTextNewPassword: EditText
    private lateinit var buttonConfermaModifica: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.modifica_password)


        // Inizializza i componenti
        editTextOldPassword = findViewById(R.id.editTextOldPassword)
        editTextNewPassword = findViewById(R.id.editTextNewPassword)

        // Aggiungi il listener al bottone
        buttonConfermaModifica.setOnClickListener {
            val oldPassword = editTextOldPassword.text.toString()
            val newPassword = editTextNewPassword.text.toString()

            if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Riempire tutti i campi", Toast.LENGTH_SHORT).show()
            } else {
                modificaPassword(oldPassword, newPassword)
            }
        }
    }

    private fun modificaPassword(oldPassword: String, newPassword: String) {
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("USER_ID", -1)

        if (userId == -1) {
            Toast.makeText(this, "Errore: ID utente non trovato", Toast.LENGTH_SHORT).show()
            return
        }

        val api = RetrofitClient.instance.create(ApiService::class.java)
        api.modificaPassword(userId, oldPassword, newPassword).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ModificaPasswordActivity, "Password modificata con successo", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Log.e("ModificaPasswordActivity", "Errore codice: ${response.code()}, messaggio: ${response.errorBody()?.string()}")

                    if (response.code() == 401) {
                        Toast.makeText(this@ModificaPasswordActivity, "La vecchia password non è corretta", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ModificaPasswordActivity, "Errore durante la modifica della password", Toast.LENGTH_SHORT).show()
                    }
                }
            }



            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("ModificaPasswordActivity", "Errore di connessione: ${t.message}")
            }

        })
    }



    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
