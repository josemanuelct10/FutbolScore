package com.example.proyectofinal.Administrador.DrawerLayoutAdmin

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.proyectofinal.Administrador.Añadir.NuevoPartido
import com.example.proyectofinal.Administrador.Editar.EditarPartido
import com.example.proyectofinal.Administrador.Eliminar.EliminarPartido
import com.example.proyectofinal.DrawerLayout.CompeticionesFragmentDL
import com.example.proyectofinal.DrawerLayout.EquiposFragmentDL
import com.example.proyectofinal.DrawerLayout.InicioFragment
import com.example.proyectofinal.DrawerLayout.JugadoresFragmentDL
import com.example.proyectofinal.EquiposFragment
import com.example.proyectofinal.InicioSesion.MainActivity
import com.example.proyectofinal.R
import com.example.proyectofinal.databinding.ActivityDrawerLayoutAdminBinding
import com.example.proyectofinal.databinding.ActivityDrawerLayoutBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DrawerLayoutAdmin : AppCompatActivity() {

    private lateinit var toogle: ActionBarDrawerToggle
    private lateinit var binding: ActivityDrawerLayoutAdminBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawerLayoutAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        // Establecer el usuario en la interfaz de navegación
        EstablecerUsuario()

        // Configurar el ActionBarDrawerToggle para el DrawerLayout
        toogle = ActionBarDrawerToggle(
            this,
            binding.drawerLayoutAdmin,
            R.string.open_drawer,
            R.string.close_drawer
        )
        binding.drawerLayoutAdmin.addDrawerListener(toogle)
        toogle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Configurar el listener para los elementos del menú de navegación
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.admin_inicio -> {
                    // Reemplazar el fragmento actual con InicioFragment
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainerView, InicioFragment())
                        addToBackStack(null)
                        commit()
                    }
                }
                R.id.admin_anadir -> {
                    // Abrir la actividad NuevoPartido
                    startActivity(Intent(this, NuevoPartido::class.java))
                }
                R.id.admin_eliminar -> {
                    // Abrir la actividad EliminarPartido
                    startActivity(Intent(this, EliminarPartido::class.java))
                }
                R.id.admin_editar -> {
                    // Abrir la actividad EditarPartido
                    startActivity(Intent(this, EditarPartido::class.java))
                }
                R.id.admin_cerrarSesion -> {
                    // Cerrar sesión de Firebase y volver a MainActivity
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
            binding.drawerLayoutAdmin.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toogle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // Establecer en Nav_Header el nombre de usuario y la imagen del usuario
    private fun EstablecerUsuario() {
        val navigationView: NavigationView = binding.navView
        val hearderView: View = navigationView.getHeaderView(0)
        val imagenPerfil: ImageView = hearderView.findViewById(R.id.imagenPerfil)
        val userName: TextView = hearderView.findViewById(R.id.userName)

        val user = FirebaseAuth.getInstance().currentUser
        userName.text = user?.email

        val email = user?.email

        db.collection("Usuarios").document(email!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val imagenUrl = document.getString("imagen")
                    // Cargar la imagen utilizando una biblioteca como Glide o Picasso
                    Glide.with(this).load(imagenUrl).into(imagenPerfil)
                } else {
                    Log.d(ContentValues.TAG, "El documento no existe")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error obteniendo el documento: ", exception)
            }
    }
}
