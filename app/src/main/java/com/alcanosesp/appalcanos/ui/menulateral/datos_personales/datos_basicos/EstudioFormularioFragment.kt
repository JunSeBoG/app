package com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos
//402
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.alcanosesp.appalcanos.App

import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.adapter.DatePickerAdapter
import com.alcanosesp.appalcanos.adapter.SpinnerAdapter
import com.alcanosesp.appalcanos.api.IRespuestaServidor
import com.alcanosesp.appalcanos.api.editarEstudio
import com.alcanosesp.appalcanos.api.obtenerEstudios
import com.alcanosesp.appalcanos.api.registrarEstudio
import com.alcanosesp.appalcanos.databinding.FragmentEstudioFormularioBinding
import com.alcanosesp.appalcanos.db.entity.Estudio
import com.alcanosesp.appalcanos.model.ItemSpinner
import com.alcanosesp.appalcanos.utils.Validador
import com.android.volley.VolleyError
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
//960 tamaño drodown spinner
class EstudioFormularioFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(EstudiosViewModel::class.java)
    }
    private var estudio: Estudio? = App.estudio
    private var id: Int? = null

    private lateinit var binding: FragmentEstudioFormularioBinding

    //SE PUEDE HACER GENERAL CON VIEW MODEL Y OTRA CLASE
    private val estados: ArrayList<ItemSpinner> = ArrayList()

    private lateinit var adapterPaises: SpinnerAdapter
    private lateinit var adapterProfesiones: SpinnerAdapter
    private lateinit var adapterNivelesEducativos: SpinnerAdapter
    private lateinit var adapterEstadosEstudio: SpinnerAdapter

    private val touchListener = object : View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (binding.etEstudioObservaciones.hasFocus()) {
                v!!.parent.requestDisallowInterceptTouchEvent(true)
                when (event!!.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_SCROLL -> {
                        v.parent.requestDisallowInterceptTouchEvent(false)
                        return true
                    }
                }
            }
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (estudio == null){
            (activity as AppCompatActivity?)!!.supportActionBar?.title = "Crear estudio"
        }else {
            (activity as AppCompatActivity?)!!.supportActionBar?.title = "Editar estudio"
            id = estudio?.id
        }

        adapterPaises = SpinnerAdapter(context!!)
        adapterProfesiones = SpinnerAdapter(context!!)
        adapterEstadosEstudio = SpinnerAdapter(context!!)
        adapterNivelesEducativos = SpinnerAdapter(context!!)

        viewModel.listaPaises.observe(this, Observer { this.adapterPaises.setItems(it) })
        viewModel.listaProfesiones.observe(this, Observer { this.adapterProfesiones.setItems(it) })
        viewModel.listaNivelesEducativos.observe(this, Observer { this.adapterNivelesEducativos.setItems(it) })

        viewModel.obtenerPaisesApi()
        viewModel.obtenerProfesionesApi()
        viewModel.obtenerNivelesEducativosApi()


        estados.add(ItemSpinner(1,"En curso"))
        estados.add(ItemSpinner(2,"Aplazado"))
        estados.add(ItemSpinner(3,"Abandonado"))
        estados.add(ItemSpinner(4,"Culminado"))

        adapterEstadosEstudio.setItems(estados)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_estudio_formulario, container, false)

        binding.estudio = estudio

        binding.btnGuardarEstudio.setOnClickListener {
            crearEstudio()
        }

        binding.etEstudioFechainicio.setOnClickListener {
            mostrarDateDialog(it as EditText)
        }

        binding.etEstudioFechafin.setOnClickListener {
            mostrarDateDialog(it as EditText)
        }

        binding.etEstudioObservaciones.setOnTouchListener(touchListener)

        /*binding.etEstudioProfesion.setAdapter(adapterProfesiones)
        binding.etEstudioProfesion.addTextChangedListener(textWatcherProfesiones)
        binding.etEstudioProfesion.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, _, i, _ ->
                val seleccion = adapterView.getItemAtPosition(i) as ItemAutocompletable
                viewModel.profesionSeleccionada(seleccion.id)
                binding.etEstudioProfesion.setText(seleccion.nombre)
            }*/
        binding.spEstudioPais.adapter = adapterPaises
        binding.spEstudioPais.dropDownWidth = 960
        binding.spEstudioPais.dropDownHorizontalOffset = -30
        binding.spEstudioPais.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seleccion = parent?.getItemAtPosition(position) as ItemSpinner
                viewModel.paisSeleccionado(seleccion.id)
            }
        }
        binding.spEstudioProfesion.adapter = adapterProfesiones
        binding.spEstudioProfesion.dropDownWidth = 960
        binding.spEstudioProfesion.dropDownHorizontalOffset = -30
        binding.spEstudioProfesion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seleccion = parent?.getItemAtPosition(position) as ItemSpinner
                viewModel.profesionSeleccionada(seleccion.id)
            }
        }
        binding.spEstudioNiveleducativo.adapter = adapterNivelesEducativos
        binding.spEstudioNiveleducativo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seleccion = parent?.getItemAtPosition(position) as ItemSpinner
                viewModel.nivelEducativoSeleccionado(seleccion.id)
            }
        }
        binding.spEstudioEstado.adapter = adapterEstadosEstudio
        binding.spEstudioEstado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seleccion = parent?.getItemAtPosition(position) as ItemSpinner

                when {
                    seleccion.nombre.equals("Culminado") -> {
                        binding.bloqueFechaFin.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.bloqueFechaFin.visibility = View.GONE
                        binding.etEstudioFechafin.text = null
                    }
                }
                viewModel.estadoEstudioSeleccionado(seleccion.nombre)
            }
        }

        //Seteo la info en los spinners cuando hay edicion
        //OCULTAR FORMULARIO MIENTRAS CARGA POSIBLEMENTE
        if (estudio != null){
            val handler = Handler()
            handler.postDelayed({ asignarSpinnersEdicion() }, 700)
        }

        return binding.root
    }

    private fun asignarSpinnersEdicion(){
        var posicionItemSpinner = 0

        posicionItemSpinner = adapterNivelesEducativos.obtenerPosicionValor(ItemSpinner(estudio?.nivelEducativoId!!.toInt(), estudio?.nivelEducativoNombre!!))
        binding.spEstudioNiveleducativo.setSelection(posicionItemSpinner)

        posicionItemSpinner = adapterEstadosEstudio.obtenerPosicionNombre(estudio?.estadoEstudio!!.camelToSnakeCase())
        binding.spEstudioEstado.setSelection(posicionItemSpinner)

        when (adapterEstadosEstudio.getItem(posicionItemSpinner)?.nombre) {
            "Culminado" -> {
                binding.etEstudioFechafin.setText(estudio!!.fechaFin)
            }
        }

        posicionItemSpinner = adapterProfesiones.obtenerPosicionValor(ItemSpinner(estudio?.profesionId!!.toInt(), estudio?.profesionNombre!!))
        binding.spEstudioProfesion.setSelection(posicionItemSpinner)

        posicionItemSpinner = adapterPaises.obtenerPosicionValor(ItemSpinner(estudio?.paisId!!.toInt(), estudio?.paisNombre!!))
        binding.spEstudioPais.setSelection(posicionItemSpinner)
    }

    //HACER GENERAL
    val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()
    fun String.camelToSnakeCase(): String {
        return camelRegex.replace(this) {
            " ${it.value}"
        }.toLowerCase(Locale.getDefault()).capitalize()
    }


    private fun crearEstudio(){
        cerrarTeclado()
        if (validarAntesdeEnviar()){
            enviarEstudio()
        }else{
            crearSnackbar(false).show()
        }
    }

    private fun enviarEstudio() {
        binding.btnGuardarEstudio.isEnabled = false

        val parametros = JSONObject()
        parametros.put("FuncionarioId", App.idFuncionario)
        parametros.put("NivelEducativoId",viewModel.idNivelEducativoSeleccionado)
        parametros.put("InstitucionEducativa", binding.etEstudioInstitucion.text.toString())
        parametros.put("EstadoEstudio", viewModel.nombreEstadoEstudioSeleccionado?.trim().toString())
        parametros.put("PaisId", viewModel.idPaisSeleccionado)
        parametros.put("AnioDeInicio",binding.etEstudioFechainicio.text.toString())
        parametros.put("AnioDeFin",binding.etEstudioFechafin.text.toString())
        parametros.put("TarjetaProfesional",binding.etEstudioNtarjetaprof.text.toString())
        parametros.put("ProfesionId", viewModel.idProfesionSeleccionada)
        parametros.put("Titulo", binding.etEstudioTitulo.text.toString())
        parametros.put("Observacion",binding.etEstudioObservaciones.text.toString())

        val callbackEstudio = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                crearSnackbar(true).show()
                recargarEstudios()
            }

            override fun error(error: VolleyError) {
                binding.btnGuardarEstudio.isEnabled = true

                val errors = String(error.networkResponse.data)
                Log.i("ERRORES", errors)
                crearSnackbar(false).show()
                obtenerMensajesBacked(JSONObject(errors).getJSONObject("errors"))
            }
        }

        if (estudio == null){
            registrarEstudio(context!!, callbackEstudio, parametros)
        }else{
            parametros.put("id", id)
            editarEstudio(context!!, callbackEstudio, parametros, id!!)
        }
    }

    private fun obtenerMensajesBacked(jsonErrors: JSONObject) {
        if (jsonErrors.has("anioDeInicio")) {
            val errores = jsonErrors.getJSONArray("anioDeInicio")
            val mensajes = StringBuilder()
            for (i in 0 until errores.length()) {
                mensajes.append(errores[i].toString() + "\n")
            }
            binding.msgErrorEstudioFechainicio.text = mensajes.toString()
            binding.msgErrorEstudioFechafin.text = "La fecha de finalización no puede ser menor a la fecha de inicio."
        }
    }

    private fun recargarEstudios(){
        val callbackEstudios = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                viewModel.eliminarEstudios()

                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    val estudio = Estudio(item)

                    viewModel.insertarEstudio(estudio)
                }

                val handler = Handler()
                handler.postDelayed({
                    findNavController().navigate(R.id.action_estudioFormularioFragment_to_nav_datos_personales)
                    App.estudio = null },
                    1500)
            }

            override fun error(error: VolleyError) {
            }
        }
        obtenerEstudios(context!!, callbackEstudios, App.idFuncionario.toString())
    }

    private fun validarAntesdeEnviar() : Boolean{
        val validador = Validador()
        var retorno = true

        //HACER VALIDACIONES SPINNERS REQUERIDOS COMPLETAS
        if(!validador.spinnerRequerido(binding.spEstudioNiveleducativo, binding.msgErrorEstudioNiveleducativo)) retorno = false

        if(!validador.campoRequerido(binding.etEstudioInstitucion, binding.msgEstudioInstitucion)) retorno = false

        if(!validador.spinnerRequerido(binding.spEstudioEstado, binding.msgErrorEstudioEstado)) retorno = false

        if(!validador.spinnerRequerido(binding.spEstudioPais, binding.msgErrorEstudioPais)) retorno = false

        //Datepicker - validacion
        if(!validador.campoRequerido(binding.etEstudioFechainicio, binding.msgErrorEstudioFechainicio)) retorno = false

        if(!validador.campoTarjetaProfesional(binding.etEstudioNtarjetaprof, binding.msgErrorEstudioNtarjetaprof, false)) retorno = false

        if(!validador.campoAlfabetico(binding.etEstudioTitulo, binding.msgErrorEstudioTitulo, true)) retorno = false

        return retorno
    }

    //SE PUEDE HACER GENERAL
    private fun cerrarTeclado(){
        val vista =   activity?.currentFocus
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