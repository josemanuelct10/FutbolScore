package com.example.proyectofinal.Administrador.Añadir

import android.Manifest
import android.R
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.proyectofinal.Administrador.DrawerLayoutAdmin.DrawerLayoutAdmin
import com.example.proyectofinal.ClasesDatos.Equipos
import com.example.proyectofinal.ClasesDatos.Jugadores
import com.example.proyectofinal.ClasesDatos.Partidos
import com.example.proyectofinal.databinding.ActivityNuevoPartidoBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.BufferedReader
import java.io.InputStreamReader

class NuevoPartido : Menu_Anadir() {


    private lateinit var binding: ActivityNuevoPartidoBinding
    private lateinit var storage: FirebaseStorage

    // Instancia de la base de datos
    private val db = FirebaseFirestore.getInstance()

    // Id del equipo seleccionado
    private var idPartido: String = ""

    // Imagen Local
    private lateinit var imagenLocal: String

    // Imagen Visitante
    private lateinit var imagenVisitante: String


    // Lista para almacenar las competiciones
    private lateinit var listaCompeticiones: List<String>

    // Variable para almacenar la competicion elegida
    private var competicionSeleccionada: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNuevoPartidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = Firebase.storage

        rellenarCompeticiones()

        binding.bGuardar.setOnClickListener {

            // Alerta de diálogo para guardar o cancelar
            val builder = AlertDialog.Builder(this)
            builder.setMessage("¿Deseas guardar el equipo?")

            // Si se ha pulsado guardar
            builder.setPositiveButton("Guardar"){ dialog, which ->
                comprobarEquipos()
            }

            builder.setNegativeButton("Cancelar"){dialog, which ->
                Toast.makeText(this, "Se ha cancelado la acción.", Toast.LENGTH_SHORT).show()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }

    // Método para comprobar los equipos que han insertado en los equipos
    private fun comprobarEquipos() {
        // Consulta para buscar el equipo local
        db.collection("Equipos").whereEqualTo("nombre", binding.etLocal.text.toString()).get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.documents.isNotEmpty()) {
                    // Se encontró un equipo con el nombre dado
                    // Consulta para buscar el equipo visitante
                    db.collection("Equipos").whereEqualTo("nombre", binding.etVisitante.text.toString()).get()
                        .addOnSuccessListener { querySnapshot ->
                            if (querySnapshot.documents.isNotEmpty()) {
                                // Se encontraron los dos equipos
                                nuevoPartido()
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

    // Método para crear un partido
    private fun nuevoPartido() {
        // Validación para una expresión regular
        val restriccion = """^\d+-\d+$""".toRegex()

        // Validaciones
        if (binding.etLocal.text.isNotEmpty() && binding.etVisitante.text.isNotEmpty() &&
            (binding.etResultado.text.toString().matches(restriccion) || binding.etResultado.text.toString() == "VS")) {
            // Añadir un nuevo partido a la colección "Partidos"
            db.collection("Partidos")
                .add(
                    mapOf(
                        "local" to binding.etLocal.text.toString(),
                        "visitante" to binding.etVisitante.text.toString(),
                        "imagenLocal" to "",
                        "imagenVisitante" to "",
                        "competicion" to competicionSeleccionada,
                        "resultado" to binding.etResultado.text.toString()
                    )
                )
                .addOnSuccessListener { documento ->
                    Toast.makeText(this, "Partido añadido con id: ${documento.id}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, DrawerLayoutAdmin::class.java)
                    startActivity(intent)
                    guardarImagenes()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "ERROR al añadir partido", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Algún campo está vacío o has insertado caracteres erróneos en el resultado", Toast.LENGTH_SHORT).show()
        }
    }

    // Método para asignar los enlaces de las imágenes a los partidos
    private fun guardarImagenes() {
        val local = binding.etLocal.text.toString()
        val visitante = binding.etVisitante.text.toString()
        // Consulta para obtener la imagen del equipo local
        db.collection("Equipos").whereEqualTo("nombre", local).get()
            .addOnSuccessListener { localSnapshot ->
                for (document in localSnapshot) {
                    imagenLocal = document["escudo"].toString()
                }
                // Consulta para obtener la imagen del equipo visitante
                db.collection("Equipos").whereEqualTo("nombre", visitante).get()
                    .addOnSuccessListener { visitanteSnapshot ->
                        for (document in visitanteSnapshot) {
                            imagenVisitante = document["escudo"].toString()
                        }
                        // Consulta para obtener el partido recién creado
                        db.collection("Partidos").whereEqualTo("local", local)
                            .whereEqualTo("visitante", visitante).get()
                            .addOnSuccessListener { partidoSnapshot ->
                                for (document in partidoSnapshot) {
                                    idPartido = document.id
                                }
                                // Actualizar los campos de imagenLocal e imagenVisitante del partido
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

