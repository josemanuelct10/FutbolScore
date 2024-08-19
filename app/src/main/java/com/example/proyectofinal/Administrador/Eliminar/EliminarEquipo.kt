package com.example.proyectofinal.Administrador.Eliminar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.proyectofinal.databinding.ActivityEliminarBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EliminarEquipo : Menu_Eliminar() {

    // Variables
    private lateinit var binding : ActivityEliminarBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var listaEquipos: MutableList<String>
    private val storageRef = FirebaseStorage.getInstance().reference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEliminarBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.tvTexto1.setText("Elige un equipo para eliminar:")
        binding.tvTexto2.setText("Lista de equipos:")

        // Inicializamos la lista de Equipos
        listaEquipos = mutableListOf()

        consultaEquipos()

        eliminarEquipo()

    }

    private fun eliminarEquipo(){
        binding.listado.setOnItemClickListener { parent, view, position, id ->
            // Obtener la competición seleccionada
            val equipo = parent.getItemAtPosition(position) as String

            // Asignamos la ruta de la imagen
            val rutaImagen = storageRef.child("Imagenes/equipos/ " + equipo + ".jpeg")

            // Realizar una consulta para buscar el documento con el nombre del equipo
            db.collection("Equipos")
                .whereEqualTo("nombre", equipo)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    // Si se encuentra el documento, eliminarlo
                    if (!querySnapshot.isEmpty) {
                        val builder = AlertDialog.Builder(this)
                        builder.setMessage("¿Deseas eliminar el equipo?")

                        // Si se ha pulsado guardar
                        builder.setPositiveButton("Eliminar"){ dialog, which ->
                            querySnapshot.documents.first().reference.delete()
                                .addOnSuccessListener {
                                    // Documento eliminado exitosamente
                                    Toast.makeText(this,"El equipo ha sido eliminado correctamente.", Toast.LENGTH_SHORT).show()
                                    // Eliminamos la imagen
                                    rutaImagen.delete()
                                    consultaEquipos()
                                    eliminarPartidos(equipo)
                                    actualizarJugador(equipo)
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this,"El equipo no se ha eliminado correctamente.", Toast.LENGTH_SHORT).show()

                                }
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

    private fun consultaEquipos() {
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
                binding.listado.adapter = adapter

            }
    }


    private fun eliminarPartidos(equipo: String){
        db.collection("Partidos").whereEqualTo("local", equipo).get()
            .addOnSuccessListener { partidosQuerySnapshot ->
                // Eliminar los jugadores encontrados
                for (partidoDocument in partidosQuerySnapshot.documents) {
                    partidoDocument.reference.delete()
                }
            }
            .addOnFailureListener { e ->
                // Ocurrió un error al eliminar los jugadores
                Toast.makeText(this, "Error al eliminar los partidos del equipo.", Toast.LENGTH_SHORT).show()
            }
        db.collection("Partidos").whereEqualTo("visitante", equipo).get()
            .addOnSuccessListener { partidosQuerySnapshot ->
                // Eliminar los jugadores encontrados
                for (partidoDocument in partidosQuerySnapshot.documents) {
                    partidoDocument.reference.delete()
                }
            }
            .addOnFailureListener { e ->
                // Ocurrió un error al eliminar los jugadores
                Toast.makeText(this, "Error al eliminar los partidos del equipo.", Toast.LENGTH_SHORT).show()
            }

    }


    private fun actualizarJugador(equipo: String) {
        db.collection("Jugadores")
            .whereEqualTo("equipo", equipo)
            .get()
            .addOnSuccessListener { jugadoresQuerySnapshot ->
                // Actualizar los jugadores encontrados asignándoles un nuevo equipo vacío
                for (jugadorDocument in jugadoresQuerySnapshot.documents) {
                    jugadorDocument.reference.update("equipo", "Sin equipo")
                }
            }
            .addOnFailureListener { e ->
                // Ocurrió un error al eliminar los jugadores
                Toast.makeText(this, "Error al eliminar los jugadores del equipo.", Toast.LENGTH_SHORT).show()
            }
    }

}