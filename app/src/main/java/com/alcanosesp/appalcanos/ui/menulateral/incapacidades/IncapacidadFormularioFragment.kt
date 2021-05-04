package com.alcanosesp.appalcanos.ui.menulateral.incapacidades

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
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
import com.alcanosesp.appalcanos.adapter.DropDownAdapter
import com.alcanosesp.appalcanos.adapter.SpinnerAdapter
import com.alcanosesp.appalcanos.api.*
import com.alcanosesp.appalcanos.databinding.FragmentIncapacidadFormularioBinding
import com.alcanosesp.appalcanos.db.entity.AusentismoFuncionario
import com.alcanosesp.appalcanos.model.ItemAutocompletable
import com.alcanosesp.appalcanos.model.ItemSpinner
import com.alcanosesp.appalcanos.utils.*
import com.android.volley.VolleyError
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

class IncapacidadFormularioFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(IncapacidadViewModel::class.java)
    }
    private val viewModelArchivo by lazy {
        ViewModelProviders.of(this).get(ArchivoAdjuntoViewModel::class.java)
    }

    private var incapacidad: AusentismoFuncionario? = App.incapacidad
    private var id: Int? = null

    private lateinit var binding: FragmentIncapacidadFormularioBinding
    private lateinit var adaptadorDiagnosticoCie: DropDownAdapter
    private lateinit var adaptadorTipoIncapacidad: SpinnerAdapter
    private lateinit var adaptadorProrrogaDe: SpinnerAdapter


    private val textWatcherDiagnostico = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (!(s.isNullOrBlank() || s.isEmpty())) {
                binding.msgErrorIncapacidadDiagnostico.text = ""
                viewModel.obtenerDiagnosticoCieApi(s)
            }
        }
    }

    private val textWatcherFechaInicio = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            viewModel.obtenerProrrogaDeApi(
                binding.etIncapacidadFechaInicio.text.toString(),
                App.idFuncionario!!
            )
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (incapacidad == null) {
            (activity as AppCompatActivity?)!!.supportActionBar?.title = "Registrar incapacidad"
        } else {
            (activity as AppCompatActivity?)!!.supportActionBar?.title = "Editar incapacidad"
            id = incapacidad?.id
            viewModel.obtenerProrrogaDeApi(incapacidad!!.fechaInicio, App.idFuncionario!!)
        }

        adaptadorDiagnosticoCie = DropDownAdapter(context!!)
        adaptadorTipoIncapacidad = SpinnerAdapter(context!!)
        adaptadorProrrogaDe = SpinnerAdapter(context!!)

        viewModel.listaDiagnosticoCie.observe(
            this,
            Observer { this.adaptadorDiagnosticoCie.setItems(it) })

        viewModel.listaTipoIncapacidad.observe(
            this,
            Observer { this.adaptadorTipoIncapacidad.setItems(it) })

        viewModel.listaProrrogaDe.observe(
            this,
            Observer { this.adaptadorProrrogaDe.setItems(it) })


        viewModel.obtenerTipoIncapacidadApi()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_incapacidad_formulario,
            container,
            false
        )

        binding.incapacidad = incapacidad

        binding.btnGuardarIncapacidad.setOnClickListener {
            viewModelArchivo.obtenerArchivoAdjunto()

        }
        viewModelArchivo.archivoAdjunto.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                for (archivo in it) {
                    if (archivo.objectId.isNullOrEmpty()) {
                        binding.msgErrorIncapacidadAdjunto.text = getString(R.string.msg_requerido)
                    } else {
                        binding.etIncapacidadAdjunto.text = archivo.objectId
                    }
                }
            }
            crearIncapacidad()
        })

        binding.etIncapacidadFechaInicio.addTextChangedListener(textWatcherFechaInicio)
        binding.etIncapacidadFechaInicio.setOnClickListener {
            mostrarDateDialog(it as EditText)

        }

        binding.etIncapacidadFechaFin.setOnClickListener {
            if (binding.etIncapacidadFechaInicio.text.isNullOrEmpty()) {
                binding.msgErrorIncapacidadFechainicio.text = getString(R.string.msg_requerido)
            } else {
                mostrarDateDialog(it as EditText)
            }
        }

        /**Mustra el dialogo para Gargar Adjuntos***/
        binding.etIncapacidadLabelAdjunto.setOnClickListener(View.OnClickListener {
            val cargarAdjuntoFragment = CargarAdjunto()
            cargarAdjuntoFragment.show(requireFragmentManager(), "CargarAdjuntoFragment")
        })



        binding.etIncapacidadDiagnostico.setAdapter(adaptadorDiagnosticoCie)
        binding.etIncapacidadDiagnostico.addTextChangedListener(textWatcherDiagnostico)
        binding.etIncapacidadDiagnostico.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, _, i, _ ->
                val seleccion = adapterView.getItemAtPosition(i) as ItemAutocompletable
                viewModel.diagnosticoCieSeleccionadoSeleccionado(seleccion.id)
                binding.etIncapacidadDiagnostico.setText(seleccion.nombre)
            }


        binding.etIncapacidadProrrogaDe.adapter = adaptadorProrrogaDe
        binding.etIncapacidadProrrogaDe.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val seleccion = parent?.getItemAtPosition(position) as ItemSpinner
                    viewModel.prorrogaDeSeleccionada(seleccion.id)
                }
            }

        binding.etIncapacidadTipo.adapter = adaptadorTipoIncapacidad
        binding.etIncapacidadTipo.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val seleccion = parent?.getItemAtPosition(position) as ItemSpinner
                    viewModel.tipoIncapacidadSeleccionada(seleccion.id)
                }
            }

        //Seteo la info en los spinners cuando hay edicion
        //OCULTAR FORMULARIO MIENTRAS CARGA POSIBLEMENTE
        if (incapacidad != null) {
            val handler = Handler()
            handler.postDelayed({ asignarSpinnersEdicion() }, 700)
        }

        return binding.root
    }

    private fun asignarSpinnersEdicion() {
        var posicionItemSpinner = 0

        posicionItemSpinner = adaptadorTipoIncapacidad.obtenerPosicionValor(
            ItemSpinner(
                incapacidad?.tipoAusentismoId!!,
                incapacidad?.tipoAusentismoNombre!!
            )
        )
        binding.etIncapacidadTipo.setSelection(posicionItemSpinner)

        posicionItemSpinner = adaptadorProrrogaDe.obtenerPosicionNombre(incapacidad?.prorrogaDe)
        binding.etIncapacidadProrrogaDe.setSelection(posicionItemSpinner)
    }

    private fun crearIncapacidad() {
        cerrarTeclado()
        if (validarAntesdeEnviar()) {
            enviarIncapacidad()
        } else {
            crearSnackbar(false).show()
        }
    }

    private fun enviarIncapacidad() {
        binding.btnGuardarIncapacidad.isEnabled = false

        val parametros = JSONObject()
        val fechaInicio = binding.etIncapacidadFechaInicio.text.toString().replace(" ", "")
        val fechafin = binding.etIncapacidadFechaFin.text.toString().replace(" ", "")
        var prorroga: String? = viewModel.idProrrogaDeSelecionda.toString()
        if (prorroga == "0") {
            prorroga = null
        }

        parametros.put("FuncionarioId", App.idFuncionario)
        parametros.put("TipoAusentismoId", viewModel.idTipoIncapacidadSeleccionado)
        parametros.put("Diagnostico", binding.etIncapacidadDiagnostico.text.toString())
        parametros.put("fechaInicio", fechaInicio)
        parametros.put("fechaFin", fechafin)
        parametros.put("HoraInicio", "0")
        parametros.put("HoraFin", "0")
        parametros.put("NumeroIncapacidad", binding.etIncapacidadNumero.text.toString())
        parametros.put("ProrrogaId", prorroga)
        parametros.put("adjunto", binding.etIncapacidadAdjunto.text.toString())


        val callbackEstudio = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                crearSnackbar(true).show()
                recargarIncapacidades()
            }

            override fun error(error: VolleyError) {
                binding.btnGuardarIncapacidad.isEnabled = true

                val errors = String(error.networkResponse.data)
                crearSnackbar(false).show()
                obtenerMensajesBacked(JSONObject(errors).getJSONObject("errors"))
            }
        }

        if (incapacidad == null) {
            registrarIncapacidad(context!!, callbackEstudio, parametros)
        } else {
            parametros.put("id", id)
            editarIncapacidad(context!!, callbackEstudio, parametros, id!!)
        }
    }

    private fun recargarIncapacidades() {
        val callbackExperiencias = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                viewModel.eliminarIncapacidad()
                var filtro: String = " or ausentismoId eq "

                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")
                val numroIncapacidades = valueArr.length() - 1

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    val incapacidad = AusentismoFuncionario(item, i.plus(1).toString())
                    filtro += " or ausentismoId eq " + item.getString("id")

                    if (numroIncapacidades == i) {
                        obtenerPrrorrogas(filtro.replace("or ausentismoId eq  or ", " "))
                    }

                    viewModel.insertarIncapacidad(incapacidad)
                }

                val handler = Handler()

                handler.postDelayed(
                    {
                        findNavController().navigate(R.id.action_incapacidadFormularioFragment_to_nav_incapacidades2)
                        App.experiencia = null
                    },
                    1500
                )
            }

            override fun error(error: VolleyError) {
            }
        }
        obtenerIncapacidad(context!!, callbackExperiencias, App.idFuncionario.toString())
    }

    private fun validarAntesdeEnviar(): Boolean {
        val validador = Validador()
        var retorno = true

        if (!validador.spinnerRequerido(
                binding.etIncapacidadTipo, binding.msgErrorIncapacidadTipo
            )
        ) retorno = false

        if (!validador.campoRequerido(
                binding.etIncapacidadDiagnostico, binding.msgErrorIncapacidadDiagnostico
            )
        ) retorno = false


        if (!validador.campoRequerido(
                binding.etIncapacidadFechaInicio, binding.msgErrorIncapacidadFechainicio
            )
        ) retorno = false
        //if(!validador.campoFecha(binding.etIncapacidadFechaInicio, binding.msgErrorIncapacidadFechainicio)) retorno = false

        if (!validador.campoRequerido(
                binding.etIncapacidadFechaFin, binding.msgErrorIncapacidadFechaFin
            )
        ) retorno = false
        //if(!validador.campoFecha(binding.etIncapacidadFechaInicio, binding.msgErrorIncapacidadFechainicio)) retorno = false

        if (!validador.campoRequerido(
                binding.etIncapacidadNumero, binding.msgErrorIncapacidadNumero
            )
        ) retorno = false
        if (!validador.campoNumerico(
                binding.etIncapacidadNumero, binding.msgErrorIncapacidadNumero,
                true
            )
        ) retorno = false

        if (!validador.textViewRequerido(
                binding.etIncapacidadAdjunto, binding.msgErrorIncapacidadAdjunto
            )
        ) retorno = false

        return retorno
    }

    //SE PUEDE HACER GENERAL
    private fun cerrarTeclado() {
        val vista = activity?.currentFocus
        if (vista != null) {
            val input =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
    private fun mostrarDateDialog(et: EditText) {
        val newFragment =
            DatePickerAdapter.newInstance(DatePickerDialog.OnDateSetListener { _, anio, mes, dia ->
                val diaSeleccionado = dia.dosDigitos()
                val mesSeleccionado = (mes + 1).dosDigitos()

                val fechaSeleccionada = "$anio - $mesSeleccionado - $diaSeleccionado"
                et.setText(fechaSeleccionada)
            })

        newFragment.show(fragmentManager!!, "datePicker")
    }

    private fun obtenerPrrorrogas(filtro: String) {

        val callbacklistaIdincapacidad = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val validador = JSONValidador()
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")
                for (i in 0 until valueArr.length()) {

                    val item = valueArr.getJSONObject(i)
                    val ausentismoId = item.getString("ausentismoId")

                    val codigo = validador.jsonNuloSegundoGrado(
                        item,
                        "ausentismo",
                        "diagnosticoCie",
                        "codigo"
                    )
                    val fechainicial =
                        validador.jsonNuloPrimerGrado(item, "prorroga", "fechaInicio")
                    val fechaFinal = validador.jsonNuloPrimerGrado(item, "prorroga", "fechaFin")
                    val prorroga =
                        codigo.plus(" (").plus(fechainicial).plus(" - ").plus(fechaFinal).plus(" )")

                    viewModel.ActualizarIncapacidadProroga(prorroga, ausentismoId.toInt())
                }
            }

            override fun error(error: VolleyError) {
            }
        }
        obtenerIncapaciadProrrogaPorId(context!!, filtro, callbacklistaIdincapacidad)
    }

    private fun obtenerMensajesBacked(jsonErrors: JSONObject) {

        obtenerMensajesJsonLabel(jsonErrors, "tipoAusentismoId", binding.msgErrorIncapacidadTipo)
        obtenerMensajesJsonLabel(jsonErrors, "diagnostico", binding.msgErrorIncapacidadDiagnostico)
        obtenerMensajesJsonLabel(jsonErrors, "fechaInicio", binding.msgErrorIncapacidadFechainicio)
        obtenerMensajesJsonLabel(jsonErrors, "fechaFin", binding.msgErrorIncapacidadFechaFin)
        obtenerMensajesJsonLabel(jsonErrors, "numeroIncapacidad", binding.msgErrorIncapacidadNumero)
        obtenerMensajesJsonLabel(jsonErrors, "prorrogaId", binding.msgErrorIncapacidadProrrogaDe)
        obtenerMensajesJsonLabel(jsonErrors, "adjunto", binding.msgErrorIncapacidadAdjunto)
    }
}
