package com.example.proyectofinal.Administrador.Eliminar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.proyectofinal.databinding.ActivityEliminarBinding
import com.google.firebase.firestore.FirebaseFirestore

class EliminarPartido : Menu_Eliminar() {
    private lateinit var binding: ActivityEliminarBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var listaPartidos: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEliminarBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.tvTexto1.setText("Elige un partido para eliminar:")
        binding.tvTexto2.setText("Lista de partidos:")

        // Inicializamos la lista de jugadores
        listaPartidos = mutableListOf()

        consultarPartidos()

        eliminarPartido()


    }

    // Metodo para consultar todos los partidos y meterlos en la lista
    private fun consultarPartidos() {
        listaPartidos.clear() // Limpia la lista actual antes de agregar nuevos elementos

        db.collection("Partidos").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    var partido = document.getString("local")
                    partido += " - " + document.getString("visitante")
                    if (partido != null && !listaPartidos.contains(partido)) {
                        listaPartidos.add(partido!!)
                    }
                }

                // Creamos el ArrayAdapter y lo asignamos al ListView
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaPartidos)
                binding.listado.adapter = adapter
            }
    }

    private fun eliminarPartido(){
        binding.listado.setOnItemClickListener { parent, view, position, id ->
            // Obtener el partido seleccionada
            var local = parent.getItemAtPosition(position) as String
            local = local.substringBefore(" - ")
            var visitante = parent.getItemAtPosition(position) as String
            visitante = visitante.substringAfter(" - ")
            // Realizar una consulta para buscar el partido
            db.collection("Partidos").whereEqualTo("local", local).whereEqualTo("visitante", visitante)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    // Si se encuentra el documento, eliminarlo
                    if (!querySnapshot.isEmpty) {
                        val builder = AlertDialog.Builder(this)
                        builder.setMessage("¿Deseas eliminar el partido?")
                        // Si se ha pulsado guardar
                        builder.setPositiveButton("Eliminar") { dialog, which ->
                            querySnapshot.documents.first().reference.delete()
                                .addOnSuccessListener {
                                    // Documento eliminado exitosamente
                                    Toast.makeText(this, "El partido ha sido eliminado correctamente.", Toast.LENGTH_SHORT).show()
                                    consultarPartidos()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "El partido no se ha eliminado correctamente.", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "La consulta no se ha realizado correctamente.", Toast.LENGTH_SHORT).show()
                }
        }
    }


}