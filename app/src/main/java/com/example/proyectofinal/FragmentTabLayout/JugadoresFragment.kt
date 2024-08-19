package com.example.proyectofinal.FragmentTabLayout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal.Adapter.JugadoresAdapter
import com.example.proyectofinal.ClasesDatos.Jugadores
import com.example.proyectofinal.DrawerLayout.DrawerLayout
import com.example.proyectofinal.databinding.FragmentRecyclerBinding
import com.google.firebase.firestore.FirebaseFirestore

class JugadoresFragment: Fragment() {
    // VARIABLES

    // Creamos la instancia de la base de datos
    private val db = FirebaseFirestore.getInstance()

    private lateinit var listaJugadores: ArrayList<Jugadores>

    private lateinit var adapterJugadores: JugadoresAdapter

    private lateinit var binding: FragmentRecyclerBinding




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecyclerBinding.inflate(inflater, container, false)

        // Obtén los datos de la actividad contenedora
        val data = (activity as? DrawerLayout)?.data


        // Instancia del array de jugadores
        listaJugadores = ArrayList()

        // Asignacion del array al adapter
        adapterJugadores = JugadoresAdapter(listaJugadores)


        if (data != null) {
            consultarJugadores(data)
        }
        return binding.root
    }

    private fun consultarJugadores(data: String){
        // Aqui consultamos todos los jugadores y los añadimos al array de partidos

        if (data != null) {
            db.collection("Jugadores").whereEqualTo("equipo", data).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val jugador = document.toObject(Jugadores::class.java)
                        jugador.nombre = document["nombre"].toString()
                        jugador.equipo = document["equipo"].toString()
                        jugador.posicion = document["posicion"].toString()
                        jugador.edad = document["edad"].toString()
                        jugador.imagen = document["imagen"].toString()

                        listaJugadores.add(jugador)

                        // Aqui asignamos al recyclerview el adapter
                        binding.listado.adapter = adapterJugadores
                        binding.listado.layoutManager = LinearLayoutManager(requireContext())

                    }
                }
        }
        else{
            Toast.makeText(requireContext(), "Ha habido un error al cargar los jugadores.", Toast.LENGTH_SHORT).show()
        }

    }

}




