package com.example.proyectofinal.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.proyectofinal.DrawerLayout.InicioFragment
import com.example.proyectofinal.EquiposFragment
import com.example.proyectofinal.FragmentTabLayout.JugadoresFragment
import com.example.proyectofinal.FragmentTabLayout.PartidosFragment

internal class AdapterTabLayout(var context: EquiposFragment, fm: FragmentManager, var totalTabs: Int): FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        // Devuelve el fragmento correspondiente a la posición dada en el TabLayout
        return when(position){
            0 -> JugadoresFragment() // Fragmento para la posición 0 (JugadoresFragment)
            1 -> PartidosFragment() // Fragmento para la posición 1 (PartidosFragment)
            else -> getItem(position) // Si la posición no es 0 ni 1, se llama recursivamente a getItem()
        }
    }

    override fun getCount(): Int {
        // Devuelve el número total de fragmentos en el TabLayout
        return totalTabs
    }
}
