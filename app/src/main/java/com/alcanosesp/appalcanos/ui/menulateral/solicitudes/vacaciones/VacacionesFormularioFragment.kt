package com.alcanosesp.appalcanos.ui.menulateral.solicitudes.vacaciones

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.alcanosesp.appalcanos.App
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.databinding.FragmentVacacionesFormularioBinding
import com.alcanosesp.appalcanos.db.entity.Funcionario
import com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos.BasicosViewModel
import com.alcanosesp.appalcanos.utils.*
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

class VacacionesFormularioFragment : Fragment() {
    private val vmVacaciones by lazy {
        ViewModelProviders.of(this).get(VacacionesViewModel::class.java)
    }

    private val vmFuncionario by lazy {
        ViewModelProviders.of(this).get(BasicosViewModel::class.java)
    }

    private lateinit var binding: FragmentVacacionesFormularioBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        validarTituloFragment()
        vmFuncionario.obtenerFuncionario()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_vacaciones_formulario,
            container,
            false
        )

        binding.vacaciones = App.solicitudVacaciones
        mostarProgresBar()

        observadorContratoID()
        observarVacaciones()

        clickBotonGuardar()
        mostarDialogoFecha()
        observaGuardarSolicitudVacaciones()
        observaRedireccionar()
        observarMensajeDialogoError()

        return binding.root
    }

    private fun observaRedireccionar() {
        vmVacaciones.redireccionar.observe(this, Observer {
            App.solicitudVacaciones = null
            findNavController().navigate(R.id.action_vacacionesFormularioFragment_to_vacacionesFragment)
        })
    }

    private fun observaGuardarSolicitudVacaciones() =
        vmVacaciones.guardoSolicitudVacaciones.observe(this, Observer {
            if (it == "EXITO") {

                vmVacaciones.eliminarSolicitudVacaciones()
                crearSnackbar(true).show()
                vmVacaciones.recargarSolicitudVacacionesApi()
            } else {
                binding.btnGuardarVacaciones.isEnabled = true
                crearSnackbar(false).show()
                obtenerMensajesBacked(JSONObject(it))
            }
        })

    private fun clickBotonGuardar() = binding.btnGuardarVacaciones.setOnClickListener {
        cerrarTeclado(activity)
        if (validarAntesdeEnviar()) {
            enviarSolicitudVacaciones()
        } else {
            crearSnackbar(false).show()
        }

    }

    private fun obtenerMensajesBacked(error: JSONObject) {

        val message: String? = error.optString("message")
        if (message!!.isNotEmpty()) {
            construirAlerta(context!!, R.layout.toas_login_warning, message)
        } else {
            val jsonErrors = error.getJSONObject("errors")

            obtenerMensajesJsonDialog(jsonErrors, "id", context!!)
            obtenerMensajesJsonDialog(jsonErrors, "libroVacacionesId", context!!)
            obtenerMensajesJsonDialog(jsonErrors, "funcionarioId", context!!)
            obtenerMensajesJsonDialog(jsonErrors, "snackbar", context!!)

            obtenerMensajesJsonLabel(
                jsonErrors,
                "fechaInicioDisfrute",
                binding.msgErrorFechaDisfruteVacaciones
            )
            obtenerMensajesJsonLabel(
                jsonErrors,
                "diasDisfrute",
                binding.msgErrorDiasDisfruteVacaciones
            )
            obtenerMensajesJsonLabel(jsonErrors, "diasDinero", binding.msgErrorDiasDineroVacaciones)
            obtenerMensajesJsonLabel(
                jsonErrors, "observacion", binding.msgErrorObservacionVacaciones
            )
        }
    }


    private fun crearSnackbar(exito: Boolean): Snackbar {
        return Snackbar.make(view!!, "Snackbar", 2600).apply {

            val vista = view

            when (exito) {
                true -> {
                    setText("Información actualizada.")
                    vista.setBackgroundColor(context.getColor(R.color.verde_pera))
                }
                false -> {
                    setText("Ha ocurrido un error al procesar la información.")
                    vista.setBackgroundColor(context.getColor(R.color.rojo))
                }
            }

            vista.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).apply {
                typeface = ResourcesCompat.getFont(context!!, R.font.muli_regular)
            }

            vista.findViewById<TextView>(com.google.android.material.R.id.snackbar_action).apply {
                typeface = ResourcesCompat.getFont(context!!, R.font.muli_bold)
                isAllCaps = false
            }

            setAction("Aceptar") {
                dismiss()
            }
        }
    }

    private fun enviarSolicitudVacaciones() {
        binding.btnGuardarVacaciones.isEnabled = false

        val fechaDisfrute = binding.etFechaDisfruteVacaciones.text.toString().replace(" ", "")
        val parametros = JSONObject().apply {
            put("FuncionarioId", App.idFuncionario)
            put("libroVacacionesId", binding.vacaciones?.libroVacacionesId?.toInt())
            put("fechaInicioDisfrute", fechaDisfrute)
            put("diasDisfrute", binding.etDiasDisfruteVacaciones.text)
            put("diasDinero", binding.etDiasDineroVacaciones.text)
            put("observacion", binding.etObservacionVacaciones.text)
        }

        vmVacaciones.enviarSolicitudVacacionesApi(parametros)
    }

    private fun validarAntesdeEnviar(): Boolean {
        val validador = Validador()
        var retorno = true

        if (!validador.campoRequerido(
                binding.etFechaDisfruteVacaciones,
                binding.msgErrorFechaDisfruteVacaciones
            )
        ) retorno = false

        if (!validador.campoRequerido(
                binding.etDiasDisfruteVacaciones,
                binding.msgErrorDiasDisfruteVacaciones
            )
        ) retorno = false

        if (!validador.campoRequerido(
                binding.etDiasDineroVacaciones,
                binding.msgErrorDiasDineroVacaciones
            )
        ) retorno = false

        return retorno
    }

    private fun mostarDialogoFecha() = binding.etFechaDisfruteVacaciones.setOnClickListener {
        mostrarDateDialogFecha(fragmentManager!!, it as EditText)
    }

    private fun observarVacaciones() {
        vmVacaciones.vacaciones.observe(viewLifecycleOwner, Observer {
            binding.vacaciones = it[0]
        })
        val handler=Handler()
        handler.postDelayed({ocultarProgresBar()},1000)

    }


    private fun observadorContratoID() {
        if (App.solicitudVacaciones == null) {
            vmFuncionario.funcionario.observe(viewLifecycleOwner, Observer {
                val funcionario: Funcionario? = it
                if (funcionario != null) {
                    Log.i("contrato id", "funcionario.contratoId.toString()")
                    vmVacaciones.obtenerPeriodoMasAntiguoApi(funcionario.contratoId.toString())
                }
            })
        }
    }

    private fun observarMensajeDialogoError() {
        vmVacaciones.mensajeDialogoErrorVacaciones.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                construirAlerta(context!!, R.layout.toas_login_warning, it)
                binding.btnGuardarVacaciones.isEnabled = false
            }
        })
    }

    private fun mostarProgresBar() {
        binding.pbVacacionesForulario.isVisible = true
        binding.svVacacionesFormulario.isVisible = false
    }

    private fun ocultarProgresBar() {
        //if (cargoDatosActules && cargoMotivos) {
        binding.pbVacacionesForulario.isVisible = false
        binding.svVacacionesFormulario.isVisible = true
        //}

    }

    private fun validarTituloFragment() = if (App.solicitudVacaciones == null) {
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Registrar solicitud"
    } else {
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Editar solicitud"

    }

}
