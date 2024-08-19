package com.example.proyectofinal.DrawerLayout


import com.example.proyectofinal.R


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal.Adapter.PartidosAdapter
import com.example.proyectofinal.ClasesDatos.Partidos
import com.example.proyectofinal.databinding.FragmentRecyclerBinding
import com.google.firebase.firestore.FirebaseFirestore


class InicioFragment : Fragment(R.layout.fragment_recycler) {
    private var _binding: FragmentRecyclerBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val listaPartidos = ArrayList<Partidos>()
    private val adapterPartidos = PartidosAdapter(listaPartidos)




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecyclerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        consultarPartidos()

    }

    private fun consultarPartidos(){
        db.collection("Partidos").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val partido = document.toObject(Partidos::class.java)
                    partido.local = document["local"].toString()
                    partido.visitante = document["visitante"].toString()
                    partido.imagenLocal = document["imagenLocal"].toString()
                    partido.imagenVisitante = document["imagenVisitante"].toString()
                    partido.competicion = document["competicion"].toString()
                    partido.resultado = document["resultado"].toString()
                    listaPartidos.add(partido)
                }

                // Asignamos al RecyclerView el adapter
                binding.listado.adapter = adapterPartidos
                binding.listado.layoutManager = LinearLayoutManager(requireContext())
            }
    }
}
