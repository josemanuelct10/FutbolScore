package com.example.proyectofinal.Administrador.Eliminar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.proyectofinal.R
import com.example.proyectofinal.databinding.ActivityEliminarBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EliminarJugador : Menu_Eliminar() {
    private lateinit var binding : ActivityEliminarBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var listaJugadores: MutableList<String>
    private val storageRef = FirebaseStorage.getInstance().reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEliminarBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.tvTexto1.setText("Elige un jugador para eliminar:")
        binding.tvTexto2.setText("Lista de jugadores:")

        // Inicializamos la lista de jugadores
        listaJugadores = mutableListOf()

        consultarJugadores()
        eliminarJugadores()

    }


    private fun consultarJugadores(){

        listaJugadores.clear() // Limpia la lista actual antes de agregar nuevos elementos

        db.collection("Jugadores").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    var jugador = document.getString("nombre")
                    if (jugador != null && !listaJugadores.contains(jugador)) {
                        listaJugadores.add(jugador!!)
                    }
                }

                // Creamos el ArrayAdapter y lo asignamos al ListView
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaJugadores)
                binding.listado.adapter = adapter
            }
    }

    private fun eliminarJugadores(){
        binding.listado.setOnItemClickListener { parent, view, position, id ->
            // Obtener la competición seleccionada
            val jugador = parent.getItemAtPosition(position) as String

            // Asignamos la ruta de la imagen
            val rutaImagen = storageRef.child("Imagenes/jugadores/ " + jugador + ".jpeg")

            // Realizar una consulta para buscar el documento con el nombre del jugador
            db.collection("Jugadores").whereEqualTo("nombre", jugador).get()
                .addOnSuccessListener { querySnapshot ->
                    // Si se encuentra el documento, eliminarlo
                    if (!querySnapshot.isEmpty) {
                        val builder = AlertDialog.Builder(this)
                        builder.setMessage("¿Deseas eliminar el jugador?")

                        // Si se ha pulsado guardar
                        builder.setPositiveButton("Eliminar") { dialog, which ->
                            querySnapshot.documents.first().reference.delete()
                                .addOnSuccessListener {
                                    // Documento eliminado exitosamente
                                    Toast.makeText(this, "El jugador ha sido eliminado correctamente.", Toast.LENGTH_SHORT).show()
                                    // Eliminar Imagen
                                    rutaImagen.delete()
                                    // Volver a cargar la lista
                                    consultarJugadores()
                                }
                                .addOnFailureListener { e -> Toast.makeText(this, "El jugador no se ha eliminado correctamente.", Toast.LENGTH_SHORT).show() }
                        }
                        builder.setNegativeButton("Cancelar"){dialog, which ->
                            Toast.makeText(this,"Se ha cancelado la accion.", Toast.LENGTH_SHORT).show()
                        }

                        val dialog = builder.create()
                        dialog.show()
                    }
                }
                .addOnFailureListener { e ->
                    // Ocurrió un error al realizar la consulta
                    Toast.makeText(this,"La consulta no se ha realizado correctamente.", Toast.LENGTH_SHORT).show()
                }
        }
    }


}