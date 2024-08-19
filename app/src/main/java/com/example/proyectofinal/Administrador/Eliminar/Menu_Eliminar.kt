package com.example.proyectofinal.Administrador.Eliminar


import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinal.*
import com.example.proyectofinal.Administrador.Añadir.Menu_Anadir
import com.example.proyectofinal.Administrador.DrawerLayoutAdmin.DrawerLayoutAdmin
import com.example.proyectofinal.InicioSesion.MainActivity
import com.google.firebase.auth.FirebaseAuth

open class Menu_Eliminar : AppCompatActivity() {

    companion object {
        private var actividadActual = R.id.eliminarPartido
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_eliminar, menu)
        for (i in 0 until menu.size()) {
            menu.getItem(i).isEnabled = (menu.getItem(i).itemId != Menu_Eliminar.actividadActual)
        }
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.inicioAdmin ->{
                val intent = Intent(this, DrawerLayoutAdmin::class.java)
                startActivity(intent)
                actividadActual = R.id.eliminarPartido
                true
            }
            R.id.eliminarJugador -> {
                val intent = Intent(this, EliminarJugador::class.java)
                Menu_Eliminar.actividadActual = R.id.eliminarJugador
                startActivity(intent)
                true
            }
            R.id.eliminarEquipo -> {
                val intent = Intent(this, EliminarEquipo::class.java)
                Menu_Eliminar.actividadActual = R.id.eliminarEquipo
                startActivity(intent)
                true
            }
            R.id.eliminarPartido -> {
                startActivity(Intent(this, EliminarPartido::class.java))
                actividadActual = R.id.eliminarPartido
                true
            }

            R.id.cerrarSesionAdmin -> {
                FirebaseAuth.getInstance().signOut()
                // Volvemos al mainActivity
                startActivity(Intent(this, MainActivity::class.java))
                actividadActual = R.id.eliminarPartido
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
