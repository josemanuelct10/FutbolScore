package com.example.proyectofinal.Adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectofinal.ClasesDatos.Jugadores
import com.example.proyectofinal.databinding.ItemJugadorBinding

class JugadoresViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Enlace de vistas utilizando el archivo de enlace generado ItemJugadorBinding
    val binding = ItemJugadorBinding.bind(view)

    // Método utilizado para vincular los datos del jugador al diseño de vista correspondiente
    fun render(jugadorModel: Jugadores) {
        // Establecer el nombre del jugador en el TextView correspondiente
        binding.tvNombre.text = jugadorModel.nombre
        // Establecer la posición del jugador en el TextView correspondiente
        binding.tvPosicion.text = jugadorModel.posicion
        // Establecer el equipo del jugador en el TextView correspondiente
        binding.tvEquipo.text = jugadorModel.equipo
        // Establecer la edad del jugador en el TextView correspondiente
        binding.tvEdad.text = jugadorModel.edad
        // Cargar la imagen del jugador utilizando la biblioteca Glide y establecerla en el ImageView correspondiente
        Glide.with(binding.iJugador.context).load(jugadorModel.imagen).into(binding.iJugador)
    }
}



