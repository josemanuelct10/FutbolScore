package com.example.proyectofinal.Administrador.Editar

import android.R
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.proyectofinal.Administrador.DrawerLayoutAdmin.DrawerLayoutAdmin
import com.example.proyectofinal.databinding.ActivityNuevoJugadorBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class EditarJugadorFinal : Menu_Editar() {
    // Binding
    private lateinit var binding: ActivityNuevoJugadorBinding

    // Creacion de lista para lista desplegable
    private lateinit var listaEquipos: MutableList<String>

    // Instancia de la db
    private val db = FirebaseFirestore.getInstance()

    // String para después usarla en el método rellenar campos
    private lateinit var jugador: String

    // Id del equipo seleccionado
    private lateinit var idJugador: String

    // Variable para eliminar la imagen
    private val storageRef = FirebaseStorage.getInstance().reference

    // Variable para equipo seleccionado de la lista despegable
    private var equipoListaDespegable: String? = null

    private var posicionSeleccionada: String? = null

    private lateinit var listaPosiciones: List<String>


    // Seleccionar imagen
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Devuelve la uri de la imagen seleccionada
        if (uri != null) {
            // Imagen seleccionada
            binding.iJugador.setImageURI(uri)
        } else {
            // Toast en caso de que no se haya seleccionado la imagen
            Toast.makeText(this, "Ha habido un error al seleccionar la imagen.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNuevoJugadorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recogemos el putExtra de la clase EditarEquipo
        val jugadorSeleccionado = intent.getStringExtra("jugadorSeleccionado")

        // Asignamos el putExtra a la variable declarada anteriormente
        if (jugadorSeleccionado != null) {
            jugador = jugadorSeleccionado
        }

        // Inicializamos la lista de Equipos
        listaEquipos = mutableListOf()

        listaPosiciones = listOf("Portero", "Lateral Derecho", "Defensa Central", "Lateral Izquierdo", "Mediocentro Defensivo", "Mediocentro", "Mediapunta", "Extremo Izquierdo", "Extremo Derecho", "Delantero Centro")


        // Rellenamos la lista llamando al metodo
        rellenarLista()

        rellenarPosiciones()

        // Llamamos al metodo
        rellenarCampos()

        binding.bGuardar.setOnClickListener {
            // Alerta de diálogo para guardar o cancelar
            val builder = AlertDialog.Builder(this)
            builder.setMessage("¿Deseas guardar el equipo?")

            // Si se ha pulsado guardar
            builder.setPositiveButton("Guardar") { dialog, which ->
                guardarCambios()

            }

            builder.setNegativeButton("Cancelar") { dialog, which ->

            }

            val dialog = builder.create()
            dialog.show()
        }

        // Cuando pulsemos sobre el imageButton, llamaremos al launcher para lanzarlo
        binding.iJugador.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            val rutaImagen = storageRef.child("Imagenes/equipos/ " + jugador + ".jpeg")
            rutaImagen.delete()
        }
    }

    // Rellenar campos
    private fun rellenarCampos() {
        db.collection("Jugadores").whereEqualTo("nombre", jugador).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    binding.etJugador.setText(document["nombre"].toString())
                    binding.etEdad.setText(document["edad"].toString())

                    // Obtener la URL de la imagen del documento
                    val linkFoto = document["imagen"].toString()
                    idJugador = document.id

                    // Cargar la imagen en el ImageView usando Glide
                    Glide.with(this).load(linkFoto).into(binding.iJugador)
                }
            }
    }

    private fun guardarCambios() {
        val edadIntroducida = binding.etEdad.text.toString()
        val edad: Int? = edadIntroducida.toIntOrNull()

        if (binding.etJugador.text.isNotEmpty() && edad != null && edad in 15..50){
            // Verificar si el ID del equipo es nulo
            db.collection("Jugadores").document(idJugador)
                .update(
                    mapOf(
                        "nombre" to binding.etJugador.text.toString(),
                        "posicion" to posicionSeleccionada,
                        "edad" to binding.etEdad.text.toString(),
                        "equipo" to equipoListaDespegable
                    )
                )
                .addOnSuccessListener {
                    // Notificar al usuario que los cambios se han guardado exitosamente
                    Toast.makeText(this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show()
                    subirImagen()
                    // Regresar a la actividad principal
                    val intent = Intent(this, DrawerLayoutAdmin::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    // Notificar al usuario que ha ocurrido un error
                    Toast.makeText(this, "Error al guardar cambios: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        else {
            Toast.makeText(this, "Se ha dejado algún campo en blanco o la edad no es válida", Toast.LENGTH_SHORT).show()
        }

    }

    // Metodo para subir la imagen a Firestore y añadir al campo imagen de cada jugador el link de la imagen
    private fun subirImagen() {
        val rutaImagen = storageRef.child("Imagenes/jugadores/ " + binding.etJugador.text + ".jpeg")

        val bitmap = (binding.iJugador.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos)
        val data = baos.toByteArray()
        var uploadTask = rutaImagen.putBytes(data)

        uploadTask.addOnFailureListener {
            Toast.makeText(this, "ERROR al guardar la foto", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { taskSnapshot ->
            rutaImagen.downloadUrl.addOnSuccessListener {
                // Consultamos el jugador a través del nombre
                val query = db.collection("Jugadores")
                    .whereEqualTo("nombre", binding.etJugador.text.toString())
                    .limit(1)

                // Si la consulta ha salido bien, se actualiza el jugador y se añade el link
                query.get().addOnSuccessListener { result ->
                    for (document in result) {
                        document.reference.update("imagen", it.toString())
                    }
                }
            }
        }
    }

    // Método para rellenar la lista despegable
    private fun rellenarLista() {
        listaEquipos.clear() // Limpia la lista actual antes de agregar nuevos elementos

        db.collection("Equipos").get().addOnSuccessListener { documents ->
            for (document in documents) {
                val equipo = document.getString("nombre")
                if (equipo != null && !listaEquipos.contains(equipo)) {
                    listaEquipos.add(equipo)
                }
            }

            // Creamos el ArrayAdapter y lo asignamos al ListView
            val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, listaEquipos)
            binding.eEquipos.adapter = adapter
        }

        // Elección del equipo
        binding.eEquipos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                equipoListaDespegable = parent?.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // En caso de no elegir ningún equipo
            }
        }
    }

    private fun rellenarPosiciones(){
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
