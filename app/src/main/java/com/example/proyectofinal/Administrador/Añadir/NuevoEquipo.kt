package com.example.proyectofinal.Administrador.Añadir

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import com.example.proyectofinal.Administrador.DrawerLayoutAdmin.DrawerLayoutAdmin
import com.example.proyectofinal.ClasesDatos.Equipos
import com.example.proyectofinal.databinding.ActivityNuevoEquipoBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.*

class NuevoEquipo : Menu_Anadir() {

    private lateinit var storage: FirebaseStorage
    private lateinit var binding: ActivityNuevoEquipoBinding

    // Instancia de la base de datos
    private val db = FirebaseFirestore.getInstance()

    // Registro del ActivityResultLauncher para seleccionar una imagen
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                // Mostrar la imagen seleccionada en el ImageView
                binding.iEscudo.setImageURI(uri)
            } else {
                // Mostrar un Toast en caso de que no se haya seleccionado ninguna imagen
                Toast.makeText(
                    this,
                    "Ha habido un error al seleccionar la imagen.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNuevoEquipoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = Firebase.storage

        // Botón "Guardar"
        binding.bGuardar.setOnClickListener {
            // Mostrar un diálogo de alerta para confirmar la acción de guardar
            val builder = AlertDialog.Builder(this)
            builder.setMessage("¿Deseas guardar el equipo?")

            // Si se ha pulsado "Guardar"
            builder.setPositiveButton("Guardar") { dialog, which ->
                comprobarEquipo()
            }

            builder.setNegativeButton("Cancelar") { dialog, which ->
                Toast.makeText(this, "Se ha cancelado la acción.", Toast.LENGTH_SHORT).show()
            }

            val dialog = builder.create()
            dialog.show()
        }

        // Cuando se hace clic en el ImageView, se lanza el selector de imágenes
        binding.iEscudo.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    // Comprobación de la existencia de algún equipo con el mismo nombre
    private fun comprobarEquipo() {
        db.collection("Equipos")
            .whereEqualTo("nombre", binding.etNombre.text.toString())
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    // Si el resultado no está vacío, significa que el equipo ya existe
                    Toast.makeText(this, "El equipo ya existe", Toast.LENGTH_SHORT).show()
                } else {
                    // Si el resultado está vacío, llama al método que quieres ejecutar en caso de que no exista el equipo
                    nuevoEquipoConImagen()
                }
            }
            .addOnFailureListener { exception ->
                // En caso de error, muestra un mensaje en el registro de la consola
                Toast.makeText(
                    this,
                    "Error al consultar la existencia del equipo.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // Método privado para guardar el equipo en Firestore y subir la imagen
    private fun nuevoEquipoConImagen() {
        val imagenVacia = binding.iEscudo.drawable
        // Si ningún campo está vacío
        if (binding.etNombre.text.isNotEmpty() && binding.etEstadio.text.isNotEmpty() && imagenVacia != null || imagenVacia is BitmapDrawable)  {
            val storageRef = storage.reference
            val rutaImagen = storageRef.child("Imagenes/equipos/" + binding.etNombre.text + ".jpeg")

            val bitmap = (binding.iEscudo.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos)
            val data = baos.toByteArray()

            val uploadTask = rutaImagen.putBytes(data)
            uploadTask.addOnFailureListener {
                Toast.makeText(this, "ERROR al guardar la foto", Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener { taskSnapshot ->
                rutaImagen.downloadUrl.addOnSuccessListener { uri ->
                    db.collection("Equipos")
                        .add(
                            mapOf(
                                "nombre" to binding.etNombre.text.toString(),
                                "estadio" to binding.etEstadio.text.toString(),
                                "escudo" to uri.toString()
                            )
                        )
                        .addOnSuccessListener { documento ->
                            Toast.makeText(this, "Equipo añadido con id: ${documento.id}", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, DrawerLayoutAdmin::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "ERROR al añadir el equipo", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        } else {
            Toast.makeText(this, "Algún campo está vacío", Toast.LENGTH_SHORT).show()
        }
    }

}
