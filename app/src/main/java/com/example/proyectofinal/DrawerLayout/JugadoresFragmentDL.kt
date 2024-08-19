package com.example.proyectofinal.DrawerLayout

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import com.example.proyectofinal.MostrarJugador
import com.example.proyectofinal.R
import com.example.proyectofinal.databinding.FragmentFiltrarBinding
import com.google.firebase.firestore.FirebaseFirestore


class JugadoresFragmentDL : Fragment(R.layout.fragment_filtrar) {
    private var _binding: FragmentFiltrarBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchView: SearchView
    private lateinit var listaJugadores: MutableList<String>
    private lateinit var adapter: ArrayAdapter<String>

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFiltrarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTexto.setText("Jugadores")
        searchView = binding.buscador
        listaJugadores = mutableListOf()

        binding.bBuscar.setOnClickListener {

            val searchValue = getSearchViewValue()
            comprobarBuscador(searchValue)
        }

        rellenarLista()


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getSearchViewValue(): String {
        return binding.buscador.query.toString()
    }

    private fun comprobarBuscador(searchValue: String) {
        db.collection("Jugadores")
            .whereEqualTo("nombre", searchValue)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (!documentSnapshot.isEmpty) {
                    val intent = Intent(requireContext(), MostrarJugador::class.java)
                    intent.putExtra("searchValue", searchValue)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "No se ha encontrado ningun jugador con ese nombre.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    "Error al obtener los datos: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun rellenarLista(){
        // Consulta de jugadores para rellenar la ListView
        db.collection("Jugadores").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val jugadores = document.getString("nombre")
                    if (jugadores != null && !listaJugadores.contains(jugadores)) {
                        listaJugadores.add(jugadores)
                    }
                }
                adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listaJugadores)
                binding.listado.adapter = adapter

                binding.listado.setOnItemClickListener { parent, _, position, _ ->
                    val jugadores = parent.getItemAtPosition(position) as String
                    val intent = Intent(requireContext(), MostrarJugador::class.java)
                    intent.putExtra("jugador", jugadores)
                    startActivity(intent)
                }
            }
    }


}

