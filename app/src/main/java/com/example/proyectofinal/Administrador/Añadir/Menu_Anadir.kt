package com.example.proyectofinal.Administrador.Añadir

import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinal.*
import com.example.proyectofinal.Administrador.DrawerLayoutAdmin.DrawerLayoutAdmin
import com.example.proyectofinal.Administrador.Editar.Menu_Editar
import com.example.proyectofinal.InicioSesion.MainActivity
import com.google.firebase.auth.FirebaseAuth

open class Menu_Anadir : AppCompatActivity() {

    companion object {
        var actividadActual = R.id.nuevoPartido
    }

    // Se llama cuando se crea el menú de opciones
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflar el menú de opciones desde el archivo menu_anadir.xml
        menuInflater.inflate(R.menu.menu_anadir, menu)

        // Habilitar o deshabilitar elementos del menú según la actividad actual
        for (i in 0 until menu.size()) {
            menu.getItem(i).isEnabled = (menu.getItem(i).itemId != Menu_Anadir.actividadActual)
        }
        return true
    }

    // Se llama cuando se selecciona un elemento del menú de opciones
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.inicioAdmin -> {
                // Iniciar la actividad DrawerLayoutAdmin
                val intent = Intent(this, DrawerLayoutAdmin::class.java)
                actividadActual = R.id.nuevoPartido
                startActivity(intent)
                true
            }
            R.id.nuevoEquipo -> {
                // Iniciar la actividad NuevoEquipo
                val intent = Intent(this, NuevoEquipo::class.java)
                // Establecer la actividad actual como "nuevoEquipo"
                actividadActual = R.id.nuevoEquipo
                startActivity(intent)
                true
            }
            R.id.nuevoJugador -> {
                // Iniciar la actividad NuevoJugador
                val intent = Intent(this, NuevoJugador::class.java)
                // Establecer la actividad actual como "nuevoJugador"
                actividadActual = R.id.nuevoJugador
                startActivity(intent)
                true
            }
            R.id.nuevoPartido -> {
                // Establecer la actividad actual como "nuevoPartido"
                val intent = Intent(this, NuevoPartido::class.java)
                // Establecer la actividad actual como "nuevoJugador"
                actividadActual = R.id.nuevoPartido
                startActivity(intent)
                true
            }
            R.id.cerrarSesionAdmin -> {
                // Cerrar la sesión de Firebase y volver a la actividad MainActivity
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, MainActivity::class.java))
                actividadActual = R.id.nuevoPartido
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

