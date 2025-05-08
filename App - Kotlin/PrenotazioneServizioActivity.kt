package com.example.albergo

import android.net.http.UrlRequest.Status
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.albergo.API.ApiService
import com.example.albergo.API.PrenotazioneServizioRequest
import com.example.albergo.API.RetrofitClient
import com.google.android.material.datepicker.MaterialDatePicker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class PrenotazioneServizioActivity : AppCompatActivity() {

    private lateinit var etDataServizio: EditText
    private lateinit var spinnerOrario: Spinner
    private lateinit var etNumeroPersone: EditText
    private lateinit var tvNomeServizio: TextView
    private lateinit var btnConfermaPrenotazione: Button

    private var dataPrenotazione: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prenotazione_servizio)

        // Recupera gli elementi dalla view
        etDataServizio = findViewById(R.id.etDataServizio)
        spinnerOrario = findViewById(R.id.spinnerOrario)
        etNumeroPersone = findViewById(R.id.etNumeroPersone)
        tvNomeServizio = findViewById(R.id.tvNomeServizio)
        btnConfermaPrenotazione = findViewById(R.id.btnConfermaPrenotazione)

        // Configura la Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Prenotazione Servizio"

        // Recupera i dati dall'intent
        val nomeServizio = intent.getStringExtra("NOME_SERVIZIO") ?: "Servizio"
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("USER_ID", -1)
        Log.d("PrenotazioneServizioActivity", "Recuperato ID utente dalle SharedPreferences: $userId")

        if (userId == -1) {
            Toast.makeText(this, "Errore: ID utente mancante. Effettua il login.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        tvNomeServizio.text = nomeServizio

        // Imposta le opzioni di orario
        setupOrarioOptions(nomeServizio)

        // Seleziona la data con il DatePicker
        etDataServizio.setOnClickListener { showDatePicker() }

        // Conferma della prenotazione
        btnConfermaPrenotazione.setOnClickListener {
            val data = etDataServizio.text.toString()
            val orario = spinnerOrario.selectedItem.toString()
            val numeroPersone = etNumeroPersone.text.toString().toIntOrNull() ?: 1

            if (data.isEmpty() || orario.isEmpty()) {
                Toast.makeText(this, "Seleziona una data e un orario per la prenotazione", Toast.LENGTH_SHORT).show()
            } else {
                prenotaServizio(userId, nomeServizio, data, orario, numeroPersone)
            }
        }
    }

    private fun setupOrarioOptions(nomeServizio: String) {
        val orari = when (nomeServizio) {
            "La Tavola Aurea" -> listOf(
                "12:00", "13:00", "14:00",
                "19:00", "20:00", "21:00"
            )
            "La Sala Massaggi" -> (8..21).map { "$it:00" }
            "Escursione sulle colline" -> listOf("10:00", "17:00")
            else -> listOf("Nessun orario disponibile")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, orari)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerOrario.adapter = adapter
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Seleziona la data")
            .build()

        datePicker.show(supportFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dataPrenotazione = sdf.format(Date(selection))
            etDataServizio.setText(dataPrenotazione)
        }
    }



    private fun prenotaServizio(userId: Int, nomeServizio: String, data: String, orario: String, numeroPersone: Int) {
        val api = RetrofitClient.instance.create(ApiService::class.java)

        val prenotazioneRequest = PrenotazioneServizioRequest(
            user_id = userId,
            tipo_servizio = nomeServizio,
            data_servizio = data,
            orario = orario,
            numero_persone = numeroPersone,
        )

        api.creaPrenotazioneServizio(prenotazioneRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@PrenotazioneServizioActivity, "Prenotazione effettuata!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@PrenotazioneServizioActivity, "Errore durante la prenotazione", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@PrenotazioneServizioActivity, "Errore di connessione", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
