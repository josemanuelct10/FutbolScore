package com.example.proyectofinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.proyectofinal.Adapter.JugadoresAdapter
import com.example.proyectofinal.Adapter.PartidosAdapter
import com.example.proyectofinal.ClasesDatos.Equipos
import com.example.proyectofinal.ClasesDatos.Jugadores
import com.example.proyectofinal.ClasesDatos.Partidos
import com.example.proyectofinal.databinding.ActivityMostrarJugadorBinding
import com.example.proyectofinal.databinding.ActivityPartidosCompeticionesBinding
import com.google.firebase.firestore.FirebaseFirestore

class MostrarJugador : ActivityWithMenus() {

    // Creamos la instancia de la base de datos
    private val db = FirebaseFirestore.getInstance()

    // Instanciamos el adapter
    private lateinit var adapterJugadores: JugadoresAdapter

    // Creamos la variable binding
    private lateinit var binding: ActivityMostrarJugadorBinding

    // Creamos el array de jugadores
    private lateinit var listaJugadores: ArrayList<Jugadores>

    // Instanciamos el adapter
    private lateinit var adapterPartidos: PartidosAdapter

    // Creamos el array de partidos
    private lateinit var listaPartidos: ArrayList<Partidos>

    override fun onCreate(savedInstanceState: Bundle?) {
        val searchValue = intent.getStringExtra("searchValue")
        val valor = intent.getStringExtra("jugador")
        super.onCreate(savedInstanceState)

        // Binding
        binding = ActivityMostrarJugadorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Instancia del array de jugadores
        listaJugadores = ArrayList()

        // Instanciamos el array de partidos
        listaPartidos = ArrayList()

        // Asignacion del array al adapter
        adapterJugadores = JugadoresAdapter(listaJugadores)

        // Asignamos el array al adapter
        adapterPartidos = PartidosAdapter(listaPartidos)

        // Variable donde se guarda el equipo del jugador seleccionado
        var equipoJugador: String = ""

        // Aqui consultamos todos los jugadores y los añadimos al array de jugadores
        val query = if (searchValue != null) {
            db.collection("Jugadores").whereEqualTo("nombre", searchValue)
        } else {
            db.collection("Jugadores").whereEqualTo("nombre", valor)
        }

        query.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val jugador = document.toObject(Jugadores::class.java)
                jugador.nombre = document["nombre"].toString()
                jugador.equipo = document["equipo"].toString()
                jugador.posicion = document["posicion"].toString()
                jugador.edad = document["edad"].toString()
                jugador.imagen = document["imagen"].toString()

                equipoJugador = jugador.equipo

                listaJugadores.add(jugador)
            }

            // Aqui asignamos al recyclerview el adapter
            binding.recyclerJugadores.adapter = adapterJugadores
            binding.recyclerJugadores.layoutManager = LinearLayoutManager(this)

            // Consulta de los partidos donde el equipo es local
            db.collection("Partidos").whereEqualTo("local", equipoJugador).get()
                .addOnSuccessListener { localDocuments ->
                    for (localDocument in localDocuments) {
                        val partido = localDocument.toObject(Partidos::class.java)
                        partido.local = localDocument["local"].toString()
                        partido.visitante = localDocument["visitante"].toString()
                        partido.imagenLocal = localDocument["imagenLocal"].toString()
                        partido.imagenVisitante = localDocument["imagenVisitante"].toString()
                        partido.competicion = localDocument["competicion"].toString()
                        partido.resultado = localDocument["resultado"].toString()

                        listaPartidos.add(partido)
                    }

                    // Consulta de los partidos donde el equipo es visitante
                    db.collection("Partidos").whereEqualTo("visitante", equipoJugador).get()
                        .addOnSuccessListener { visitanteDocuments ->
                            for (visitanteDocument in visitanteDocuments) {
                                val partido = visitanteDocument.toObject(Partidos::class.java)
                                partido.local = visitanteDocument["local"].toString()
                                partido.visitante = visitanteDocument["visitante"].toString()
                                partido.imagenLocal = visitanteDocument["imagenLocal"].toString()
                                partido.imagenVisitante = visitanteDocument["imagenVisitante"].toString()
                                partido.competicion = visitanteDocument["competicion"].toString()
                                partido.resultado = visitanteDocument["resultado"].toString()

                                listaPartidos.add(partido)
                            }

                            // Aquí asignamos al RecyclerView el adapter
                            binding.listaPartidos.adapter = adapterPartidos
                            binding.listaPartidos.layoutManager = LinearLayoutManager(this)
                        }
                }
        }
    }
}

