package com.example.albergo

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.albergo.API.ApiService
import com.example.albergo.API.PrenotazioneRequest
import com.example.albergo.API.RetrofitClient
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class NuovaPrenotazioneActivity : AppCompatActivity() {

    private lateinit var etPeriodo: EditText
    private lateinit var spinnerAdulti: Spinner
    private lateinit var spinnerBambini: Spinner
    private lateinit var spinnerColazione: Spinner

    private var checkInDate: String? = null
    private var checkOutDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuova_prenotazione)

        // Recupera l'ID utente dalle SharedPreferences
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("USER_ID", -1)

        if (userId == -1) {
            Toast.makeText(this, "Errore: ID utente non trovato. Effettua il login.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configura la Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  // Abilita il pulsante indietro
        supportActionBar?.title = "Informazioni sull'Albergo"

        etPeriodo = findViewById(R.id.etPeriodo)
        spinnerAdulti = findViewById(R.id.spinnerAdulti)
        spinnerBambini = findViewById(R.id.spinnerBambini)
        spinnerColazione = findViewById(R.id.spinnerColazione)
        val btnPrenota = findViewById<Button>(R.id.btnPrenota)

        // Configura gli Spinner
        val numeri = (0..10).toList()
        val adapterNumeri = ArrayAdapter(this, android.R.layout.simple_spinner_item, numeri)
        adapterNumeri.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAdulti.adapter = adapterNumeri
        spinnerBambini.adapter = adapterNumeri

        // Spinner per Colazione
        val colazioneOptions = listOf("SI", "NO")
        val adapterColazione = ArrayAdapter(this, android.R.layout.simple_spinner_item, colazioneOptions)
        adapterColazione.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerColazione.adapter = adapterColazione

        // Gestione selezione del periodo (Check-In e Check-Out)
        etPeriodo.setOnClickListener {
            showDateRangePicker()
        }

        // Click su Prenota
        btnPrenota.setOnClickListener {
            if (checkInDate.isNullOrEmpty() || checkOutDate.isNullOrEmpty()) {
                Toast.makeText(this, "Seleziona il periodo per la prenotazione", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Log.d("NuovaPrenotazioneActivity", "Invio richiesta di prenotazione")
            val numAdulti = spinnerAdulti.selectedItem as Int
            val numBambini = spinnerBambini.selectedItem as Int
            val colazione = spinnerColazione.selectedItem.toString()

            // Passa l'ID utente alla funzione di prenotazione
            creaPrenotazione(userId, checkInDate!!, checkOutDate!!, numAdulti, numBambini, colazione)
        }
    }

    private fun showDateRangePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()

        // Configura il selettore di date
        builder.setTitleText("Seleziona il periodo")
        val constraints = CalendarConstraints.Builder().build()
        builder.setCalendarConstraints(constraints)

        val datePicker = builder.build()
        datePicker.show(supportFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            checkInDate = sdf.format(selection.first)  // Data di Check-In
            checkOutDate = sdf.format(selection.second) // Data di Check-Out
            etPeriodo.setText("$checkInDate - $checkOutDate")
        }
    }

    private fun creaPrenotazione(userId: Int, checkIn: String, checkOut: String, numAdulti: Int, numBambini: Int, colazione: String) {
        val api = RetrofitClient.instance.create(ApiService::class.java)

        val prenotazione = PrenotazioneRequest(
            user_id = userId,
            check_in = checkIn,
            check_out = checkOut,
            adulti = numAdulti,
            bambini = numBambini,
            colazione = colazione,
        )

        api.creaPrenotazione(prenotazione).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@NuovaPrenotazioneActivity, "Prenotazione creata con successo!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@NuovaPrenotazioneActivity, "Errore durante la prenotazione", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@NuovaPrenotazioneActivity, "Errore di connessione: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Metodo per gestire il click sul pulsante indietro nella Toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()  // Chiude l'Activity e torna indietro
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

