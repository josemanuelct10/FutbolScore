package com.example.proyectofinal.Administrador.Añadir

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.proyectofinal.Administrador.DrawerLayoutAdmin.DrawerLayoutAdmin
import com.example.proyectofinal.ClasesDatos.Equipos
import com.example.proyectofinal.ClasesDatos.Jugadores
import com.example.proyectofinal.databinding.ActivityNuevoJugadorBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader

class NuevoJugador : Menu_Anadir() {


    // Variables
    private lateinit var imagen: ImageButton
    private lateinit var storage: FirebaseStorage
    private lateinit var binding: ActivityNuevoJugadorBinding
    private var equipoSeleccionado: String? = null // Declarar la variable aquí
    private var posicionSeleccionada: String? = null

    // Creacion de lista para lista desplegable
    private lateinit var listaEquipos: MutableList<String>

    private lateinit var listaPosiciones: List<String>

    // Instancia de la base de datos
    private val db = FirebaseFirestore.getInstance()

    // Seleccionar Imagen
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Devuelve la URI de la imagen seleccionada
        if (uri != null) {
            // Imagen seleccionada
            imagen.setImageURI(uri)
        } else {
            // Toast en caso de que no se haya seleccionado la imagen
            Toast.makeText(this, "Ha habido un error al seleccionar la imagen.", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNuevoJugadorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializamos la lista de Equipos
        listaEquipos = mutableListOf()


        // Rellenamos la lista llamando al método
        rellenarLista()
        rellenarPosiciones()

        imagen = binding.iJugador

        storage = Firebase.storage

        binding.bGuardar.setOnClickListener {

            // Alerta de diálogo para guardar o cancelar
            val builder = AlertDialog.Builder(this)
            builder.setMessage("¿Deseas guardar el jugador?")

            // Si se ha pulsado guardar
            builder.setPositiveButton("Guardar") { dialog, which ->
                comprobarJugador()
            }

            builder.setNegativeButton("Cancelar") { dialog, which ->
                Toast.makeText(this, "Se ha cancelado la acción", Toast.LENGTH_SHORT).show()
            }

            val dialog = builder.create()
            dialog.show()
        }

        // Cuando pulsemos sobre el ImageButton, llamaremos al launcher para lanzarlo
        binding.iJugador.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    // Método para comprobar si existe algún jugador con ese nombre
    private fun comprobarJugador() {
        db.collection("Jugadores")
            .whereEqualTo("nombre", binding.etJugador.text.toString())
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    // Si el resultado no está vacío, significa que el jugador ya existe
                    Toast.makeText(this, "El jugador ya existe", Toast.LENGTH_SHORT).show()
                } else {
                    // Si el resultado está vacío, llama al método que quieres ejecutar en caso de que no exista el jugador
                    nuevoJugador()
                }
            }
            .addOnFailureListener { exception ->
                // En caso de error, muestra un mensaje en el registro de la consola
                Toast.makeText(this, "Error al consultar jugadores", Toast.LENGTH_SHORT).show()
            }
    }

    // Creación del jugador en Firestore
    private fun nuevoJugador() {
        val edadIntroducida = binding.etEdad.text.toString()
        val edad: Int? = edadIntroducida.toIntOrNull()

        val imagenVacia = binding.iJugador.drawable

        if (binding.etJugador.text.isNotEmpty() && edad != null && edad in 15..50 && (imagenVacia != null || imagenVacia is BitmapDrawable)) {
            db.collection("Jugadores")
                .add(
                    mapOf(
                        "edad" to edad.toString(),
                        "equipo" to equipoSeleccionado,
                        "nombre" to binding.etJugador.text.toString(),
                        "posicion" to posicionSeleccionada,
                        "imagen" to ""
                    )
                )
                .addOnSuccessListener {
                    Toast.makeText(this, "Jugador añadido correctamente", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, DrawerLayoutAdmin::class.java)
                    startActivity(intent)
                    subirImagen()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "ERROR al añadir jugador", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Algún campo está vacío o la edad no esta entre 15 y 50 años.", Toast.LENGTH_SHORT).show()
        }
    }

    // Rellenar lista de equipos
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
                binding.eEquipos.adapter = adapter
            }

        // Elección del equipo
        binding.eEquipos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                equipoSeleccionado = parent?.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // En caso de no elegir ningún equipo
            }
        }
    }

    // Método para subir la imagen a Firestore y añadir al campo "imagen" de cada jugador el enlace de la imagen
    private fun subirImagen() {
        val storageRef = storage.reference
        val jugador = binding.etJugador.text.toString()
        val rutaImagen = storageRef.child("Imagenes/jugadores/$jugador.jpeg")

        // Redimensionar la imagen a un tamaño específico
        val bitmap = (binding.iJugador.drawable as BitmapDrawable).bitmap
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, false)

        // Convertir el bitmap a un array de bytes para subirlo a Firebase Storage
        val baos = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos)
        val data = baos.toByteArray()

        // Subir la imagen a Firebase Storage y obtener el enlace de descarga
        val uploadTask = rutaImagen.putBytes(data)

        uploadTask.addOnFailureListener {
            Toast.makeText(this, "ERROR al guardar la foto", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { taskSnapshot ->
            rutaImagen.downloadUrl.addOnSuccessListener { uri ->
                val imagen = uri.toString()

                // Actualizar el campo "imagen" del jugador en Firestore con el enlace de descarga
                val query = db.collection("Jugadores")
                    .whereEqualTo("nombre", jugador)
                    .limit(1)

                query.get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            document.reference.update("imagen", imagen)
                        }
                    }
            }
        }
    }


    private fun rellenarPosiciones(){
        listaPosiciones = listOf("Portero", "Lateral Derecho", "Defensa Central", "Lateral Izquierdo", "Mediocentro Defensivo", "Mediocentro", "Mediapunta", "Extremo Izquierdo", "Extremo Derecho", "Delantero Centro")

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaPosiciones)
        binding.listaPosiciones.adapter = adapter

        // Elección del equipo
        binding.listaPosiciones.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                posicionSeleccionada = parent?.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // En caso de no elegir ningún equipo
            }
        }
    }


}
