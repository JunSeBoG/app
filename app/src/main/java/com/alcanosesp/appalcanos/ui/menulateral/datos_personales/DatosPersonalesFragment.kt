package com.alcanosesp.appalcanos.ui.menulateral.datos_personales

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2

import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.adapter.AdapterViewPager

import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DatosPersonalesFragment : Fragment() {

    private lateinit var viewPager:ViewPager
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_datos_personales, container, false)

        val tabs = view.findViewById<TabLayout>(R.id.tab_layout)
        val pager = view.findViewById<ViewPager2>(R.id.pager_datosPersonales)

        pager.adapter = AdapterViewPager(this)

        val tabMediator = TabLayoutMediator(tabs, pager,
            TabLayoutMediator.TabConfigurationStrategy{ tab, position ->
                when(position){
                    0 -> {
                        tab.text = " Básicos"
                        tab.setIcon(R.drawable.ic_person)}
                    1 -> {
                        tab.text = " Familia"
                        tab.setIcon(R.drawable.ic_supervisor_account)}
                    2 -> {
                        tab.text = " Estudios"
                        tab.setIcon(R.drawable.ic_school)}
                    3 -> {
                        tab.text = "Experiencia"
                        tab.setIcon(R.drawable.ic_work_outline)}
                }
            })

        tabMediator.attach()
        return view
    }
}