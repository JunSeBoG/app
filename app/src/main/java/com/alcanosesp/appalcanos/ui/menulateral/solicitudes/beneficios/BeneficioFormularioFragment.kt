package com.alcanosesp.appalcanos.ui.menulateral.solicitudes.beneficios

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.alcanosesp.appalcanos.App
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.adapter.DatePickerAdapter
import com.alcanosesp.appalcanos.adapter.SpinnerAdapter
import com.alcanosesp.appalcanos.api.IRespuestaServidor
import com.alcanosesp.appalcanos.api.editarSolicitudBeneficio
import com.alcanosesp.appalcanos.api.obtenerSolicitudesBeneficios
import com.alcanosesp.appalcanos.api.registrarSolicitudBeneficio
import com.alcanosesp.appalcanos.databinding.FragmentBeneficioFormularioBinding
import com.alcanosesp.appalcanos.db.entity.SolicitudBeneficio
import com.alcanosesp.appalcanos.model.ItemSpinner
import com.alcanosesp.appalcanos.utils.Validador
import com.alcanosesp.appalcanos.utils.opcionAxulioEducativoNombres
import com.android.volley.VolleyError
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_beneficio_formulario.view.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class BeneficioFormularioFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(BeneficiosViewModel::class.java)
    }
    private var solicitud: SolicitudBeneficio? = App.solicitudBeneficio
    private var id: Int? = null

    private lateinit var binding: FragmentBeneficioFormularioBinding

    private lateinit var adapterTipoBeneficio: SpinnerAdapter
    private lateinit var adapterOpcionAuxilio: SpinnerAdapter

    //SE PUEDE HACER GENERAL CON VIEW MODEL Y OTRA CLASE
    private val opcionesAuxilio: ArrayList<ItemSpinner> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (solicitud == null){
            (activity as AppCompatActivity?)!!.supportActionBar?.title = "Registrar solicitud"
        }else {
            (activity as AppCompatActivity?)!!.supportActionBar?.title = "Editar solicitud"
            id = solicitud?.id
        }

        adapterTipoBeneficio = SpinnerAdapter(context!!)
        adapterOpcionAuxilio = SpinnerAdapter(context!!)

        viewModel.listaTiposBeneficio.observe(this, Observer { this.adapterTipoBeneficio.setItems(it) })

        opcionesAuxilio.add(ItemSpinner(1,"Opción 1: condonación"))
        opcionesAuxilio.add(ItemSpinner(2,"Opción 2: condonación y financiación"))

        adapterOpcionAuxilio.setItems(opcionesAuxilio)

        viewModel.obtenerTiposBeneficioApi()
        viewModel.obtenerRequisitosBeneficioApi(0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_beneficio_formulario, container, false)

        binding.solicitud = solicitud

        binding.btnGuardarSolicitud.setOnClickListener {
            crearSolicitud()
        }

        binding.etFechainicioEstudio.setOnClickListener {
            mostrarDateDialog(it as EditText)
        }

        binding.etFechafinEstudio.setOnClickListener {
            mostrarDateDialog(it as EditText)
        }

        binding.etValorSolicitado.apply{
            setSpacing(false)
            setDecimals(false)
            setSeparator(".")
        }

        binding.spTipoBeneficio.adapter = adapterTipoBeneficio
        binding.spTipoBeneficio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                ocultarControles()
                val seleccion = parent?.getItemAtPosition(position) as ItemSpinner
                viewModel.tipoBeneficioSeleccionado(seleccion.id)

                if(seleccion.id != 0){
                    viewModel.obtenerParametrosTipoBeneficio()
                    viewModel.obtenerRequisitosBeneficioApi(seleccion.id)
                }
            }
        }

        binding.spOpcionAuxilio.adapter = adapterOpcionAuxilio
        binding.spOpcionAuxilio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seleccion = parent?.getItemAtPosition(position) as ItemSpinner
                viewModel.opcionAuxilioSeleccionado(seleccion.nombre)
                Log.i("Seleccionado", "Seleccionado")
            }
        }

        viewModel.permiteValorSolicitado.observe(this, Observer {
            if(solicitud == null){
                binding.etValorSolicitado.setText("")
            }
            when(it){
                "true" -> {
                    binding.grupoValorSolicitado.visibility = View.VISIBLE
                    binding.grupoFechaInicio.visibility = View.GONE
                    binding.grupoFechaFinaliza.visibility = View.GONE
                }
            }
        })
        viewModel.permitePlazoMes.observe(this, Observer {
            if(solicitud == null){
                binding.etPlazoMaximo.setText("")
            }
            when(it) {
                "true" -> {
                    binding.grupoPlazoMaximo.visibility = View.VISIBLE
                    binding.grupoFechaInicio.visibility = View.GONE
                    binding.grupoFechaFinaliza.visibility = View.GONE
                }
            }
        })
        viewModel.permiteAuxilioEducativo.observe(this, Observer {
            if(solicitud == null) {
                binding.etFechainicioEstudio.setText("")
                binding.etFechafinEstudio.setText("")
                binding.spOpcionAuxilio.setSelection(0)
            }
            when(it) {
                "true" -> {
                    binding.grupoFechaInicio.visibility = View.VISIBLE
                    binding.grupoFechaFinaliza.visibility = View.VISIBLE
                    binding.grupoOpcionAuxilio.visibility = View.VISIBLE
                }
            }
        })
        viewModel.permitePermisoEstudio.observe(this, Observer {
            if(solicitud == null) {
                binding.etFechainicioEstudio.setText("")
                binding.etFechafinEstudio.setText("")
                binding.etCantidadHoras.setText("")
            }
            when(it) {
                "true" -> {
                    binding.grupoFechaInicio.visibility = View.VISIBLE
                    binding.grupoFechaFinaliza.visibility = View.VISIBLE
                    binding.grupoCantidadHoras.visibility = View.VISIBLE
                }
            }
        })
        viewModel.requisitosBeneficio.observe(this, Observer {
            if(it.isNotEmpty()){
                mostrarRequisitos(it)
            }
        })

        if (solicitud != null){
            val handler = Handler()
            handler.postDelayed({ asignarSpinnersEdicion() }, 500)
            binding.spTipoBeneficio.isEnabled = false
        }

        return binding.root
    }

    private fun mostrarRequisitos(lista: List<HashMap<String, String>>) {
        if (lista.isNotEmpty()){
            binding.grupoRequisitos.visibility = View.VISIBLE
            binding.listaRequisitosBeneficio.removeAllViews()
            for (i in lista.indices){
                val tvAdjunto = layoutInflater.inflate(R.layout.adjuntos_layout, null) as TextView
                tvAdjunto.apply {
                    text = lista[i]["nombre"]
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_attach_file, 0);
                }
                binding.listaRequisitosBeneficio.addView(tvAdjunto)
            }
        }else{
            binding.grupoRequisitos.visibility = View.VISIBLE
            binding.listaRequisitosBeneficio.removeAllViews()
        }
    }

    private fun asignarSpinnersEdicion(){
        var posicionItemSpinner = 0

        posicionItemSpinner = adapterTipoBeneficio.obtenerPosicionValor(ItemSpinner(solicitud?.tipoBeneficioId!!.toInt(), solicitud?.tipoBeneficioNombre!!))
        binding.spTipoBeneficio.setSelection(posicionItemSpinner)

        posicionItemSpinner = adapterOpcionAuxilio.obtenerPosicionNombre(opcionAxulioEducativoNombres[solicitud?.opcionAuxilioEducativo!!])
        binding.spOpcionAuxilio.setSelection(posicionItemSpinner)
    }

    private fun crearSolicitud(){
        cerrarTeclado()
        if (validarAntesdeEnviar()){
            enviarSolicitud()
        }else{
            crearSnackbar(false).show()
        }
    }

    private fun enviarSolicitud() {
        binding.btnGuardarSolicitud.isEnabled = false

        val c = Calendar.getInstance()
        val anio = c.get(Calendar.YEAR)
        val mes = c.get(Calendar.MONTH) + 1
        val dia = c.get(Calendar.DAY_OF_MONTH)

        val parametros = JSONObject()
        parametros.put("funcionarioId", App.idFuncionario)
        parametros.put("fechaSolicitud", "$anio-$mes-$dia")
        parametros.put("tipoBeneficioId", viewModel.idTipoBeneficioSeleccionado.toString())
        parametros.put("plazoMaximo", vacioANull(binding.etPlazoMaximo.text.toString()))
        parametros.put("valorSolicitud", vacioANull(binding.etValorSolicitado.cleanIntValue.toString()))
        parametros.put("cantidadHoraSemana", vacioANull(binding.etCantidadHoras.text.toString()))
        parametros.put("fechaInicioEstudio",  vacioANull(binding.etFechainicioEstudio.text.toString()))
        parametros.put("FechaFinalizacionEstudio",  vacioANull(binding.etFechafinEstudio.text.toString()))
        parametros.put("opcionAuxilioEducativo", vacioANull(viewModel.nombreOpcionAuxilioEducativoSeleccionado.toString()))
        parametros.put("observacion", binding.etSolicitudObservaciones.text.toString())

        Log.i("PARAMETTOS", parametros.toString())

        val callbackSolicitudBeneficio = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                crearSnackbar(true).show()
                recargarSolicitudesBeneficios()
            }

            override fun error(error: VolleyError) {
                binding.btnGuardarSolicitud.isEnabled = true

                val errors = String(error.networkResponse.data)
                Log.i("ERRORES", errors)
                crearSnackbar(false).show()
                // obtenerMensajesBacked(JSONObject(errors).getJSONObject("errors"))
            }
        }

        if (solicitud == null){
            registrarSolicitudBeneficio(context!!, callbackSolicitudBeneficio, parametros)
        }else{
            parametros.put("id", id)
            editarSolicitudBeneficio(context!!, callbackSolicitudBeneficio, parametros, id!!)
        }
    }

    //General
    private fun vacioANull(s: String): String?{
        return if (s.isEmpty() || s == "0"){
            null
        }else{
            s
        }
    }

    private fun recargarSolicitudesBeneficios(){
        val callbackEstudios = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                viewModel.eliminarSolicitudes()

                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    val solicitud = SolicitudBeneficio(item)

                    viewModel.insertarSolicitud(solicitud)
                }

                val handler = Handler()
                handler.postDelayed({
                    findNavController().navigate(R.id.action_beneficioFormularioFragment_to_beneficiosFragment)
                    App.solicitudBeneficio = null },
                    1500)
            }

            override fun error(error: VolleyError) {
            }
        }
        obtenerSolicitudesBeneficios(context!!, callbackEstudios, App.idFuncionario.toString())
    }

    private fun validarAntesdeEnviar() : Boolean{
        val validador = Validador()
        var retorno = true

        if(!validador.spinnerRequerido(binding.spTipoBeneficio, binding.msgErrorEstudioNiveleducativo)) retorno = false

        if(viewModel.permiteValorSolicitadoS.equals("true")){
            if(!validador.campoRequerido(binding.etValorSolicitado, binding.msgValorSolicitado)) retorno = false
        }

        if(viewModel.permitePlazoMesS.equals("true")){
            if(!validador.campoRequerido(binding.etPlazoMaximo, binding.msgPlazoMaximo)) retorno = false
        }

        if(viewModel.permiteAuxilioEducativoS.equals("true")){
            if(!validador.spinnerRequerido(binding.spOpcionAuxilio, binding.msgOpcionAuxilio)) retorno = false
            if(!validador.campoRequerido(binding.etFechainicioEstudio, binding.msgErrorFechainicioEstudio)) retorno = false
            if(!validador.campoRequerido(binding.etFechafinEstudio, binding.msgErrorFechafinEstudio)) retorno = false
        }

        if(viewModel.permitePermisoEstudioS.equals("true")){
            if(!validador.campoRequerido(binding.etFechainicioEstudio, binding.msgErrorFechainicioEstudio)) retorno = false
            if(!validador.campoRequerido(binding.etFechafinEstudio, binding.msgErrorFechafinEstudio)) retorno = false
            if(!validador.campoRequerido(binding.etCantidadHoras, binding.msgCantidadHoras)) retorno = false
        }

        return retorno
    }

    private fun cerrarTeclado(){
        val vista = activity?.currentFocus
        if (vista != null){
            val input  = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            input.hideSoftInputFromWindow(vista.windowToken, 0)
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

    fun ocultarControles(){
        binding.grupoValorSolicitado.visibility = View.GONE
        binding.grupoPlazoMaximo.visibility = View.GONE
        binding.grupoFechaInicio.visibility = View.GONE
        binding.grupoFechaFinaliza.visibility = View.GONE
        binding.grupoCantidadHoras.visibility = View.GONE
        binding.grupoOpcionAuxilio.visibility = View.GONE
        binding.grupoRequisitos.visibility = View.GONE
        binding.listaRequisitosBeneficio.removeAllViews()
    }

    private fun mostrarDateDialog(et : EditText) {
        val newFragment = DatePickerAdapter.newInstance(DatePickerDialog.OnDateSetListener { _, anio, mes, dia ->
            val diaSeleccionado = dia.dosDigitos()
            val mesSeleccionado = (mes + 1).dosDigitos()

            val fechaSeleccionada = "$anio - $mesSeleccionado - $diaSeleccionado"
            et.setText(fechaSeleccionada)
        })

        newFragment.show(fragmentManager!!, "datePicker")
    }

    private fun Int.dosDigitos() = if (this <= 9) "0$this" else this.toString()
}