package com.example.proyectofinal.Administrador.Editar

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.proyectofinal.databinding.ActivityEditarBinding
import com.google.firebase.firestore.FirebaseFirestore
class EditarPartido : Menu_Editar() {

    // Variables
    private lateinit var binding: ActivityEditarBinding
    private var partidoSeleccionado: String? = null // Declarar la variable aquí

    // Creacion de lista para lista desplegable
    private lateinit var listaPartidos: MutableList<String>

    // Instancia de la base de datos
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tEditarEquipos.setText("Editar Partido")


        // Inicializamos la lista de Partidos
        listaPartidos = mutableListOf()

        // Rellenamos la lista llamando al método
        rellenarLista()

        binding.bEditar.setOnClickListener {
            val intent = Intent(this, EditarPartidoFinal::class.java)
            intent.putExtra("partidoSeleccionado", partidoSeleccionado)
            startActivity(intent)
        }
    }

    private fun rellenarLista() {
        listaPartidos.clear() // Limpia la lista actual antes de agregar nuevos elementos

        db.collection("Partidos").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    var partido = document.getString("local")
                    partido += " - "
                    partido += document.getString("visitante")

                    if (partido != null && !listaPartidos.contains(partido)) {
                        listaPartidos.add(partido)
                    }
                }

                // Creamos el ArrayAdapter y lo asignamos al ListView
                val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, listaPartidos)
                binding.deplegableEquipos.adapter = adapter
            }

        // Elección del partido
        binding.deplegableEquipos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                partidoSeleccionado = parent?.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // En caso de no elegir ningún partido
            }
        }
    }
}
