package com.alcanosesp.appalcanos.ui.menulateral.solicitudes.permisos

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Button
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alcanosesp.appalcanos.App
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.adapter.AdapterRVSoportePermiso
import com.alcanosesp.appalcanos.adapter.SpinnerAdapter
import com.alcanosesp.appalcanos.api.*
import com.alcanosesp.appalcanos.databinding.FragmentPermisoFormularioBinding
import com.alcanosesp.appalcanos.db.entity.SolicitudPermiso
import com.alcanosesp.appalcanos.db.entity.SolicitudPermisoSoporte
import com.alcanosesp.appalcanos.model.ItemSpinner
import com.alcanosesp.appalcanos.utils.Validador
import com.alcanosesp.appalcanos.utils.construirAlerta
import com.alcanosesp.appalcanos.utils.mostrarDateDialogFecha
import com.alcanosesp.appalcanos.utils.mostrarDateDialoghora
import com.android.volley.VolleyError
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_permiso_formulario.*
import org.json.JSONObject
import kotlin.math.abs


class PermisoFormularioFragment : Fragment(), AdapterRVSoportePermiso.OnSoportePermisosListener {

    private val vmPermiso by lazy {
        ViewModelProviders.of(this).get(PermisoViewModel::class.java)
    }
    private val solicituPermisos = App.solicitudPermiso
    private lateinit var binding: FragmentPermisoFormularioBinding
    private lateinit var adaptadorClasePermiso: SpinnerAdapter
    private lateinit var adaptadorTipoPermiso: SpinnerAdapter
    private lateinit var idCreado: String
    private var permisoHoras = false
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterRVSoportePermiso: AdapterRVSoportePermiso
    private var listaSoporte = ArrayList<SolicitudPermisoSoporte>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (solicituPermisos == null) {
            (activity as AppCompatActivity?)!!.supportActionBar?.title = "Registrar solicitud"
        } else {
            (activity as AppCompatActivity?)!!.supportActionBar?.title = "Editar solicitud"
        }

        adaptadorClasePermiso = SpinnerAdapter(context!!)
        adaptadorTipoPermiso = SpinnerAdapter(context!!)

        vmPermiso.obtenerClasePermisoApi(context!!)


        vmPermiso.listaClasePermiso.observe(this, Observer {
            this.adaptadorClasePermiso.setItems(it)
            if(solicituPermisos != null) {
                asignarSpinnersClasePermiso()
            }
            mostrarVista()
        })

