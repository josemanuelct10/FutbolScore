package com.example.proyectofinal.Administrador.Editar

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.proyectofinal.Administrador.DrawerLayoutAdmin.DrawerLayoutAdmin
import com.example.proyectofinal.databinding.ActivityNuevoEquipoBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class EditarEquipoFinal : Menu_Editar() {
    // Binding
    private lateinit var binding: ActivityNuevoEquipoBinding

    // Instancia de la db
    private val db = FirebaseFirestore.getInstance()

    // String para despues usarla en el metodo rellenar campos
    private lateinit var equipo: String

    // Id del equipo seleccionado
    private lateinit var idEquipo: String

    // Variable para eliminar la imagen
    private val storageRef = FirebaseStorage.getInstance().reference

    // Seleccionar imagen
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            // Imagen seleccionada
            binding.iEscudo.setImageURI(uri)
        } else {
            // Toast en caso de que no se haya seleccionado la imagen
            Toast.makeText(this, "Ha habido un error al seleccionar la imagen.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNuevoEquipoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recogemos el putExtra de la clase EditarEquipo
        val equipoSeleccionado = intent.getStringExtra("equipoSeleccionado")

        // Asignamos el putExtra a la variable declarada anteriormente
        if (equipoSeleccionado != null) {
            equipo = equipoSeleccionado
        }

        // Llamamos al metodo
        rellenarCampos()

        binding.bGuardar.setOnClickListener {
            // Alerta de dialogo para guardar o cancelar
            val builder = AlertDialog.Builder(this)
            builder.setMessage("¿Deseas guardar el equipo?")

            // Si se ha pulsado guardar
            builder.setPositiveButton("Guardar") { dialog, which ->
                guardarCambios()

            }

            builder.setNegativeButton("Cancelar") { dialog, which ->
                // Acción al pulsar el botón Cancelar
            }

            val dialog = builder.create()
            dialog.show()
        }

        // Cuando pulsemos sobre el imageButton, llamaremos al launcher para lanzarlo
        binding.iEscudo.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            val rutaImagen = storageRef.child("Imagenes/equipos/ " + equipo + ".jpeg")
            rutaImagen.delete()
        }
    }

    // Rellenar campos
    private fun rellenarCampos() {
        db.collection("Equipos").whereEqualTo("nombre", equipo).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    binding.etNombre.setText(document["nombre"].toString())
                    binding.etEstadio.setText(document["estadio"].toString())
                    // Obtener la URL de la imagen del documento
                    val linkFoto = document["escudo"].toString()
                    idEquipo = document.id

                    // Cargar la imagen en el ImageView usando Glide
                    Glide.with(this).load(linkFoto).into(binding.iEscudo)
                }
            }
    }

    // Metodo para actualizar los campos en firebase
    private fun guardarCambios() {
        if (binding.etEstadio.text.isNotEmpty() && binding.etNombre.text.isNotEmpty()){
            db.collection("Equipos").document(idEquipo)
                .update(mapOf(
                    "nombre" to binding.etNombre.text.toString(),
                    "estadio" to binding.etEstadio.text.toString()
                ))
                .addOnSuccessListener {
                    // Notificar al usuario que los cambios se han guardado exitosamente
                    Toast.makeText(this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show()
                    // Regresar a la actividad principal
                    val intent = Intent(this, DrawerLayoutAdmin::class.java)
                    startActivity(intent)
                    subirImagen()
                }
                .addOnFailureListener { e ->
                    // Notificar al usuario que ha ocurrido un error
                    Toast.makeText(this, "Error al guardar cambios: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
        else {
            Toast.makeText(this, "Se han dejado campos en blanco.", Toast.LENGTH_SHORT).show()

        }

    }

    // Metodo para subir la imagen a Firestore y añadir al campo imagen de cada equipo el link de la imagen
    private fun subirImagen() {
        val rutaImagen = storageRef.child("Imagenes/equipos/ " + binding.etNombre.text + ".jpeg")

        val bitmap = (binding.iEscudo.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos)
        val data = baos.toByteArray()
        var uploadTask = rutaImagen.putBytes(data)

        uploadTask.addOnFailureListener {
            Toast.makeText(this, "ERROR al guardar la foto", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { taskSnapshot ->
            rutaImagen.downloadUrl.addOnSuccessListener {
                // Consultamos el equipo a través del nombre
                val query = db.collection("Equipos")
                    .whereEqualTo("nombre", binding.etNombre.text.toString())
                    .limit(1)

                // Si la consulta ha salido bien, se actualiza el jugador y se añade el enlace
                query.get().addOnSuccessListener { result ->
                    for (document in result) {
                        document.reference.update("escudo", it.toString())
                    }
                }
            }
        }
    }
}
