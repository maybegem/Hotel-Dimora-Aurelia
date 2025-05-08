package com.example.albergo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.albergo.API.ApiService
import com.example.albergo.API.RetrofitClient
import com.example.albergo.API.ServiziResponse
import com.example.albergo.API.Servizio
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServiziExtraActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_servizi)

        // Recupera l'ID utente dalle SharedPreferences
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("USER_ID", -1)

        if (userId == -1) {
            Toast.makeText(
                this,
                "Errore: ID utente mancante. Effettua il login.",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        // Configura la Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Servizi Extra"

        // Configura RecyclerView
        recyclerView = findViewById(R.id.recyclerViewServizi)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Chiama la funzione per recuperare i dati
        fetchServizi(userId)
    }

    private fun fetchServizi(userId: Int) {
        val api = RetrofitClient.instance.create(ApiService::class.java)

        api.getServizi().enqueue(object : Callback<ServiziResponse> {
            override fun onResponse(
                call: Call<ServiziResponse>,
                response: Response<ServiziResponse>
            ) {
                if (response.isSuccessful) {
                    val serviziResponse = response.body()
                    if (serviziResponse?.success == true) {
                        val servizi = serviziResponse.servizi
                        Log.d("ServiziExtraActivity", "Dati ricevuti: ${servizi.size} servizi")

                        // Personalizza i nomi dei servizi
                        val serviziPersonalizzati = servizi.map { servizio ->
                            servizio.copy(
                                nome = when (servizio.nome) {
                                    "Ristorante" -> "La Tavola Aurea"
                                    "Spa" -> "La Sala Massaggi"
                                    "Escursione" -> "Escursione sulle colline"
                                    else -> servizio.nome
                                }
                            )
                        }

                        // Imposta l'adapter
                        recyclerView.adapter = ServiziAdapter(serviziPersonalizzati) { servizio ->
                            apriDettaglioPrenotazione(servizio, userId)
                        }
                    } else {
                        Log.e("ServiziExtraActivity", "Errore: success=false nella risposta")
                    }
                } else {
                    Log.e("ServiziExtraActivity", "Errore: risposta non riuscita")
                }
            }

            override fun onFailure(call: Call<ServiziResponse>, t: Throwable) {
                Log.e("ServiziExtraActivity", "Errore di connessione: ${t.message}")
                Toast.makeText(
                    this@ServiziExtraActivity,
                    "Errore di connessione",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun apriDettaglioPrenotazione(servizio: Servizio, userId: Int) {
        // Naviga alla schermata di prenotazione del servizio
        val intent = Intent(this, PrenotazioneServizioActivity::class.java)
        intent.putExtra("NOME_SERVIZIO", servizio.nome)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
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