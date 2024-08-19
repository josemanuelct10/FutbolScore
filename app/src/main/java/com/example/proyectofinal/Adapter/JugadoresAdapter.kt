package com.example.proyectofinal.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.ClasesDatos.Jugadores
import com.example.proyectofinal.R

class JugadoresAdapter(private val listaJugadores: List<Jugadores>) : RecyclerView.Adapter<JugadoresViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JugadoresViewHolder {
        // Inflar el diseño de elemento de jugador y crear un nuevo JugadoresViewHolder
        val LayoutInflater = LayoutInflater.from(parent.context)
        return JugadoresViewHolder(LayoutInflater.inflate(R.layout.item_jugador, parent, false))
    }

    override fun onBindViewHolder(holder: JugadoresViewHolder, position: Int) {
        // Obtener el objeto Jugadores en la posición dada
        val item = listaJugadores[position]
        // Vincular los datos del objeto Jugadores al JugadoresViewHolder
        holder.render(item)
    }

    override fun getItemCount(): Int {
        // Devolver el número de elementos en la lista de jugadores
        return listaJugadores.size
    }
}
