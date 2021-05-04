package com.alcanosesp.appalcanos.ui.menulateral.solicitudes.vacaciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.adapter.AdapterViewPager
import com.alcanosesp.appalcanos.adapter.AdapterViewPagerVacaciones
import com.alcanosesp.appalcanos.databinding.FragmentVacacionesVisualizarBinding
import com.google.android.material.tabs.TabLayoutMediator

class VacacionesVisualizarFragment : Fragment() {
    private lateinit var binding: FragmentVacacionesVisualizarBinding
    private lateinit var viewPager: ViewPager
    private lateinit var tableLayout: TableLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_vacaciones_visualizar,
            container,
            false
        )

        binding.pagerDatosPersonales.adapter = AdapterViewPagerVacaciones(this)

        val tabMediator = TabLayoutMediator(
            binding.tabLayout, binding.pagerDatosPersonales,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = " Información"
                        tab.setIcon(R.drawable.ic_info)
                        tab.icon!!.setTint(context!!.getColor( R.color.blanco))
                    }
                    1 -> {
                        tab.text = " Interrupciones"
                        tab.setIcon(R.drawable.ic_content_cut)
                        tab.icon!!.setTint(context!!.getColor( R.color.blanco))
                    }

                }
            })
        tabMediator.attach()
        return binding.root
    }
}
