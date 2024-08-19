package com.example.proyectofinal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal.Adapter.PartidosAdapter
import com.example.proyectofinal.ClasesDatos.Partidos
import com.example.proyectofinal.DrawerLayout.CompeticionesFragmentDL
import com.example.proyectofinal.FragmentTabLayout.PartidosFragment
import com.example.proyectofinal.databinding.ActivityPartidosCompeticionesBinding
import com.google.firebase.firestore.FirebaseFirestore

class MostrarPartidosCompeticiones() : ActivityWithMenus() {

    // Creamos la instancia de la base de datos
    private val db = FirebaseFirestore.getInstance()

    // Instanciamos el adapter
    private lateinit var adapterPartidos: PartidosAdapter

    // Creamos el array de jugadores
    private lateinit var listaPartidos: ArrayList<Partidos>

    // Creamos la variable binding
    private lateinit var binding : ActivityPartidosCompeticionesBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        val searchValue = intent.getStringExtra("searchValue")
        val valor = intent.getStringExtra("competicion")
        super.onCreate(savedInstanceState)

        // Binding
        binding = ActivityPartidosCompeticionesBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Instanciamos el array de partidos
        listaPartidos = ArrayList()

        // Asignamos el array al adapter
        adapterPartidos = PartidosAdapter(listaPartidos)



        // Aqui consultamos todos los partidos de la competicion buscada en el buscador y los añadimos al array de partidos
        if (searchValue != null){
            db.collection("Partidos").whereEqualTo("competicion", searchValue ).get()
                .addOnSuccessListener { documents ->
                    for (document in documents){
                        val partido = document.toObject(Partidos::class.java)
                        partido.local = document["local"].toString()
                        partido.visitante = document["visitante"].toString()
                        partido.imagenLocal = document["imagenLocal"].toString()
                        partido.imagenVisitante = document["imagenVisitante"].toString()
                        partido.competicion = document["competicion"].toString()
                        partido.resultado = document["resultado"].toString()
                        listaPartidos.add(partido)

                        // Aqui asignamos al recyclerview el adapter
                        binding.listadoPartidosFiltrados.adapter = adapterPartidos
                        binding.listadoPartidosFiltrados.layoutManager = LinearLayoutManager(this)
                    }

                }
        }
        else{
            // Aqui consultamos todos los partidos de la competicion elegida en el ListView en el buscador y los añadimos al array de partidos
            db.collection("Partidos").whereEqualTo("competicion", valor ).get()
                .addOnSuccessListener { documents ->
                    for (document in documents){
                        val partido = document.toObject(Partidos::class.java)
                        partido.local = document["local"].toString()
                        partido.visitante = document["visitante"].toString()
                        partido.imagenLocal = document["imagenLocal"].toString()
                        partido.imagenVisitante = document["imagenVisitante"].toString()
                        partido.competicion = document["competicion"].toString()
                        partido.resultado = document["resultado"].toString()
                        listaPartidos.add(partido)

                        // Aqui asignamos al recyclerview el adapter
                        binding.listadoPartidosFiltrados.adapter = adapterPartidos
                        binding.listadoPartidosFiltrados.layoutManager = LinearLayoutManager(this)
                    }

                }
        }

    }

}

