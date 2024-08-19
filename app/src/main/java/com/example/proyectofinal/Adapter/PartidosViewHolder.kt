package com.example.proyectofinal.Adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectofinal.ClasesDatos.Partidos
import com.example.proyectofinal.databinding.ItemPartidosBinding

class PartidosViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Enlace de vistas utilizando el archivo de enlace generado ItemPartidosBinding
    val binding = ItemPartidosBinding.bind(view)

    // Método utilizado para vincular los datos del partido al diseño de vista correspondiente
    fun render(partidoModel: Partidos) {
        // Establecer el nombre del equipo local en el TextView correspondiente
        binding.equipoLocal.text = partidoModel.local
        // Establecer el nombre del equipo visitante en el TextView correspondiente
        binding.equipoVisitante.text = partidoModel.visitante
        // Cargar la imagen del equipo local utilizando la biblioteca Glide y establecerla en el ImageView correspondiente
        Glide.with(binding.imagenLocal.context).load(partidoModel.imagenLocal).into(binding.imagenLocal)
        // Cargar la imagen del equipo visitante utilizando la biblioteca Glide y establecerla en el ImageView correspondiente
        Glide.with(binding.imagenVisitante.context).load(partidoModel.imagenVisitante).into(binding.imagenVisitante)
        // Establecer el nombre de la competición en el TextView correspondiente
        binding.tvCompeticion.text = partidoModel.competicion
        // Establecer el resultado del partido en el TextView correspondiente
        binding.tvResultado.text = partidoModel.resultado
    }
}
