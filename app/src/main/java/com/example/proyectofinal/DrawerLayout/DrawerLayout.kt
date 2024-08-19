package com.example.proyectofinal.DrawerLayout

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
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
import com.example.proyectofinal.EquiposFragment
import com.example.proyectofinal.InicioSesion.MainActivity
import com.example.proyectofinal.R
import com.example.proyectofinal.databinding.ActivityDrawerLayoutBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DrawerLayout : AppCompatActivity(), EquiposFragmentDL.OnButtonClickListener {

    private lateinit var toogle: ActionBarDrawerToggle
    private lateinit var binding : ActivityDrawerLayoutBinding
    private val db = FirebaseFirestore.getInstance()
    var data: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawerLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Establecer el usuario
        EstablecerUsuario()

        // Configuración del ActionBarDrawerToggle
        toogle = ActionBarDrawerToggle(this, binding.drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        binding.drawerLayout.addDrawerListener(toogle)
        toogle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Configuración del NavigationView
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_inicio -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainerView, InicioFragment())
                        addToBackStack(null)
                        commit()
                    }
                }
                R.id.nav_jugadores -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainerView, JugadoresFragmentDL())
                        addToBackStack(null)
                        commit()
                    }
                }
                R.id.nav_competiciones -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainerView, CompeticionesFragmentDL())
                        addToBackStack(null)
                        commit()
                    }
                }
                R.id.nav_equipos -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainerView, EquiposFragmentDL())
                        addToBackStack(null)
                        commit()
                    }
                }
                R.id.nav_soporte ->{
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        // type = "text/plain"
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf("josemanuel30503@hotmail.com"))
                        putExtra(Intent.EXTRA_SUBJECT, "FutbolScore")
                    }
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    }

                }
                R.id.nav_cerrarSesion -> {
                    FirebaseAuth.getInstance().signOut()
                    // Volvemos al MainActivity
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
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
        val headerView: View = navigationView.getHeaderView(0)
        val imagenPerfil: ImageView = headerView.findViewById(R.id.imagenPerfil)
        val userName: TextView = headerView.findViewById(R.id.userName)

        val user = FirebaseAuth.getInstance().currentUser
        userName.text = user?.email

        val email = user?.email

        db.collection("Usuarios").document(email!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val imageUrl = document.getString("imagen")
                    // Cargar la imagen utilizando una biblioteca como Glide o Picasso
                    Glide.with(this).load(imageUrl).into(imagenPerfil)
                } else {
                    Log.d(ContentValues.TAG, "El documento no existe")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error obteniendo el documento: ", exception)
            }
    }

    override fun onButtonClick(data: String) {
        // Aquí recibes la información del fragmento origen y puedes pasarla al fragmento destino
        val fragment = EquiposFragment()
        val bundle = Bundle()
        bundle.putString("data", data)
        fragment.arguments = bundle

        // Realiza la transacción
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun tabLayout(data: String) {
        this.data = data
    }
}
