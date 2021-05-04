package com.alcanosesp.appalcanos.ui.menulateral.solicitudes.cesantias

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.alcanosesp.appalcanos.App
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.databinding.FragmentCesantiasVisualizarBinding
import com.alcanosesp.appalcanos.utils.estadosBeneficios


class CesantiasVisualizarFragment : Fragment() {
    private val vmCesantias by lazy {
        ViewModelProviders.of(this).get(CesantiasViewModel::class.java)
    }
    private lateinit var binding: FragmentCesantiasVisualizarBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_cesantias_visualizar,
            container,
            false
        )
        binding.cesantias = App.solicitudCesantia!!


        clickEditar()
        clickCancelar()
        observadorCancelarRedireccionar()
        clickDescargarSoporte()
        ocultarMostrarInfo()
        colorEstado()

        return binding.root
    }

    private fun observadorCancelarRedireccionar() {
        vmCesantias.redireccionar.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(R.id.action_cancelar_cesantiasVisualizarFragment_to_cesantiasFragment2)
        })
    }

    private fun clickEditar() = binding.fabAprobar.setOnClickListener {
        App.solicitudCesantia = binding.cesantias
        findNavController().navigate(R.id.action_cesantiasVisualizarFragment_to_cesantiasFormularioFragment)
    }

    private fun clickCancelar() = binding.fabCancelar.setOnClickListener {
        mostarDialogoCancelar()
    }

    private fun clickDescargarSoporte() = binding.llSoporteCesantias.setOnClickListener {
        val soporte = binding.cesantias?.soporte

        if (!soporte.isNullOrEmpty()) {
            val url =
                getString(R.string.apiNomina).plus("api/archivos/$soporte/Archivo")
            val pilaIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(pilaIntent)
        }
    }


    private fun colorEstado() = context.let { it ->
        it?.getColor(estadosBeneficios[binding.cesantias?.estado]!!).let {
            binding.tvEstadoCesantias.background.setTint(it!!)
        }
    }

    private fun ocultarMostrarInfo() {
        when (binding.cesantias?.estado) {
            "EnTramite" -> {
                binding.fabAprobar.isVisible = true
                binding.fabAprobar.setImageDrawable(context!!.getDrawable(R.drawable.ic_edit))
                binding.fabCancelar.isVisible = true
            }
            "Autorizada" -> {
                binding.fabCancelar.isVisible = true
            }
        }
    }

    private fun mostarDialogoCancelar() {
        val vista = LayoutInflater.from(context).inflate(R.layout.dialogo_botones, null)
        val textView = vista.findViewById<TextView>(R.id.texto_dialog)
        textView.text = getString(R.string.pregunta_anular_solicitud)

        val botonAceptar = vista.findViewById<Button>(R.id.boton_dialog)
        val botonCancelar = vista.findViewById<Button>(R.id.boton_dialog_cancel)

        val builder = AlertDialog.Builder(context)
        builder.apply {
            setView(vista)
            create()
        }
        val dialogo = builder.show()
        botonAceptar.setOnClickListener {
            dialogo.dismiss()
            binding.pbSolicitudPermiso.isVisible = true
            binding.fabCancelar.isVisible = false
            binding.fabAprobar.isVisible = false
            vmCesantias.cancelarSolicitudCesantiasApi(binding.cesantias?.id)
        }

        botonCancelar.setOnClickListener {
            dialogo.dismiss()
        }
    }
}
