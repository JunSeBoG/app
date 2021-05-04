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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alcanosesp.appalcanos.App
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.adapter.AdapterRecyclerView
import com.alcanosesp.appalcanos.databinding.FragmentVacacionesBinding
import com.alcanosesp.appalcanos.db.entity.SolicitudVacaciones
import com.alcanosesp.appalcanos.utils.construirAlerta

class VacacionesFragment : Fragment(), AdapterRecyclerView.OnRecyclerClickListener {
    private val vmVacaciones by lazy {
        ViewModelProviders.of(this).get(VacacionesViewModel::class.java)
    }

    private lateinit var binding: FragmentVacacionesBinding
    private lateinit var adapterRVVacaciones: AdapterRecyclerView
    private var lista = ArrayList<SolicitudVacaciones>()
    private var obtenerBaseDeDatos = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vmVacaciones.obtenerSolicitudVacaciones()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_vacaciones, container, false)

        adapterRVVacaciones = AdapterRecyclerView(context, this)
        binding.rvVacaciones.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = adapterRVVacaciones
        }

        mostarProgresBar()
        observadorSolicitudVacaciones()
        swiperListener()
        observarMensajeDialogoError()
        clickCreaVacaciones()

        return binding.root
    }


    private fun clickCreaVacaciones() {
        App.solicitudVacaciones = null
        binding.fabVacaciones.setImageDrawable(context!!.getDrawable(R.drawable.ic_add))
        binding.fabVacaciones.setOnClickListener {
            findNavController().navigate(R.id.action_vacacionesFragment_to_vacacionesFormularioFragment)
        }
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

    private fun swiperListener() = binding.refreshVacaciones.setOnRefreshListener {
        mostarProgresBar()
        vmVacaciones.obtenerSolicitudVacacionesApi()
        binding.refreshVacaciones.isRefreshing = false
    }

    private fun observadorSolicitudVacaciones() {
        vmVacaciones.vacaciones.observe(viewLifecycleOwner, Observer {

            lista.clear()
            val vacaciones = it
            if (vacaciones.isEmpty() && obtenerBaseDeDatos) {
                obtenerBaseDeDatos = false
                vmVacaciones.obtenerSolicitudVacacionesApi()

            } else {
                for (element in vacaciones) {
                    lista.add(element)
                }
                adapterRVVacaciones.crearListaElementos(lista)
                ocultarEmptyState()
            }
            adapterRVVacaciones.notifyDataSetChanged()

            if (vacaciones.isEmpty() && !obtenerBaseDeDatos) {
                mostarEmptyState()
            }

            ocultarProgresBar()
        })
    }

    private fun mostarProgresBar() {
        binding.pbVacaciones.isVisible = true
        binding.refreshVacaciones.isVisible = false
        binding.llEmptyVacaciones.isVisible = false
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

    override fun seleccionarItem(item: Any?) {
        App.solicitudVacaciones = item as SolicitudVacaciones
        findNavController().navigate(R.id.action_vacacionesFragment_to_vacacionesVisualizarFragment)
    }
}
