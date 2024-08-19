package com.example.proyectofinal

import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinal.DrawerLayout.CompeticionesFragmentDL
import com.example.proyectofinal.DrawerLayout.DrawerLayout
import com.example.proyectofinal.DrawerLayout.EquiposFragmentDL
import com.example.proyectofinal.DrawerLayout.JugadoresFragmentDL
import com.example.proyectofinal.InicioSesion.MainActivity
import com.google.firebase.auth.FirebaseAuth

open class ActivityWithMenus : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_principal, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.inicio -> {
                val intent = Intent(this, DrawerLayout::class.java)
                startActivity(intent)
                true
            }

            R.id.cerrarSesion -> {
                FirebaseAuth.getInstance().signOut()
                // Volvemos al mainActivity
                startActivity(Intent(this, MainActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
