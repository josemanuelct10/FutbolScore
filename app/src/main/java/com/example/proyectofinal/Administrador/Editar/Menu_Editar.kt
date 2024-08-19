package com.example.proyectofinal.Administrador.Editar

import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinal.Administrador.Añadir.NuevoEquipo
import com.example.proyectofinal.Administrador.Añadir.NuevoJugador
import com.example.proyectofinal.Administrador.Añadir.NuevoPartido
import com.example.proyectofinal.Administrador.DrawerLayoutAdmin.DrawerLayoutAdmin
import com.example.proyectofinal.InicioSesion.MainActivity
import com.example.proyectofinal.R
import com.google.firebase.auth.FirebaseAuth

open class Menu_Editar : AppCompatActivity() {
    companion object {
       private var actividadActual = R.id.editarPartido
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_editar, menu)
        for (i in 0 until menu.size()) {
            menu.getItem(i).isEnabled = (menu.getItem(i).itemId != actividadActual)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.inicioAdmin ->{
                val intent = Intent(this, DrawerLayoutAdmin::class.java)
                startActivity(intent)
                actividadActual = R.id.editarPartido
                true
            }
            R.id.editarEquipo -> {
                val intent = Intent(this, EditarEquipo::class.java)
                actividadActual = R.id.editarEquipo
                startActivity(intent)
                true
            }
            R.id.editarJugador -> {
                val intent = Intent(this, EditarJugador::class.java)
                actividadActual = R.id.editarJugador
                startActivity(intent)
                true
            }
            R.id.editarPartido -> {
                val intent = Intent(this, EditarPartido::class.java)
                actividadActual = R.id.editarPartido
                startActivity(intent)
                true
            }
            R.id.cerrarSesionAdmin -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, MainActivity::class.java))
                actividadActual = R.id.editarPartido
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
