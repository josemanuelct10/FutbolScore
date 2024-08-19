package com.example.proyectofinal.DrawerLayout

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyectofinal.MostrarPartidosCompeticiones
import com.example.proyectofinal.R
import com.example.proyectofinal.databinding.FragmentFiltrarBinding
import com.google.firebase.firestore.FirebaseFirestore


class CompeticionesFragmentDL : Fragment(R.layout.fragment_filtrar) {

    // Declaración de variables
    private var _binding: FragmentFiltrarBinding? = null
    private val binding get() = _binding!!
    private lateinit var searchView: SearchView
    private lateinit var listaCompeticiones: MutableList<String>
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

        binding.tvTexto.setText("Competiciones")
        // Configuración de la vista y la lista de competiciones
        searchView = binding.buscador
        listaCompeticiones = mutableListOf()

        binding.bBuscar.setOnClickListener {
            val searchValue = getSearchViewValue()
            comprobarBuscador(searchValue)
        }

        obtenerCompeticiones()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Obtener el valor actual del SearchView
    private fun getSearchViewValue(): String {
        return binding.buscador.query.toString()
    }

    // Comprobar si la competición buscada existe en la base de datos
    private fun comprobarBuscador(searchValue: String) {
        db.collection("Partidos")
            .whereEqualTo("competicion", searchValue)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (!documentSnapshot.isEmpty) {
                    val intent = Intent(requireContext(), MostrarPartidosCompeticiones::class.java)
                    intent.putExtra("searchValue", searchValue)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "No se ha encontrado ninguna competición con ese nombre.",
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

    private fun obtenerCompeticiones(){
        // Obtener competiciones de la colección "Partidos"
        db.collection("Partidos").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val competicion = document.getString("competicion")
                    if (competicion != null && !listaCompeticiones.contains(competicion)) {
                        listaCompeticiones.add(competicion)
                    }
                }

                // Configuración del adaptador y la lista de competiciones
                adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    listaCompeticiones
                )
                binding.listado.adapter = adapter

                binding.listado.setOnItemClickListener { parent, _, position, _ ->
                    val competicion = parent.getItemAtPosition(position) as String
                    val intent = Intent(requireContext(), MostrarPartidosCompeticiones::class.java)
                    intent.putExtra("competicion", competicion)
                    startActivity(intent)
                }
            }
    }
}
