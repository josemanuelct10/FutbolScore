package com.example.proyectofinal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.proyectofinal.Adapter.AdapterTabLayout
import com.example.proyectofinal.Adapter.JugadoresAdapter
import com.example.proyectofinal.ClasesDatos.Equipos
import com.example.proyectofinal.ClasesDatos.Jugadores
import com.example.proyectofinal.DrawerLayout.DrawerLayout
import com.example.proyectofinal.FragmentTabLayout.JugadoresFragment
import com.example.proyectofinal.databinding.FragmentEquiposBinding
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore


class EquiposFragment: Fragment(R.layout.fragment_equipos) {

    private var _binding: FragmentEquiposBinding? = null
    private val binding get() = _binding!!

    // Creamos la instancia de la base de datos
    private val db = FirebaseFirestore.getInstance()

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager


    private lateinit var data: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEquiposBinding.inflate(inflater, container, false)

         data = (activity as? DrawerLayout)?.data.toString()

        // Configuracion del tabLayout
        tabLayout = binding.tabLayout
        viewPager = binding.viewPager

        tabLayout.addTab(tabLayout.newTab().setText("Jugadores"))
        tabLayout.addTab(tabLayout.newTab().setText("Partidos"))

        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = AdapterTabLayout(this, childFragmentManager, tabLayout.tabCount)
        viewPager.adapter = adapter

        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })


        consultarEquipos()


        return binding.root
    }


    // Consulta para establecer el nombre, estadio e imagen al equipo elegido en el fragment anterior
    private fun consultarEquipos(){
        db.collection("Equipos").whereEqualTo("nombre", data).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    binding.tvEquipo.text = document["nombre"].toString()
                    binding.tvEstadio.text = document["estadio"].toString()
                    val iEscudo = document["escudo"].toString()
                    Glide.with(requireContext())
                        .load(iEscudo)
                        .into(binding.imEscudo)
                }
            }
    }

}