        vmPermiso.listaTipoPermiso.observe(this, Observer {
            this.adaptadorTipoPermiso.setItems(it)
            if(solicituPermisos != null) {
                asignarSpinnersTipoPermiso()
            }
            mostrarVista()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_permiso_formulario,
            container,
            false
        )

        binding.solicitudPermiso = solicituPermisos

        binding.sSolicitudPermisoTipoPermiso.adapter = adaptadorTipoPermiso
        binding.sSolicitudPermisoTipoPermiso.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    val seleccion = parent?.getItemAtPosition(position) as ItemSpinner
                    vmPermiso.tipoPermisoSeleccionado(seleccion.id)
                }

            }

        binding.sSolicitudPermisoClasePermiso.adapter = adaptadorClasePermiso
        binding.sSolicitudPermisoClasePermiso.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    val seleccion = parent?.getItemAtPosition(position) as ItemSpinner
                    vmPermiso.clasePermisoSeleccionado(seleccion.id)

                    if (seleccion.id != 0) {
                        mostrarVista("Guardando")
                        vmPermiso.obtenerTipoPermisoApi(context!!)
                    }

                    if (seleccion.nombre.toString().contains("hora")) {
                        permisoHoras = true
                        binding.llSolicitudPermisoHoraLlegada.isVisible = true
                        binding.llSolicitudPermisoHoraSalida.isVisible = true
                        binding.llSolicituPermisoFechaFin.isVisible = false
                    } else {
                        permisoHoras = false
                        binding.llSolicitudPermisoHoraLlegada.isVisible = false
                        binding.llSolicitudPermisoHoraSalida.isVisible = false
                        binding.llSolicituPermisoFechaFin.isVisible = true
                    }
                }

            }



        binding.etSolicitudPermisoFechaInicio.setOnClickListener {
            mostrarDateDialogFecha(fragmentManager!!, it as EditText)
        }
        binding.etSolicitudPermisoFechaFin.setOnClickListener {
            mostrarDateDialogFecha(fragmentManager!!, it as EditText)
        }
        binding.etSolicitudPermisoHoraSalida.setOnClickListener {
            mostrarDateDialoghora(fragmentManager!!, it as EditText)
        }
        binding.etSolicitudPermisoHoraLlegada.setOnClickListener {
            mostrarDateDialoghora(fragmentManager!!, it as EditText)
        }

        binding.btnGuardarSolicitudPermiso.setOnClickListener {
            crearSoliictudPermiso()
            mostrarVista("Guardando")
        }

        binding.tvSoportePermiso.setOnClickListener {
            findNavController().navigate(R.id.action_permisoFormularioFragment_to_permisoSoporteFragment)
        }




        if (solicituPermisos == null) {
            binding.llSoportePermiso.isVisible = false
        } else {
            vmPermiso.obtenerSoporteSolicitudPermisoByPermisoId(this.solicituPermisos.id!!)
            binding.sSolicitudPermisoClasePermiso.isEnabled = false
        }
        recyclerView = binding.rvSoportePermiso
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapterRVSoportePermiso = AdapterRVSoportePermiso(context!!, this, "delete")
        recyclerView.adapter = adapterRVSoportePermiso
        vmPermiso.soportePermiso.observe(this, Observer {
            Log.i("observador","crateview")
            listaSoporte.clear()
            vmPermiso.eliminarSoporteSolicitudPermiso()
            val soporte = it
            var numeroLineas = 0
            if (soporte.isNotEmpty()) {
                for (i in soporte) {
                    vmPermiso.insertarSoporteSolicitudPermiso(i)
                    var linea = 0
                    if (i.comentario.length > 100) {
                        linea = abs(i.comentario.length / 50) - 1
                    }
                    numeroLineas += linea

                    listaSoporte.add(i)
                    adapterRVSoportePermiso.crearListaElementos(listaSoporte)
                    adapterRVSoportePermiso.notifyDataSetChanged()
                }
                val paramr = binding.rvSoportePermiso.layoutParams
                paramr.height = 120 * soporte.size
            }
        })


        return binding.root
    }

    private fun asignarSpinnersClasePermiso() {
        var posicionItemSpinner = 0

        posicionItemSpinner = adaptadorClasePermiso.obtenerPosicionValor(
            ItemSpinner(
                solicituPermisos?.claseAusentismoId!!,
                solicituPermisos.claseAusentismoNombre)
        )
        binding.sSolicitudPermisoClasePermiso.setSelection(posicionItemSpinner)
    }

    private fun asignarSpinnersTipoPermiso() {
        var posicionItemSpinner = 0

        posicionItemSpinner = adaptadorTipoPermiso.obtenerPosicionValor(
            ItemSpinner(
                solicituPermisos?.tipoAusentismoId!!,
                solicituPermisos.tipoAusentismoNombre)
        )
        binding.sSolicitudPermisoTipoPermiso.setSelection(posicionItemSpinner)
    }


    private fun crearSoliictudPermiso() {
        cerrarTeclado()
        if (validarAntesdeEnviar()) {
            enviarSolicitud()
        } else {
            mostrarVista()
            crearSnackbar(false).show()
        }
    }

    private fun enviarSolicitud() {
        binding.btnGuardarSolicitudPermiso.isEnabled = false

        var horaSalida: String? = null
        var horaLlegada: String? = null
        val fechaInicio = binding.etSolicitudPermisoFechaInicio.text.toString().replace(" ", "")
        val fechafin = if (permisoHoras) {
            fechaInicio
        } else {
            binding.etSolicitudPermisoFechaFin.text.toString().replace(" ", "")
        }

        if (ll_solicitud_permiso_hora_llegada.isVisible) {
            horaSalida = binding.etSolicitudPermisoHoraSalida.text.toString().replace(" ", "")
        }
        if (ll_solicitud_permiso_hora_salida.isVisible) {
            horaLlegada = binding.etSolicitudPermisoHoraLlegada.text.toString().replace(" ", "")
        }

        val parametros = JSONObject().apply {
            put("FuncionarioId", App.idFuncionario)
            put("tipoAusentismoId", vmPermiso.idTipoPermiso)
            put("fechaInicio", fechaInicio)
            put("fechaFin", fechafin)
            put("horaSalida", horaSalida)
            put("horaLlegada", horaLlegada)
            put("observaciones", binding.edSolicitudPermisoObservacion.text.toString().trim())
        }


        val callbackRegistarSolicitudPermiso = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                idCreado = json.getString("id")

                mostrarVista()
                crearSnackbar(true).show()
                recargarSolicitudPermisos()
            }

            override fun error(error: VolleyError) {
                binding.btnGuardarSolicitudPermiso.isEnabled = true
                var errors: String? = String(error.networkResponse.data)
                if (errors?.isEmpty()!!) {
                    errors = "404"
                }
                mostrarVista()
                crearSnackbar(false).show()
                obtenerMensajesBacked(JSONObject(errors).getJSONObject("errors"))
            }
        }
        if (solicituPermisos == null) {
            registrarSolicitudPermiso(context!!, callbackRegistarSolicitudPermiso, parametros)
        } else {
            parametros.put("id", solicituPermisos.id)
            editarSolicitudPermiso(
                context!!,
                callbackRegistarSolicitudPermiso,
                parametros,
                solicituPermisos.id!!
            )
        }
    }

    private fun obtenerMensajesBacked(jsonErrors: JSONObject) {
        if (jsonErrors.has("snackbar")) {
            val errores = jsonErrors.getJSONArray("snackbar")
            val mensajes = StringBuilder()
            for (i in 0 until errores.length()) {
                mensajes.append(errores[i].toString() + "\n")
            }
            construirAlerta(context!!, R.layout.toas_login_warning, mensajes.toString())
        }
        if (jsonErrors.has("funcionarioId")) {
            val errores = jsonErrors.getJSONArray("funcionarioId")
            val mensajes = StringBuilder()
            for (i in 0 until errores.length()) {
                mensajes.append(errores[i].toString() + "\n")
            }
            construirAlerta(context!!, R.layout.toas_login_warning, mensajes.toString())
        }
        if (jsonErrors.has("tipoAusentismoId")) {
            val errores = jsonErrors.getJSONArray("tipoAusentismoId")
            val mensajes = StringBuilder()
            for (i in 0 until errores.length()) {
                mensajes.append(errores[i].toString() + "\n")
            }
            binding.msgErrorSolicitudPermisoTipoPermiso.text = mensajes.toString()
        }
        if (jsonErrors.has("fechaInicio")) {
            val errores = jsonErrors.getJSONArray("fechaInicio")
            val mensajes = StringBuilder()
            for (i in 0 until errores.length()) {
                mensajes.append(errores[i].toString() + "\n")
            }
            binding.msgErrorSolicitudPermisoFechaInicio.text = mensajes.toString()
        }
        if (jsonErrors.has("fechaFin")) {
            val errores = jsonErrors.getJSONArray("fechaFin")
            val mensajes = StringBuilder()
            for (i in 0 until errores.length()) {
                mensajes.append(errores[i].toString() + "\n")
            }
            binding.msgErrorSolicitudPermisoFechaFin.text = mensajes.toString()
        }
        if (jsonErrors.has("horaSalida")) {
            val errores = jsonErrors.getJSONArray("horaSalida")
            val mensajes = StringBuilder()
            for (i in 0 until errores.length()) {
                mensajes.append(errores[i].toString() + "\n")
            }
            binding.msgErrorSolicitudPermisoHoraSalida.text = mensajes.toString()
        }
        if (jsonErrors.has("horaLlegada")) {
            val errores = jsonErrors.getJSONArray("horaLlegada")
            val mensajes = StringBuilder()
            for (i in 0 until errores.length()) {
                mensajes.append(errores[i].toString() + "\n")
            }
            binding.msgErrorSolicitudPermisoHoraLlegada.text = mensajes.toString()
        }
        if (jsonErrors.has("observaciones")) {
            val errores = jsonErrors.getJSONArray("observaciones")
            val mensajes = StringBuilder()
            for (i in 0 until errores.length()) {
                mensajes.append(errores[i].toString() + "\n")
            }
            binding.msgErrorSolicitudPermisoObservaciones.text = mensajes.toString()
        }
    }

    private fun recargarSolicitudPermisos() {
        val callbackRecargarSolicitudPermiso = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                vmPermiso.eliminarSolicitudPermiso()

                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    if (item.getString("id") == idCreado) {
                        App.solicitudPermiso = SolicitudPermiso(item, i.plus(1).toString())
                    }

                    val permiso = SolicitudPermiso(item, i.plus(1).toString())
                    vmPermiso.insertarSolicitudPermiso(permiso)


                }

                val handler = Handler()

                handler.postDelayed(
                    {
                        if(solicituPermisos==null){
                            findNavController().navigate(R.id.action_permisoFormularioFragment_to_permisoSoporteFragment)
                        }else{
                            findNavController().navigate(R.id.action_permisoFormularioFragment_to_permisoFragment)
                        }
                    },
                    1500
                )
            }

            override fun error(error: VolleyError) {
            }
        }
        obtenerSolicitudPermiso(
            context!!,
            callbackRecargarSolicitudPermiso,
            App.idFuncionario.toString()
        )
    }

    private fun validarAntesdeEnviar(): Boolean {
        val validador = Validador()
        var retorno = true

        if (!validador.spinnerRequerido(
                binding.sSolicitudPermisoClasePermiso,
                binding.msgErrorSolicitudPermisoClasePermiso
            )
        ) retorno = false
        if (!validador.spinnerRequerido(
                binding.sSolicitudPermisoTipoPermiso,
                binding.msgErrorSolicitudPermisoTipoPermiso
            )
        ) retorno = false
        if (!validador.campoRequerido(
                binding.etSolicitudPermisoFechaInicio,
                binding.msgErrorSolicitudPermisoFechaInicio
            )
        ) retorno = false
        if (binding.llSolicituPermisoFechaFin.isVisible) {
            if (!validador.campoRequerido(
                    binding.etSolicitudPermisoFechaFin,
                    binding.msgErrorSolicitudPermisoFechaFin
                )
            ) retorno = false
        }

        if (binding.llSolicitudPermisoHoraSalida.isVisible) {
            if (!validador.campoRequerido(
                    binding.etSolicitudPermisoHoraSalida,
                    binding.msgErrorSolicitudPermisoHoraSalida
                )
            ) retorno = false
        }

        if (binding.llSolicitudPermisoHoraLlegada.isVisible) {
            if (!validador.campoRequerido(
                    binding.etSolicitudPermisoHoraLlegada,
                    binding.msgErrorSolicitudPermisoHoraLlegada
                )
            ) retorno = false
        }

        return retorno
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

    private fun cerrarTeclado() {
        val vista = activity?.currentFocus
        if (vista != null) {
            val input =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            input.hideSoftInputFromWindow(vista.windowToken, 0)
        }
    }

    private fun mostrarVista(vista: String? = null) {
        when (vista) {
            "Cargando" -> {
                binding.pbPermisosForulario.isVisible = true
                binding.svFormularioPermiso.isVisible = false
            }
            "Guardando" -> {
                binding.pbPermisosForulario.isVisible = true
            }
            else -> {
                binding.pbPermisosForulario.isVisible = false
                binding.svFormularioPermiso.isVisible = true
            }
        }
    }

    override fun seleccionarSoportePermiso(soportePermiso: SolicitudPermisoSoporte?) {

        val vista = LayoutInflater.from(context).inflate(R.layout.dialogo_botones, null)
        val textView = vista.findViewById<TextView>(R.id.texto_dialog)
        textView.text =
            getString(R.string.pregunta_eliminar_soporte, soportePermiso?.tipoSoporteNombre)

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
            elimarSoportePermisoApi(soportePermiso)
        }

        botonCancelar.setOnClickListener {
            dialogo.dismiss()
        }
    }

    private fun elimarSoportePermisoApi(soportePermiso: SolicitudPermisoSoporte?) {
        val callbackElimianrSoportePermiso = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                crearSnackbar(true).show()
                eliminarSoporteMongoApi(soportePermiso?.adjunto!!)
            }

            override fun error(error: VolleyError) {
                eliminarSoporteMongoApi(soportePermiso?.adjunto!!)
            }

        }
        elimianrSoporteSolicitudPermiso(
            context!!,
            soportePermiso?.id!!,
            callbackElimianrSoportePermiso
        )
    }

    private fun eliminarSoporteMongoApi(objectId: String) {
        val callbackElimianrSoportePermisoMongo = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                crearSnackbar(true).show()
                obtenerSoportePermisoApi()
            }

            override fun error(error: VolleyError) {
                obtenerSoportePermisoApi()
            }
        }
        eliminarArchivoServer(
            context!!, objectId, callbackElimianrSoportePermisoMongo
        )
    }

    fun obtenerSoportePermisoApi() {
        val callbackSoportePermiso = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                listaSoporte.clear()
                vmPermiso.eliminarSoporteSolicitudPermisoById(solicituPermisos?.id!!)
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    val permiso = SolicitudPermisoSoporte(item, (i + 1).toString())
                    Log.i(item.getInt("solicitudPermisoId").toString(),solicituPermisos.id.toString())
                    if(item.getInt("solicitudPermisoId")==solicituPermisos.id){
                        listaSoporte.add(permiso)
                    }
                    vmPermiso.insertarSoporteSolicitudPermiso(permiso)

                }
                adapterRVSoportePermiso.notifyDataSetChanged()
            }

            override fun error(error: VolleyError) {
                val codigo = error.networkResponse.statusCode
                construirAlerta(
                    context!!,
                    R.layout.toas_login_warning,
                    getString(R.string.mensaje_eror_404, codigo.toString())
                )
            }
        }
        obtenerSoportePermiso(context!!, callbackSoportePermiso, App.idFuncionario.toString(),solicituPermisos?.id.toString())
    }

}
