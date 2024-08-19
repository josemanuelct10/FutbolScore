package com.example.proyectofinal.Administrador.Editar

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.proyectofinal.databinding.ActivityEditarBinding
import com.google.firebase.firestore.FirebaseFirestore

class EditarEquipo : Menu_Editar() {

    // Variables
    private lateinit var binding: ActivityEditarBinding
    private var equipoSeleccionado: String? = null

    // Creación de lista para lista desplegable
    private lateinit var listaEquipos: MutableList<String>

    // Instancia de la base de datos
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tEditarEquipos.setText("Editar Equipo")

        // Inicializamos la lista de Equipos
        listaEquipos = mutableListOf()

        // Rellenamos la lista llamando al método
        rellenarLista()

        binding.bEditar.setOnClickListener {
            val intent = Intent(this, EditarEquipoFinal::class.java)
            intent.putExtra("equipoSeleccionado", equipoSeleccionado)
            startActivity(intent)
        }
    }

    private fun rellenarLista() {
        listaEquipos.clear() // Limpia la lista actual antes de agregar nuevos elementos

        db.collection("Equipos").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val equipo = document.getString("nombre")
                    if (equipo != null && !listaEquipos.contains(equipo)) {
                        listaEquipos.add(equipo)
                    }
                }

                // Creamos el ArrayAdapter y lo asignamos al ListView
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaEquipos)
                binding.deplegableEquipos.adapter = adapter
            }

        // Elección del equipo
        binding.deplegableEquipos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                equipoSeleccionado = parent?.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // En caso de no elegir ningún equipo
            }
        }
    }
}
