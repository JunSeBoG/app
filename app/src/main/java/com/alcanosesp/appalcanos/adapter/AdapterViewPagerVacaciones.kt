package com.alcanosesp.appalcanos.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alcanosesp.appalcanos.ui.menulateral.solicitudes.vacaciones.VacacionesVisualizarInterrupcionesFragment
import com.alcanosesp.appalcanos.ui.menulateral.solicitudes.vacaciones.VacacionesVisualizarVacacionesFragment

class AdapterViewPagerVacaciones(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> VacacionesVisualizarVacacionesFragment()
            1 -> VacacionesVisualizarInterrupcionesFragment()
            else -> VacacionesVisualizarVacacionesFragment()
        }
    }
}