package com.example.proyectofinal.InicioSesion

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.proyectofinal.DrawerLayout.DrawerLayout
import com.example.proyectofinal.databinding.ActivityRegistroBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class RegistroActivity : AppCompatActivity() {

    // Variables para usar mas adelante
    private lateinit var binding: ActivityRegistroBinding
    private lateinit var imagen: ImageButton
    private lateinit var storage: FirebaseStorage
    private val db = FirebaseFirestore.getInstance()


    // Proceso para seleccionar la foto de la galeria
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){
        // Devuelve la uri de la imagen seleccionada
            uri ->
        if(uri!=null){
            // Imagen seleccionada
            imagen.setImageURI(uri)
        }
        else{
            // Toast en caso de que no se ha seleccinada la imagen
            Toast.makeText(this, "Ha habido un error al seleccionar la imagen.", Toast.LENGTH_SHORT).show()

        }
    }

    // Variable para abrir la camara
    private val pickFoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        val image = it.data?.extras?.get("data") as Bitmap
        binding.iUsuario.setImageBitmap(image)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Instancia de Storage
        storage = Firebase.storage

        imagen = binding.iUsuario


        // Cuando pulsemos sobre el imageButton, llamaremos al launcher para lanzarlo
        binding.iUsuario.setOnClickListener{
            mostrarOpciones()

        }



        binding.bRegistroR.setOnClickListener {

            // Registrar Usuarios
            registrarUsuario()

            // Subir imagen
            subirImagen()
        }



    }
    // Metodo para subir la imagen a firestore y añadir al campo imagen de cada usuario el link de la imagen
    private fun subirImagen(){
        val storageRef = storage.reference
        val rutaImagen = storageRef.child("Imagenes/usuarios/ " + binding.etNombre.text + binding.etApellidos.text + ".jpeg")

        val bitmap = (binding.iUsuario.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos)
        val data = baos.toByteArray()
        var uploadTask = rutaImagen.putBytes(data)

        uploadTask.addOnFailureListener{
            Toast.makeText(this, "ERROR al guardar la foto", Toast.LENGTH_SHORT).show()


        }.addOnSuccessListener { taskSnapshot ->
            rutaImagen.downloadUrl.addOnSuccessListener {
                // Consultamos el usuario a través del nombre
                val query = db.collection("Usuarios")
                    .whereEqualTo("email", binding.etEmailRegistro.text.toString())
                    .limit(1)

                // Si la consulta ha salido bien se actualiza el jugador y se añade el link
                query.get()
                    .addOnSuccessListener { result ->
                        for (document in result){
                            document.reference.update("imagen",it.toString())
                        }
                    }
            }
        }
    }

    private fun mostrarOpciones(){
        val opciones = arrayOf<CharSequence>("Tomar foto", "Elegir de galería", "Cancelar")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Elige una opción")
        builder.setItems(opciones) { dialog, opcion ->
            when (opcion) {
                0 -> pickFoto.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                1 -> pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                2 -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun registrarUsuario(){
        if(binding.etEmailRegistro.text.isNotEmpty() && binding.etPasswordRegistro.text.isNotEmpty()
            && binding.etNombre.text.isNotEmpty() && binding.etApellidos.text.isNotEmpty()){
            // Creacion del usuario y contraseña
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                binding.etEmailRegistro.text.toString(), binding.etPasswordRegistro.text.toString()
            ).addOnCompleteListener{
                if(it.isSuccessful){
                    // Añadimos los demas campos a la tabla de usuarios
                    db.collection("Usuarios").document(binding.etEmailRegistro.text.toString())
                        .set(mapOf(
                            "nombre" to binding.etNombre.text.toString(),
                            "apellidos" to binding.etApellidos.text.toString(),
                            "email" to binding.etEmailRegistro.text.toString(),
                            "imagen" to ""
                        ))

                    // Cambiamos a la activity del recycler
                    val intent = Intent(this, DrawerLayout::class.java)
                    startActivity(intent)
                    // Toast en caso de error al registro
                }else{Toast.makeText(this, "Error en el registro del nuevo usuario", Toast.LENGTH_SHORT).show()}
            }

        }else{
            // Toast en caso de que este algun campo vacío
            Toast.makeText(this,"Algún campo está vacío", Toast.LENGTH_SHORT).show()
        }
    }



}