package com.alcanosesp.appalcanos.ui.menulateral.solicitudes.cesantias

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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
import com.alcanosesp.appalcanos.adapter.SpinnerAdapter
import com.alcanosesp.appalcanos.api.IRespuestaServidor
import com.alcanosesp.appalcanos.api.obtenerIncapacidad
import com.alcanosesp.appalcanos.databinding.FragmentCesantiasFormularioBinding
import com.alcanosesp.appalcanos.db.entity.SolicitudCesantia
import com.alcanosesp.appalcanos.model.ItemSpinner
import com.alcanosesp.appalcanos.utils.*
import com.android.volley.VolleyError
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject


class CesantiasFormularioFragment : Fragment() {
    private val vmCesantias by lazy {
        ViewModelProviders.of(this).get(CesantiasViewModel::class.java)
    }

    private val vmArchivo by lazy {
        ViewModelProviders.of(this).get(ArchivoAdjuntoViewModel::class.java)
    }

    private lateinit var binding: FragmentCesantiasFormularioBinding
    private lateinit var adaptadorMotivoSolicitud: SpinnerAdapter
    private var cargoMotivos = false
    private var cargoDatosActules = false
    private var valor: String? = "0"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        validarTituloFragment()
        obtenerDatosSpinerMotivoSolicitud()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_cesantias_formulario,
            container,
            false
        )
        binding.cesantias = App.solicitudCesantia
        mostarProgresBar()

        obtenerDatosActuales()
        escuchaSelectItemMotivoSolicitud()
        observarDatosActuales()
        clickCargarSoporte()
        clickBotonGuardar()
        observaGuardarSolicitudCesantias()
        observarArchivoAdjuntoGuardar()
        observarMensajeDialogoError()
        observaRedireccionar()

        monedaSindecimales(binding.tvValorSolicitadoCesantias)

        return binding.root
    }

    private fun obtenerDatosActuales() {
        vmCesantias.obtenerDatosActualesSolicitiudApi()
    }

    private fun observaRedireccionar() {
        vmCesantias.redireccionar.observe(this, Observer {
            App.solicitudCesantia = null
            findNavController().navigate(R.id.action_cesantiasFormularioFragment_to_cesantiasFragment)
        })
    }

    private fun observarArchivoAdjuntoGuardar() {
        vmArchivo.archivoAdjunto.observe(this, Observer {
            if (it.isNotEmpty()) {
                for (archivo in it) {
                    if (archivo.objectId.isEmpty()) {
                        binding.msgErrorAdjuntoCesantias.text = getString(R.string.msg_requerido)
                    } else {
                        binding.tvSoporteCesantias.text = archivo.objectId
                    }
                }
            } else {
                binding.msgErrorAdjuntoCesantias.text = getString(R.string.msg_requerido)
            }
            crearSolicitudCesantias()
        })
    }

    private fun crearSolicitudCesantias() {
        cerrarTeclado(activity)
        if (validarAntesdeEnviar()) {
            enviarIncapacidad()
        } else {
            crearSnackbar(false).show()
        }
    }

    private fun enviarIncapacidad() {
        binding.btnGuardarCesantias.isEnabled = false

        val parametros = JSONObject().apply {
            put("FuncionarioId", App.idFuncionario)
            put("motivoSolicitudCesantiaId", vmCesantias.idMotivoCesantia)
            put(
                "valorSolicitado",
                binding.tvValorSolicitadoCesantias.cleanIntValue
            )
            put("soporte", binding.tvSoporteCesantias.text)
            put("observacion", binding.etSolicitudPermisoObservacion.text.trim())
        }

        vmCesantias.enviarSolicitudCesantiasApi(parametros)
    }

    private fun observaGuardarSolicitudCesantias() =
        vmCesantias.guardoSolicitudCesantias.observe(this, Observer {
            if (it == "EXITO") {

                vmArchivo.eliminarArchivoAdjunto()
                crearSnackbar(true).show()
                vmCesantias.recargarSolicitudCesantiasApi()
            } else {
                binding.btnGuardarCesantias.isEnabled = true
                crearSnackbar(false).show()
                obtenerMensajesBacked(JSONObject(it))
            }
        })


    private fun recargarSolicitudCesantias() {
        val callbackRecargaCesantias = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                vmCesantias.eliminarSolicidCesantias()


                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    val cesantias = SolicitudCesantia(item)

                    vmCesantias.insertarSolicidCesantias(cesantias)
                }


                val handler = Handler()
                handler.postDelayed(
                    {
                        vmCesantias.obtenerSolicidCesantias()
                    }, 100
                )

                handler.postDelayed(
                    {
                        vmCesantias.obtenerSolicidCesantias()
                        findNavController().navigate(R.id.action_cesantiasFormularioFragment_to_cesantiasFragment)
                        App.experiencia = null
                    },
                    1500
                )
            }

            override fun error(error: VolleyError) {
            }
        }
        obtenerIncapacidad(context!!, callbackRecargaCesantias, App.idFuncionario.toString())
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

    private fun validarAntesdeEnviar(): Boolean {
        val validador = Validador()
        var retorno = true

        if (!validador.spinnerRequerido(
                binding.sMotivoSolicitudCesantias,
                binding.msgErrorMotivoSolicitudCesantias
            )
        ) retorno = false

        if (!validador.campoRequerido(
                binding.tvValorSolicitadoCesantias,
                binding.msgErrorValorSolicitadoCesantias
            )
        ) retorno = false

        if (!validador.textViewRequerido(
                binding.tvSoporteCesantias,
                binding.msgErrorAdjuntoCesantias
            )
        ) retorno = false

        if (!validador.textViewRequerido(
                binding.etSolicitudPermisoObservacion,
                binding.msgErrorObservacionCesantias
            )
        ) retorno = false


        return retorno
    }

    private fun clickBotonGuardar() = binding.btnGuardarCesantias.setOnClickListener {
        vmArchivo.obtenerArchivoAdjunto()
        //validar observarArchivoAdjuntoGuardar
    }


    private fun clickCargarSoporte() = binding.tvLabelCargarSoporte.setOnClickListener {
        val cargarAdjuntoFragment = CargarAdjunto()
        cargarAdjuntoFragment.show(requireFragmentManager(), "CargarAdjuntoCesantias")
    }


    private fun escuchaSelectItemMotivoSolicitud() {
        binding.sMotivoSolicitudCesantias.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val seleccion = parent?.getItemAtPosition(position) as ItemSpinner
                    vmCesantias.motivoCesantia(seleccion.id)
                }
            }
    }

    private fun obtenerDatosSpinerMotivoSolicitud() {
        adaptadorMotivoSolicitud = SpinnerAdapter(context!!)
        vmCesantias.obtenerMotivoSolicitiudApi()

        vmCesantias.listaMotivoCesantia.observe(this, Observer {
            this.adaptadorMotivoSolicitud.setItems(it)
            binding.sMotivoSolicitudCesantias.adapter = adaptadorMotivoSolicitud
            if (App.solicitudCesantia != null) {
                asignarSpinnersMotivoCesantias()
            }

            cargoMotivos = true
            ocultarProgresBar()
        })
    }

    private fun asignarSpinnersMotivoCesantias() {
        val posicionItemSpinner = adaptadorMotivoSolicitud.obtenerPosicionValor(
            ItemSpinner(
                binding.cesantias!!.motivoSolicitudCesantiaId!!,
                binding.cesantias!!.motivoSolicitudCesantiaNombre
            )
        )
        binding.sMotivoSolicitudCesantias.setSelection(posicionItemSpinner)
    }

    private fun validarTituloFragment() = if (App.solicitudCesantia == null) {
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Registrar solicitud"
    } else {
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Editar solicitud"
    }


    private fun observarDatosActuales() {
        vmCesantias.datosActualesCesantias.observe(viewLifecycleOwner, Observer {
            val datos = it
            it.anticiposSolicitados
            binding.datoscesantias = it

            cargoDatosActules = true
            ocultarProgresBar()
        })
    }

    private fun obtenerMensajesBacked(error: JSONObject) {

        val message: String? = error.optString("message")
        if (message!!.isNotEmpty()) {
            construirAlerta(context!!, R.layout.toas_login_warning, message)
        } else {
            val jsonErrors = error.getJSONObject("errors")

            obtenerMensajesJsonDialog(jsonErrors, "errror", context!!)
            obtenerMensajesJsonDialog(jsonErrors, "snack", context!!)
            obtenerMensajesJsonDialog(jsonErrors, "funcionarioId", context!!)

            obtenerMensajesJsonLabel(
                jsonErrors, "motivoSolicitudCesantiaId", binding.msgErrorMotivoSolicitudCesantias
            )
            obtenerMensajesJsonLabel(
                jsonErrors, "valorSolicitado", binding.msgErrorValorSolicitadoCesantias
            )
            obtenerMensajesJsonLabel(
                jsonErrors, "soporte", binding.msgErrorAdjuntoCesantias
            )
            obtenerMensajesJsonLabel(
                jsonErrors, "observacion", binding.msgErrorObservacionCesantias
            )
        }
    }

    private fun observarMensajeDialogoError() {
        vmCesantias.mensajeDialogoError.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                construirAlerta(context!!, R.layout.toas_login_warning, it)
                binding.btnGuardarCesantias.isEnabled = false
            }
        })
    }

    private fun mostarProgresBar() {
        binding.pbCesantiasForulario.isVisible = true
        binding.svCesantiasFormulario.isVisible = false
    }

    private fun ocultarProgresBar() {
        if (cargoDatosActules && cargoMotivos) {
            binding.pbCesantiasForulario.isVisible = false
            binding.svCesantiasFormulario.isVisible = true
        }

    }
}
