package com.alcanosesp.appalcanos.ui.menulateral.solicitudes.cesantias

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
import com.alcanosesp.appalcanos.databinding.FragmentCesantiasBinding
import com.alcanosesp.appalcanos.db.entity.SolicitudCesantia
import com.alcanosesp.appalcanos.utils.construirAlerta

class CesantiasFragment : Fragment(), AdapterRecyclerView.OnRecyclerClickListener {

    private val vmCesantias by lazy {
        ViewModelProviders.of(this).get(CesantiasViewModel::class.java)
    }

    private lateinit var binding: FragmentCesantiasBinding
    private lateinit var adapterRVCesantias: AdapterRecyclerView
    private var lista = ArrayList<SolicitudCesantia>()
    private var obtenerBaseDeDatos = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cesantias, container, false)

        adapterRVCesantias = AdapterRecyclerView(context, this)
        binding.rvCesantias.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = adapterRVCesantias
        }

        vmCesantias.obtenerSolicidCesantias()
        observadorSolicitudCesantias()
        swiperListener()
        observarMensajeDialogoError()
        clickCreaCesantias()

        return binding.root
    }

    private fun clickCreaCesantias() {
        App.solicitudCesantia = null
        binding.fabCesantias.setImageDrawable(context!!.getDrawable(R.drawable.ic_add))
        binding.fabCesantias.setOnClickListener {
            findNavController().navigate(R.id.action_cesantiasFragment_to_cesantiasFormularioFragment)
        }
    }

    private fun swiperListener() = binding.refreshCesantias.setOnRefreshListener {
        mostarProgresBar()
        vmCesantias.obtenerSolicitudCesantiasApi()
        binding.refreshCesantias.isRefreshing = false
    }

    private fun observadorSolicitudCesantias() {
        vmCesantias.cesantias.observe(this, Observer {

            lista.clear()
            val cesantias = it
            if (cesantias.isEmpty() && !obtenerBaseDeDatos) {
                mostarEmptyState()
                ocultarProgresBar()
            }
            if (cesantias.isEmpty() && obtenerBaseDeDatos) {
                obtenerBaseDeDatos = false
                vmCesantias.obtenerSolicitudCesantiasApi()

            } else {
                for (element in cesantias) {
                    lista.add(element)
                }
                adapterRVCesantias.crearListaElementos(lista)
                if(lista.size>0) {
                    ocultarEmptyState()
                    ocultarProgresBar()
                }
            }
            adapterRVCesantias.notifyDataSetChanged()


        })


    }

    private fun mostarEmptyState() {
        binding.llEmptyCesantias.isVisible = true
        binding.refreshCesantias.isVisible = false
    }

    private fun ocultarEmptyState() {
        binding.llEmptyCesantias.isVisible = false
    }

    private fun mostarProgresBar() {
        binding.pbCesantias.isVisible = true
        binding.refreshCesantias.isVisible = false
    }

    private fun ocultarProgresBar() {
        binding.pbCesantias.isVisible = false
        binding.refreshCesantias.isVisible = true
        binding.rvCesantias.isVisible = true
    }

    override fun seleccionarItem(item: Any?) {
        App.solicitudCesantia = item as SolicitudCesantia
        findNavController().navigate(R.id.action_cesantiasFragment_to_cesantiasVisualizarFragment)
    }

    private fun observarMensajeDialogoError() {
        vmCesantias.mensajeDialogoError.observe(this, Observer {
            val mensaje = it
            if (!mensaje.isNullOrEmpty()) {
                construirAlerta(context!!, R.layout.toas_login_warning, mensaje.toString())
            }
            ocultarProgresBar()
        })
    }
}
