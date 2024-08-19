package com.example.proyectofinal.DrawerLayout

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyectofinal.R
import com.example.proyectofinal.databinding.FragmentFiltrarBinding
import com.google.firebase.firestore.FirebaseFirestore


class EquiposFragmentDL : Fragment(R.layout.fragment_filtrar) {

    // Habilitacion de binding
    private var _binding: FragmentFiltrarBinding? = null
    private val binding get() = _binding!!

    // Se declara una variable para almacenar la vista de búsqueda
    private lateinit var searchView: SearchView

    // Se declara una lista mutable para almacenar los nombres de los equipos
    private lateinit var listaEquipos: MutableList<String>

    // Se declara un adaptador para la lista de equipos
    private lateinit var adapter: ArrayAdapter<String>

    // Se obtiene una instancia de la base de datos Firestore
    private val db = FirebaseFirestore.getInstance()

    // Se declara una variable para el listener del botón
    private var buttonClickListener: OnButtonClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Se infla el diseño de la vista del fragmento y se asigna al binding
        _binding = FragmentFiltrarBinding.inflate(inflater, container, false)

        binding.tvTexto.setText("Equipos")

        // Se asigna la vista de búsqueda del binding a la variable searchView
        searchView = binding.buscador

        // Se inicializa la lista de equipos como una lista mutable vacía
        listaEquipos = mutableListOf()

        // Se configura el clic del botón de búsqueda
        binding.bBuscar.setOnClickListener {
            val searchValue = getSearchViewValue()

            comprobarBuscador(searchValue)

            // Se llama al método tabLayout del listener del botón
            buttonClickListener?.tabLayout(searchValue)
        }

        // Llamada al metodo consultar equipos
        consultarEquipos()

        return binding.root
    }

    /**
     * Método para obtener los equipos de la colección "Equipos" en Firestore
     */
    private fun consultarEquipos() {
        // Se obtienen los documentos de la colección "Equipos" de Firestore
        db.collection("Equipos").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Se obtiene el nombre del equipo de cada documento
                    val equipo = document.getString("nombre")
                    if (equipo != null && !listaEquipos.contains(equipo)) {
                        // Si el nombre del equipo no es nulo y no está en la lista, se agrega a la lista
                        listaEquipos.add(equipo)
                    }
                }

                // Se crea un adaptador con el contexto actual, un diseño predeterminado para el elemento de la lista
                // y la lista de equipos
                adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    listaEquipos
                )
                // Se asigna el adaptador a la vista de la lista de equipos
                binding.listado.adapter = adapter

                // Se configura el clic de los elementos de la lista de equipos
                binding.listado.setOnItemClickListener { parent, _, position, _ ->
                    // Se obtiene el equipo seleccionado
                    val equipos = parent.getItemAtPosition(position) as String
                    // Se llama al método onButtonClick del listener del botón con el equipo seleccionado
                    buttonClickListener?.onButtonClick(equipos)
                    // Se llama al método tabLayout del listener del botón con el equipo seleccionado
                    buttonClickListener?.tabLayout(equipos)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Se libera el binding al destruir la vista
        _binding = null
    }

    /**
     * Método para obtener el valor ingresado en la vista de búsqueda
     */
    private fun getSearchViewValue(): String {
        // Se obtiene el texto ingresado en la vista de búsqueda
        return binding.buscador.query.toString()
    }

    /**
     * Interfaz para manejar los eventos de clic del botón y cambio de pestaña
     */
    interface OnButtonClickListener {
        // Método para manejar el evento de clic del botón
        fun onButtonClick(data: String)
        // Método para manejar el evento de cambio de pestaña
        fun tabLayout(data: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Se verifica si el contexto implementa la interfaz OnButtonClickListener
        if (context is OnButtonClickListener) {
            // Si es así, se asigna el contexto como el listener del botón
            buttonClickListener = context
        } else {
            // Si no, se lanza una excepción informando que el contexto debe implementar la interfaz
            throw IllegalArgumentException("The host activity must implement OnButtonClickListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        // Se anula el listener del botón al desvincularse del contexto
        buttonClickListener = null
    }

    /**
     * Método para comprobar el valor ingresado en el buscador y realizar acciones correspondientes
     */
    private fun comprobarBuscador(searchValue: String) {
        db.collection("Equipos")
            .whereEqualTo("nombre", searchValue)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (!documentSnapshot.isEmpty) {
                    // Se llama al método onButtonClick del listener del botón con el valor de búsqueda
                    buttonClickListener?.onButtonClick(searchValue)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "No se ha encontrado ningún equipo con ese nombre.",
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
}
