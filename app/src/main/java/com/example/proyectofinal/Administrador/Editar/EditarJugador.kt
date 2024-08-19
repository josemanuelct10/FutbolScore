package com.example.proyectofinal.Administrador.Editar

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.proyectofinal.databinding.ActivityEditarBinding
import com.google.firebase.firestore.FirebaseFirestore

class EditarJugador : Menu_Editar() {
    // Variables
    private lateinit var binding: ActivityEditarBinding
    private var jugadorSeleccionado: String? = null // Declarar la variable aquí

    // Creación de lista para lista desplegable
    private lateinit var listaJugador: MutableList<String>

    // Instancia de la base de datos
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tEditarEquipos.setText("Editar Jugador")

        // Inicializamos la lista de Equipos
        listaJugador = mutableListOf()

        // Rellenamos la lista llamando al método
        rellenarLista()

        binding.bEditar.setOnClickListener {
            val intent = Intent(this, EditarJugadorFinal::class.java)
            intent.putExtra("jugadorSeleccionado", jugadorSeleccionado)
            startActivity(intent)
        }
    }

    private fun rellenarLista() {
        listaJugador.clear() // Limpia la lista actual antes de agregar nuevos elementos

        db.collection("Jugadores").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val equipo = document.getString("nombre")
                    if (equipo != null && !listaJugador.contains(equipo)) {
                        listaJugador.add(equipo)
                    }
                }

                // Creamos el ArrayAdapter y lo asignamos al ListView
                val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, listaJugador)
                binding.deplegableEquipos.adapter = adapter
            }

        // Elección del equipo
        binding.deplegableEquipos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                jugadorSeleccionado = parent?.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // En caso de no elegir ningún equipo
            }
        }
    }
}
