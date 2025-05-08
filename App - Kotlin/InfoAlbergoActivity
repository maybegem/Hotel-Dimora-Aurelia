package com.example.albergo

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.albergo.API.ApiService
import com.squareup.picasso.Picasso
import com.example.albergo.API.RetrofitClient
import com.example.albergo.API.AlbergoInfo
import com.squareup.picasso.NetworkPolicy
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InfoAlbergoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_albergo)

        // Configura la Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  // Abilita il pulsante indietro
        supportActionBar?.title = "Informazioni sull'Albergo"

        val imageView = findViewById<ImageView>(R.id.imageViewAlbergo)
        val textViewDescrizione = findViewById<TextView>(R.id.textViewDescrizione)

        // Configura Retrofit
        val api = RetrofitClient.instance.create(ApiService::class.java)

        // Recupera i dati sull'albergo
        api.getInfoAlbergo().enqueue(object : Callback<AlbergoInfo> {
            override fun onResponse(call: Call<AlbergoInfo>, response: Response<AlbergoInfo>) {
                if (response.isSuccessful) {
                    val info = response.body()
                    if (info != null) {
                        // Aggiorna la UI con i dati
                        textViewDescrizione.text = info.descrizione

                        // Ora prova a caricare l'immagine dal server
                        Picasso.get().load(info.immagine_url)
                            .into(imageView, object : com.squareup.picasso.Callback {
                                override fun onSuccess() {
                                    Toast.makeText(this@InfoAlbergoActivity, "Immagine caricata con successo", Toast.LENGTH_SHORT).show()
                                }

                                override fun onError(e: Exception?) {
                                    Toast.makeText(this@InfoAlbergoActivity, "Errore nel caricamento dell'immagine", Toast.LENGTH_SHORT).show()
                                    e?.printStackTrace()

                                    // Imposta subito l'immagine di errore
                                    imageView.setImageResource(R.drawable.errore_connessione)
                                }
                            })


                    }
                } else {
                    Toast.makeText(this@InfoAlbergoActivity, "Errore nel caricamento", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AlbergoInfo>, t: Throwable) {
                Toast.makeText(this@InfoAlbergoActivity, "Errore di connessione: ${t.message}", Toast.LENGTH_SHORT).show()
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
