package com.example.proyectofinal.Administrador.Editar

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.proyectofinal.Administrador.DrawerLayoutAdmin.DrawerLayoutAdmin
import com.example.proyectofinal.databinding.ActivityNuevoPartidoBinding
import com.google.firebase.firestore.FirebaseFirestore

class EditarPartidoFinal : Menu_Editar() {
    // Variables
    // Binding
    private lateinit var binding: ActivityNuevoPartidoBinding

    // Id del partido seleccionado
    private lateinit var idPartido: String

    // Instancia de la base de datos
    private val db = FirebaseFirestore.getInstance()

    // Variables relacionadas con los datos del partido
    private lateinit var local: String
    private lateinit var visitante: String

    // Imágenes de los equipos local y visitante
    private lateinit var imagenLocal: String
    private lateinit var imagenVisitante: String

    // Lista para almacenar las competiciones
    private lateinit var listaCompeticiones: List<String>

    // Variable para almacenar la competicion elegida
    private var competicionSeleccionada: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNuevoPartidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recogemos el putExtra de la clase EditarEquipo
        val partidoSeleccionado = intent.getStringExtra("partidoSeleccionado")

        // Asignamos el putExtra a la variable declarada anteriormente
        if (partidoSeleccionado != null) {
            local = partidoSeleccionado.substringBefore(" - ")
            visitante = partidoSeleccionado.substringAfter(" - ")

        }


        rellenarCampos()
        rellenarCompeticiones()

        binding.bGuardar.setOnClickListener {
            // Alerta de diálogo para confirmar guardar o cancelar
            val builder = AlertDialog.Builder(this)
            builder.setMessage("¿Deseas guardar el partido?")

            // Si se ha pulsado guardar
            builder.setPositiveButton("Guardar") { dialog, which ->
                comprobarEquipos()
            }

            builder.setNegativeButton("Cancelar") { dialog, which ->
                Toast.makeText(this, "Se ha cancelado la acción.", Toast.LENGTH_SHORT).show()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }

    // Rellenar campos
    private fun rellenarCampos() {
        db.collection("Partidos")
            .whereEqualTo("local", local)
            .whereEqualTo("visitante", visitante)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    binding.etLocal.setText(document["local"].toString())
                    binding.etVisitante.setText(document["visitante"].toString())
                    binding.etResultado.setText(document["resultado"].toString())
                    idPartido = document.id
                }
            }
    }

    // Guardar cambios
    private fun guardarCambios() {
        // Validaciones
        db.collection("Partidos").document(idPartido).update(
            mapOf(
                "local" to binding.etLocal.text.toString(),
                "visitante" to binding.etVisitante.text.toString(),
                "imagenLocal" to "",
                "imagenVisitante" to "",
                "competicion" to competicionSeleccionada,
                "resultado" to binding.etResultado.text.toString()
            ))
            .addOnSuccessListener {
                Toast.makeText(this, "Partido actualizado con id: $idPartido", Toast.LENGTH_SHORT).show()
                guardarImagenes()
                val intent = Intent(this, DrawerLayoutAdmin::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "ERROR al actualizar partido", Toast.LENGTH_SHORT).show()
            }
    }

    private fun comprobarEquipos() {
        // Validación para una expresión regular
        val restriccion = """^\d+-\d+$""".toRegex()
        if (binding.etLocal.text.isNotEmpty() && binding.etVisitante.text.isNotEmpty() &&
            (binding.etResultado.text.toString().matches(restriccion) || binding.etResultado.text.toString() == "VS"))
        {
            db.collection("Equipos")
                .whereEqualTo("nombre", binding.etLocal.text.toString())
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.documents.isNotEmpty()) {
                        // Se encontró un equipo con el nombre dado
                        db.collection("Equipos")
                            .whereEqualTo("nombre", binding.etVisitante.text.toString())
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                if (querySnapshot.documents.isNotEmpty()) {
                                    local = binding.etLocal.text.toString()
                                    visitante = binding.etVisitante.text.toString()
                                    // Se encontró un equipo con el nombre dado
                                    guardarCambios()
                                } else {
                                    // No se encontró ningún equipo con el nombre dado
                                    Toast.makeText(this, "No se encontró el equipo visitante.", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener { exception ->
                                // Ocurrió un error al realizar la consulta
                                Toast.makeText(this, "Fallo la consulta.", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        // No se encontró ningún equipo con el nombre dado
                        Toast.makeText(this, "No se encontró el equipo local.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    // Ocurrió un error al realizar la consulta
                    Toast.makeText(this, "Fallo la consulta.", Toast.LENGTH_SHORT).show()
                }

        }
        else {
            Toast.makeText(this, "Se ha dejado algún campo en blanco o se ha insertado un resultado no válido.", Toast.LENGTH_SHORT).show()
        }

    }

    // Método para asignar los enlaces de las imágenes a los partidos
    private fun guardarImagenes() {
        val local = binding.etLocal.text.toString()
        val visitante = binding.etVisitante.text.toString()

        db.collection("Equipos")
            .whereEqualTo("nombre", local)
            .get()
            .addOnSuccessListener { localSnapshot ->
                for (document in localSnapshot) {
                    imagenLocal = document["escudo"].toString()
                }

                db.collection("Equipos")
                    .whereEqualTo("nombre", visitante)
                    .get()
                    .addOnSuccessListener { visitanteSnapshot ->
                        for (document in visitanteSnapshot) {
                            imagenVisitante = document["escudo"].toString()
                        }
                        db.collection("Partidos").document(idPartido)
                            .update(
                                mapOf(
                                    "imagenLocal" to imagenLocal,
                                    "imagenVisitante" to imagenVisitante
                                )
                            )
                    }
            }
    }
    private fun rellenarCompeticiones(){
        listaCompeticiones = listOf("LaLiga Santander", "Premier League", "Serie A", "Ligue 1", "Bundesliga", "Champions League", "Europa League", "Conference League", "Copa del Rey")

        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, listaCompeticiones)
        binding.listaCompeticiones.adapter = adapter

        // Elección del equipo
        binding.listaCompeticiones.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                competicionSeleccionada = parent?.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // En caso de no elegir ningún equipo
            }
        }
    }
}
