package com.example.proyectofinal.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.ClasesDatos.Partidos
import com.example.proyectofinal.R

class PartidosAdapter(private val listaPartidos: List<Partidos>) : RecyclerView.Adapter<PartidosViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartidosViewHolder {
        // Inflar el diseño de elemento de partido y crear un nuevo PartidosViewHolder
        val LayoutInflater = LayoutInflater.from(parent.context)
        return PartidosViewHolder(LayoutInflater.inflate(R.layout.item_partidos, parent, false))
    }

    override fun onBindViewHolder(holder: PartidosViewHolder, position: Int) {
        // Obtener el objeto Partidos en la posición dada
        val item = listaPartidos[position]
        // Vincular los datos del objeto Partidos al PartidosViewHolder
        holder.render(item)
    }

    override fun getItemCount(): Int {
        // Devolver el número de elementos en la lista de partidos
        return listaPartidos.size
    }
}
