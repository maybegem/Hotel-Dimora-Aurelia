package com.example.albergo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.albergo.API.PrenotazioneServizioRequest

class PrenotazioniServiziAdapter(private val prenotazioniServizi: List<PrenotazioneServizioRequest>) :
    RecyclerView.Adapter<PrenotazioniServiziAdapter.PrenotazioneServizioViewHolder>() {

    inner class PrenotazioneServizioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewNomeServizio = itemView.findViewById<TextView>(R.id.textViewNomeServizio)
        private val textViewDataServizio = itemView.findViewById<TextView>(R.id.textViewDataServizio)
        private val textViewOrarioServizio = itemView.findViewById<TextView>(R.id.textViewOrarioServizio)
        private val textViewNumeroPersone = itemView.findViewById<TextView>(R.id.textViewNumeroPersone)


        fun bind(prenotazione: PrenotazioneServizioRequest) {
            textViewNomeServizio.text = "Servizio: ${prenotazione.tipo_servizio}"
            textViewDataServizio.text = "Data: ${prenotazione.data_servizio}"
            textViewOrarioServizio.text = "Orario: ${prenotazione.orario}"
            textViewNumeroPersone.text = "Numero persone: ${prenotazione.numero_persone}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrenotazioneServizioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_prenotazione_servizio, parent, false)
        return PrenotazioneServizioViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrenotazioneServizioViewHolder, position: Int) {
        holder.bind(prenotazioniServizi[position])
    }

    override fun getItemCount(): Int = prenotazioniServizi.size
}
