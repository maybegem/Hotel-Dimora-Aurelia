package com.example.albergo

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.albergo.API.Servizio
import com.squareup.picasso.Picasso

class ServiziAdapter(
    private val servizi: List<Servizio>,
    private val onClick: (Servizio) -> Unit
) : RecyclerView.Adapter<ServiziAdapter.ServizioViewHolder>() {

    inner class ServizioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewServizio)
        val textViewDescrizione: TextView = itemView.findViewById(R.id.textViewDescrizioneServizio)
        val btnPrenota: Button = itemView.findViewById(R.id.btnPrenotaServizio)

        fun bind(servizio: Servizio) {
            textViewDescrizione.text = servizio.descrizione

            // Imposta un'immagine predefinita (già fissata nell'XML)
            imageView.setImageResource(R.drawable.errore_connessione)

            // Carica l'immagine dal server con Picasso
            Picasso.get()
                .load(servizio.immagine_url)
                .into(imageView, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        // Se il caricamento è riuscito, l'immagine viene aggiornata
                    }

                    override fun onError(e: Exception?) {
                        // Se l'immagine non si carica, resta quella predefinita
                        e?.printStackTrace()
                    }
                })

            // Click listener per il bottone "Prenota"
            btnPrenota.setOnClickListener {
                val context = itemView.context
                val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val userId = sharedPref.getInt("USER_ID", -1)

                if (userId == -1) {
                    Toast.makeText(context, "Errore: ID utente mancante. Effettua il login.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Modifica il nome del servizio per il bottone
                val nomePersonalizzato = when (servizio.nome) {
                    "Ristorante" -> "La Tavola Aurea"
                    "Spa" -> "La Sala Massaggi"
                    "Escursione" -> "Escursione sulle colline"
                    else -> servizio.nome
                }

                // Avvia l'activity di prenotazione
                val intent = Intent(context, PrenotazioneServizioActivity::class.java)
                intent.putExtra("NOME_SERVIZIO", nomePersonalizzato)
                intent.putExtra("USER_ID", userId)  // Passa l'ID utente
                context.startActivity(intent)
            }

            // Click listener per tutta la card
            itemView.setOnClickListener {
                onClick(servizio)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServizioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_servizio, parent, false)
        return ServizioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServizioViewHolder, position: Int) {
        holder.bind(servizi[position])
    }

    override fun getItemCount(): Int = servizi.size
}
