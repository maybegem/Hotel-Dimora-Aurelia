package com.example.albergo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.albergo.API.PrenotazioneRequest
import com.example.albergo.R

class PrenotazioniAdapter(private val prenotazioni: List<PrenotazioneRequest>) :
    RecyclerView.Adapter<PrenotazioniAdapter.PrenotazioneViewHolder>() {

    inner class PrenotazioneViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewCheckIn = itemView.findViewById<TextView>(R.id.textViewCheckIn)
        private val textViewCheckOut = itemView.findViewById<TextView>(R.id.textViewCheckOut)
        private val textViewAdulti = itemView.findViewById<TextView>(R.id.textViewAdulti)
        private val textViewBambini = itemView.findViewById<TextView>(R.id.textViewBambini)
        private val textViewColazione = itemView.findViewById<TextView>(R.id.textViewColazione)

        fun bind(prenotazione: PrenotazioneRequest) {
            textViewCheckIn.text = "Check-In: ${prenotazione.check_in}"
            textViewCheckOut.text = "Check-Out: ${prenotazione.check_out}"
            textViewAdulti.text = "Adulti: ${prenotazione.adulti}"
            textViewBambini.text = "Bambini: ${prenotazione.bambini}"
            textViewColazione.text = "Colazione: ${prenotazione.colazione}"

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrenotazioneViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_prenotazione, parent, false)
        return PrenotazioneViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrenotazioneViewHolder, position: Int) {
        holder.bind(prenotazioni[position])
    }

    override fun getItemCount(): Int = prenotazioni.size
}



