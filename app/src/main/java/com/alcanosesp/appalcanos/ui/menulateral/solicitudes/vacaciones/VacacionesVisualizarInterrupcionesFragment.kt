package com.alcanosesp.appalcanos.ui.menulateral.solicitudes.vacaciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.adapter.AdapterRecyclerView
import com.alcanosesp.appalcanos.databinding.FragmentVacacionesVisualizarInterrupcionesBinding
import com.alcanosesp.appalcanos.model.InterrupcionVacaciones
import com.alcanosesp.appalcanos.utils.construirAlerta

class VacacionesVisualizarInterrupcionesFragment : Fragment(),
    AdapterRecyclerView.OnRecyclerClickListener {

    private val vmVacaciones by lazy {
        ViewModelProviders.of(this).get(VacacionesViewModel::class.java)
    }

    private lateinit var binding: FragmentVacacionesVisualizarInterrupcionesBinding
    private lateinit var adapterRVInterrupciones: AdapterRecyclerView
    private var lista = ArrayList<InterrupcionVacaciones>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vmVacaciones.obtenerInterrupcionesApi()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_vacaciones_visualizar_interrupciones,
            container,
            false
        )

        adapterRVInterrupciones = AdapterRecyclerView(context, this)
        binding.rvVacaciones.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = adapterRVInterrupciones
        }

        mostarProgresBar()
        observarInterrupciones()
        swiperListener()
        observarMensajeDialogoError()

        return binding.root
    }

    private fun swiperListener() = binding.refreshVacaciones.setOnRefreshListener {
        mostarProgresBar()
        vmVacaciones.obtenerInterrupcionesApi()
        binding.refreshVacaciones.isRefreshing = false
    }


    private fun observarInterrupciones() {
        vmVacaciones.interrupcion.observe(viewLifecycleOwner, Observer {

            lista.clear()
            val interrupcion: List<InterrupcionVacaciones>? = it
            if (!interrupcion.isNullOrEmpty()) {

                for (element in interrupcion) {
                    lista.add(element)
                }
                adapterRVInterrupciones.crearListaElementos(lista)
                adapterRVInterrupciones.notifyDataSetChanged()
                ocultarEmptyState()

            }else{
                mostarEmptyState()
            }
            ocultarProgresBar()
        })
    }

    private fun observarMensajeDialogoError() {
        vmVacaciones.mensajeDialogoErrorVacaciones.observe(viewLifecycleOwner, Observer {
            val mensaje = it
            if (!mensaje.isNullOrEmpty()) {
                construirAlerta(context!!, R.layout.toas_login_warning, mensaje.toString())
            }
            ocultarProgresBar()
        })
    }

    override fun seleccionarItem(item: Any?) {
        //log ver justificacion
    }

    private fun mostarProgresBar() {
        binding.pbVacaciones.isVisible = true
        binding.refreshVacaciones.isVisible = false
    }

    private fun ocultarProgresBar() {
        binding.pbVacaciones.isVisible = false
        binding.refreshVacaciones.isVisible = true
    }

    private fun mostarEmptyState() {
        binding.llEmptyVacaciones.isVisible = true
        binding.refreshVacaciones.isVisible = false
    }

    private fun ocultarEmptyState() {
        binding.llEmptyVacaciones.isVisible = false
    }

}
