package com.alcanosesp.appalcanos.ui.menulateral.solicitudes.vacaciones

import android.app.AlertDialog
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
import com.alcanosesp.appalcanos.databinding.FragmentVacacionesVisualizarVacacionesBinding
import com.alcanosesp.appalcanos.utils.estadosVacaciones

class VacacionesVisualizarVacacionesFragment : Fragment() {

    private val vmVacaciobnes by lazy {
        ViewModelProviders.of(this).get(VacacionesViewModel::class.java)
    }
    private lateinit var binding: FragmentVacacionesVisualizarVacacionesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_vacaciones_visualizar_vacaciones,
            container,
            false
        )

        binding.vacaciones = App.solicitudVacaciones

        clickEditar()
        clickCancelar()
        observadorCancelarRedireccionar()
        ocultarMostrarInfo()
        colorEstado()

        return binding.root
    }

    private fun colorEstado() = context.let { it ->
        it?.getColor(estadosVacaciones[binding.vacaciones?.estado]!!).let {
            binding.tvEstadoVacaciones.background.setTint(it!!)
        }
    }

    private fun ocultarMostrarInfo() {
        when (binding.vacaciones?.estado) {
            "Solicitada" -> {
                binding.fabAprobar.isVisible = true
                binding.fabAprobar.setImageDrawable(context!!.getDrawable(R.drawable.ic_edit))
                binding.fabCancelar.isVisible = true
            }
            "Aprobada"  ->  binding.llRespuestaSolicitudVacaciones.isVisible = true
            "Autorizada"  ->  binding.llRespuestaSolicitudVacaciones.isVisible = true
            "Rechazada"  ->  binding.llRespuestaSolicitudVacaciones.isVisible = true
        }
    }


    private fun observadorCancelarRedireccionar() {
        vmVacaciobnes.redireccionar.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(R.id.action_cancelar_vacacionesVisualizarFragment_to_vacacionesFragment)
        })
    }

    private fun clickCancelar() = binding.fabCancelar.setOnClickListener {
        mostarDialogoCancelar()
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
            binding.pbSolicitudVacaciones.isVisible = true
            binding.fabCancelar.isVisible = false
            binding.fabAprobar.isVisible = false
            vmVacaciobnes.cancelarSolicitudVacacionesApi(binding.vacaciones?.id)
        }

        botonCancelar.setOnClickListener {
            dialogo.dismiss()
        }
    }

    private fun clickEditar() = binding.fabAprobar.setOnClickListener {
        App.solicitudVacaciones = binding.vacaciones
        findNavController().navigate(R.id.action_vacacionesVisualizarFragment_to_vacacionesFormularioFragment)
    }

}
