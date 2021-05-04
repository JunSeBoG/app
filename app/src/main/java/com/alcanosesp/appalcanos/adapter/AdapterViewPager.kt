package com.alcanosesp.appalcanos.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos.BasicosFragment
import com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos.EstudiosFragment
import com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos.ExperienciasFragment
import com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos.FamiliaresFragment

class AdapterViewPager (fragment: Fragment) : FragmentStateAdapter(fragment){

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> BasicosFragment()
            1 -> FamiliaresFragment()
            2 -> EstudiosFragment()
            3 -> ExperienciasFragment()
            else -> BasicosFragment()
        }
    }
}