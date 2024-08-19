package com.example.proyectofinal.FragmentTabLayout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal.Adapter.PartidosAdapter
import com.example.proyectofinal.ClasesDatos.Partidos
import com.example.proyectofinal.DrawerLayout.DrawerLayout
import com.example.proyectofinal.databinding.FragmentRecyclerBinding
import com.google.firebase.firestore.FirebaseFirestore

class PartidosFragment : Fragment() {

    // VARIABLES

    // Creamos la instancia de la base de datos
    private val db = FirebaseFirestore.getInstance()

    private lateinit var listaPartidos: ArrayList<Partidos>

    private lateinit var adapterPartidos: PartidosAdapter

    private lateinit var binding: FragmentRecyclerBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecyclerBinding.inflate(inflater, container, false)
        // Instancia del array de jugadores
        listaPartidos = ArrayList()

        val data = (activity as? DrawerLayout)?.data

        // Asignacion del array al adapter
        adapterPartidos = PartidosAdapter(listaPartidos)

        // Aqui consultamos todos los partidos y los añadimos al array de partidos
        if (data != null) {
            consultarPartidos(data)
        }

        return binding.root
    }

    private fun consultarPartidos(data: String){

        if (data != null) {
            // Consulta de los partidos donde el equipo es local
            db.collection("Partidos").whereEqualTo("local", data).get()
                .addOnSuccessListener { localDocuments ->
                    for (localDocument in localDocuments) {
                        val partido = localDocument.toObject(Partidos::class.java)
                        partido.local = localDocument["local"].toString()
                        partido.visitante = localDocument["visitante"].toString()
                        partido.imagenLocal = localDocument["imagenLocal"].toString()
                        partido.imagenVisitante = localDocument["imagenVisitante"].toString()
                        partido.competicion = localDocument["competicion"].toString()
                        partido.resultado = localDocument["resultado"].toString()

                        listaPartidos.add(partido)
                    }

                    // Consulta de los partidos donde el equipo es visitante
                    db.collection("Partidos").whereEqualTo("visitante", data).get()
                        .addOnSuccessListener { visitanteDocuments ->
                            for (visitanteDocument in visitanteDocuments) {
                                val partido = visitanteDocument.toObject(Partidos::class.java)
                                partido.local = visitanteDocument["local"].toString()
                                partido.visitante = visitanteDocument["visitante"].toString()
                                partido.imagenLocal = visitanteDocument["imagenLocal"].toString()
                                partido.imagenVisitante =
                                    visitanteDocument["imagenVisitante"].toString()
                                partido.competicion = visitanteDocument["competicion"].toString()
                                partido.resultado = visitanteDocument["resultado"].toString()

                                listaPartidos.add(partido)
                            }

                            // Aquí asignamos al RecyclerView el adapter
                            binding.listado.adapter = adapterPartidos
                            binding.listado.layoutManager = LinearLayoutManager(requireContext())
                        }
                }
        }
        else{
            Toast.makeText(requireContext(), "Ha habido un error al cargar los partidos.", Toast.LENGTH_SHORT).show()
        }
    }


}