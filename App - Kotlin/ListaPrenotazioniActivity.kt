package com.example.albergo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.albergo.API.*
import com.example.albergo.API.PrenotazioneRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities


class ListaPrenotazioniActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var toggleSwitch: SwitchCompat
    private lateinit var sharedPrefs: SharedPreferencesManager
    private var userId: Int = -1

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (isInternetAvailable()) {
                Toast.makeText(this@ListaPrenotazioniActivity, "Connessione ripristinata! Aggiornamento...", Toast.LENGTH_SHORT).show()
                fetchPrenotazioniStanze()
                fetchPrenotazioniServizi()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_prenotazioni)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Le tue Prenotazioni"

        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userId = sharedPref.getInt("USER_ID", -1)

        if (userId == -1) {
            Toast.makeText(this, "Errore: ID utente non trovato.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        recyclerView = findViewById(R.id.recyclerViewPrenotazioni)
        recyclerView.layoutManager = LinearLayoutManager(this)

        sharedPrefs = SharedPreferencesManager(this)

        toggleSwitch = findViewById(R.id.switchPrenotazioni)
        toggleSwitch.setOnCheckedChangeListener { _, isChecked ->
            aggiornaRecyclerView(isChecked)
        }

        // Registra il listener per la connessione di rete
        registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        aggiornaRecyclerView(toggleSwitch.isChecked)
    }


    private fun aggiornaRecyclerView(mostraServizi: Boolean) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val oggi = sdf.parse(sdf.format(Date()))  // Data di oggi in formato Date

        if (mostraServizi) {
            val prenotazioniServizi = sharedPrefs.caricaPrenotazioniServizi(userId)
                .filter { it.data_servizio.isNotEmpty() }  //  Controllo per evitare errori
                .filter {
                    val dataFinePrenotazione = sdf.parse(it.data_servizio)  //  Usa data_servizio per verificare la fine della prenotazione
                    dataFinePrenotazione != null && !dataFinePrenotazione.before(oggi)  //  Mostra solo quelle future o in corso
                }
            recyclerView.adapter = PrenotazioniServiziAdapter(prenotazioniServizi)
        } else {
            val prenotazioniStanze = sharedPrefs.caricaPrenotazioniStanze(userId)
                .filter { it.check_out.isNotEmpty() }  //  Controllo per evitare errori
                .filter {
                    val dataFinePrenotazione = sdf.parse(it.check_out)  //  Usa check_out per verificare la fine della prenotazione
                    dataFinePrenotazione != null && !dataFinePrenotazione.before(oggi)  //  Mostra solo quelle future o in corso
                }
            recyclerView.adapter = PrenotazioniAdapter(prenotazioniStanze)
        }

        recyclerView.adapter?.notifyDataSetChanged()
    }




    private fun fetchPrenotazioniStanze() {
        val api = RetrofitClient.instance.create(ApiService::class.java)
        api.getPrenotazioniStanze(userId).enqueue(object : Callback<List<PrenotazioneRequest>> {
            override fun onResponse(call: Call<List<PrenotazioneRequest>>, response: Response<List<PrenotazioneRequest>>) {
                if (response.isSuccessful) {
                    val prenotazioni = response.body() ?: emptyList()
                    sharedPrefs.salvaPrenotazioniStanze(userId, prenotazioni)
                    recyclerView.adapter = PrenotazioniAdapter(prenotazioni)
                    recyclerView.adapter?.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@ListaPrenotazioniActivity, "Errore nel caricamento delle stanze", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PrenotazioneRequest>>, t: Throwable) {
                Toast.makeText(this@ListaPrenotazioniActivity, "Errore di connessione", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchPrenotazioniServizi() {
        val api = RetrofitClient.instance.create(ApiService::class.java)
        api.getPrenotazioniServizi(userId).enqueue(object : Callback<List<PrenotazioneServizioRequest>> {
            override fun onResponse(call: Call<List<PrenotazioneServizioRequest>>, response: Response<List<PrenotazioneServizioRequest>>) {
                if (response.isSuccessful) {
                    val prenotazioniServizi = response.body() ?: emptyList()
                    sharedPrefs.salvaPrenotazioniServizi(userId, prenotazioniServizi)
                    recyclerView.adapter = PrenotazioniServiziAdapter(prenotazioniServizi)
                    recyclerView.adapter?.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@ListaPrenotazioniActivity, "Errore nel caricamento delle prenotazioni di servizi", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PrenotazioneServizioRequest>>, t: Throwable) {
                Toast.makeText(this@ListaPrenotazioniActivity, "Errore di connessione", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkReceiver)  // Rimuove il listener quando l'Activity viene distrutta
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
