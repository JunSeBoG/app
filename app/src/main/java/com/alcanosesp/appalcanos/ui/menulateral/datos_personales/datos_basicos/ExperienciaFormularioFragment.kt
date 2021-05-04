package com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos
//319
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
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
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.alcanosesp.appalcanos.App

import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.adapter.DatePickerAdapter
import com.alcanosesp.appalcanos.adapter.SpinnerAdapter
import com.alcanosesp.appalcanos.api.*
import com.alcanosesp.appalcanos.databinding.FragmentExperienciaFormularioBinding
import com.alcanosesp.appalcanos.db.entity.Experiencia
import com.alcanosesp.appalcanos.model.ItemSpinner
import com.alcanosesp.appalcanos.utils.Validador
import com.android.volley.VolleyError
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

class ExperienciaFormularioFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(ExperienciasViewModel::class.java)
    }
    private var experiencia: Experiencia? = App.experiencia
    private var id: Int? = null

    private lateinit var binding: FragmentExperienciaFormularioBinding

    //SE PUEDE HACER GENERAL CON VIEW MODEL Y OTRA CLASE
    private val trabajaActualmente: ArrayList<ItemSpinner> = ArrayList()

    private lateinit var adapterTrabajaActualmente: SpinnerAdapter

    private val touchListener = object : View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (binding.etExperienciaMotivoretiro.hasFocus()) {
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

        if (experiencia == null){
            (activity as AppCompatActivity?)!!.supportActionBar?.title = "Crear experiencia"
        }else {
            (activity as AppCompatActivity?)!!.supportActionBar?.title = "Editar experiencia"
            id = experiencia?.id
        }

        adapterTrabajaActualmente = SpinnerAdapter(context!!)

        //SE PUEDE HACER GENERAL CON VIEW MODEL Y OTRA CLASE
        trabajaActualmente.add(ItemSpinner(1,"Si"))
        trabajaActualmente.add(ItemSpinner(2,"No"))

        //SE PUEDE HACER GENERAL CON VIEW MODEL Y OTRA CLASE
        adapterTrabajaActualmente.setItems(trabajaActualmente)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_experiencia_formulario, container, false)

        binding.experiencia = experiencia

        binding.etExperienciaMotivoretiro.setOnTouchListener(touchListener)
        binding.etExperienciaObservaciones.setOnTouchListener(touchListener)
        binding.etExperienciaFunciones.setOnTouchListener(touchListener)

        binding.btnGuardarExperiencia.setOnClickListener {
            crearExperiencia()
        }

        binding.etExperienciaFechaInicio.setOnClickListener {
            mostrarDateDialog(it as EditText)
        }

        binding.etExperienciaFechafin.setOnClickListener {
            mostrarDateDialog(it as EditText)
        }

        binding.etExperienciaTrabaja.adapter = adapterTrabajaActualmente
        binding.etExperienciaTrabaja.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seleccion = parent?.getItemAtPosition(position) as ItemSpinner

                if (seleccion.nombre.equals("No")){
                    binding.bloqueFechaFin.visibility = View.VISIBLE
                }else {
                    binding.bloqueFechaFin.visibility = View.GONE
                    binding.etExperienciaFechafin.text = null
                }
                viewModel.trabajaActualmenteSeleccion(seleccion.nombre)
            }
        }

        //Seteo la info en los spinners cuando hay edición
        //OCULTAR FORMULARIO MIENTRAS CARGA POSIBLEMENTE
        if (experiencia != null){
            val handler = Handler()
            handler.postDelayed({ asignarSpinnersEdicion() }, 700)
        }

        return binding.root
    }

    private fun asignarSpinnersEdicion() {
        val posicionItemSpinner = adapterTrabajaActualmente.obtenerPosicionNombre(experiencia?.trabajaActualmente!!)
        binding.etExperienciaTrabaja.setSelection(posicionItemSpinner)

        //ARREGLAR SI SE PUEDE
        if (adapterTrabajaActualmente.getItem(posicionItemSpinner)?.nombre.equals("No")){
            binding.etExperienciaFechafin.setText(experiencia?.fechaFin)
        }
    }

    private fun crearExperiencia(){
        cerrarTeclado()
        if (validarAntesdeEnviar()){
            enviarEstudio()
        }else{
            crearSnackbar(false).show()
        }
    }

    private fun enviarEstudio() {
        binding.btnGuardarExperiencia.isEnabled = false

        val parametros = JSONObject()
        parametros.put("FuncionarioId", App.idFuncionario)
        parametros.put("nombreCargo", binding.etExperienciaCargo.text.toString())
        parametros.put("nombreEmpresa", binding.etExperienciaEmpresa.text.toString())
        parametros.put("telefono", binding.etExperienciaTelefono.text.toString())
        parametros.put("salario", binding.etExperienciaSalario.text.toString())
        parametros.put("nombreJefeInmediato", binding.etExperienciaJefeinmediato.text.toString())
        parametros.put("trabajaActualmente", toBool(viewModel.trabajaActualmente!!))
        parametros.put("fechaInicio", binding.etExperienciaFechaInicio.text.toString())
        parametros.put("fechaFin", binding.etExperienciaFechafin.text.toString())
        parametros.put("funcionesCargo", binding.etExperienciaFunciones.text.toString())
        parametros.put("motivoRetiro",binding.etExperienciaMotivoretiro.text.toString())
        parametros.put("observaciones",binding.etExperienciaObservaciones.text.toString())

        val callbackEstudio = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                crearSnackbar(true).show()
                recargarExperiencias()
            }

            override fun error(error: VolleyError) {
                binding.btnGuardarExperiencia.isEnabled = true

                //val errors = String(error.networkResponse.data)
                crearSnackbar(false).show()
                //obtenerMensajesBacked(JSONObject(errors).getJSONObject("errors"))
            }
        }

        if (experiencia == null){
            registrarExperiencia(context!!, callbackEstudio, parametros)
        }else{
            parametros.put("id", id)
            editarExperiencia(context!!, callbackEstudio, parametros, id!!)
        }
    }

    private fun recargarExperiencias(){
        val callbackExperiencias = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                viewModel.eliminarExperiencias()

                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    val experiencia = Experiencia(item)

                    viewModel.insertarExperiencia(experiencia)
                }

                val handler = Handler()

                handler.postDelayed({
                    findNavController().navigate(R.id.action_experienciaFormularioFragment_to_datos_personales)
                    App.experiencia = null },
                    1500)
            }

            override fun error(error: VolleyError) {
            }
        }
        obtenerExperiencias(context!!, callbackExperiencias, App.idFuncionario.toString())
    }

    private fun validarAntesdeEnviar() : Boolean{
        val validador = Validador()
        var retorno = true

        if(!validador.campoAlfanumerico(binding.etExperienciaCargo, binding.msgErrorExperienciaCargo, true)) retorno = false

        if(!validador.campoAlfanumerico(binding.etExperienciaEmpresa, binding.msgErrorExperienciaEmpresa, true)) retorno = false

        if(!validador.campoTelefono(binding.etExperienciaTelefono, binding.msgErrorExperienciaTelefono, true)) retorno = false

        if(!validador.campoNumerico(binding.etExperienciaSalario, binding.msgErrorExperienciaSalario, false)) retorno = false

        if(!validador.campoAlfabetico(binding.etExperienciaJefeinmediato, binding.msgErrorExperienciaJefeinmediato, false)) retorno = false

        if(!validador.spinnerRequerido(binding.etExperienciaTrabaja, binding.msgErrorExperienciaTrabaja)) retorno = false

        //BACKEND
        if(!validador.campoRequerido(binding.etExperienciaFechaInicio, binding.msgErrorExperienciaFechainicio)) retorno = false

        return retorno
    }

    //Se puede hacer general
    private fun toBool(s: String): Boolean {
        return when (s) {
            "Si" -> true
            else -> false
        }
    }

    //SE PUEDE HACER GENERAL
    private fun cerrarTeclado(){
        val vista =   activity?.currentFocus
        if (vista != null){
            val input  = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            input.hideSoftInputFromWindow(vista.windowToken, 0)
        }
    }

    //Se puede hacer general
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

    //Se puede hacer general
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