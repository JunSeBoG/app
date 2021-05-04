package com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos
//475
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alcanosesp.appalcanos.App
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.adapter.SpinnerAdapter
import com.alcanosesp.appalcanos.api.*
import com.alcanosesp.appalcanos.databinding.FragmentBasicosBinding
import com.alcanosesp.appalcanos.db.entity.Funcionario
import com.alcanosesp.appalcanos.model.ItemSpinner
import com.alcanosesp.appalcanos.utils.Validador
import com.android.volley.VolleyError
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

class BasicosFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(BasicosViewModel::class.java)
    }

    private lateinit var funcionario: Funcionario
    private lateinit var binding: FragmentBasicosBinding

    private val lentes: ArrayList<ItemSpinner> = ArrayList()

    private lateinit var adapterPaises: SpinnerAdapter
    private lateinit var adapterDeptos: SpinnerAdapter
    private lateinit var adapterMunicipios: SpinnerAdapter
    private lateinit var adapterViviendas: SpinnerAdapter
    private lateinit var adapterLentes: SpinnerAdapter

    private val touchListenerS = View.OnTouchListener { _, _ ->
        cerrarTeclado()
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lentes.add(ItemSpinner(1, "Si"))
        lentes.add(ItemSpinner(2, "No"))

        adapterLentes = SpinnerAdapter(context!!)
        adapterPaises = SpinnerAdapter(context!!)
        adapterDeptos = SpinnerAdapter(context!!)
        adapterViviendas = SpinnerAdapter(context!!)
        adapterMunicipios = SpinnerAdapter(context!!)

        viewModel.listaPaises.observe(this, Observer { this.adapterPaises.setItems(it) })
        viewModel.listaDepartamentos.observe(this, Observer { this.adapterDeptos.setItems(it) })
        viewModel.listaMunicipios.observe(this, Observer { this.adapterMunicipios.setItems(it) })
        viewModel.listaTiposVivienda.observe(this, Observer { this.adapterViviendas.setItems(it) })
        this.adapterLentes.setItems(lentes)

        viewModel.obtenerTipoViviendasApi()
        viewModel.obtenerPaisesApi()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_basicos, container, false)

        bindearFuncionario()

        binding.spLentes.adapter = adapterLentes
        binding.spLentes.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seleccion = parent?.getItemAtPosition(position) as ItemSpinner
                viewModel.usaLentesSeleccion(seleccion.nombre)
            }
        }
        binding.spVivienda.adapter = adapterViviendas
        binding.spVivienda.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seleccion = parent?.getItemAtPosition(position) as ItemSpinner
                viewModel.tipoViviendaSeleccionada(seleccion.id)
            }
        }
        binding.spPaisResidencia.adapter = adapterPaises
        binding.spPaisResidencia.dropDownWidth = 960
        binding.spPaisResidencia.dropDownHorizontalOffset = -30
        binding.spPaisResidencia.setOnTouchListener(touchListenerS)
        binding.spPaisResidencia.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seleccion = parent?.getItemAtPosition(position) as ItemSpinner
                viewModel.paisSeleccionado(seleccion.id)
                viewModel.obtenerDeptosApi()
            }
        }
        binding.spDepartamentoResidencia.adapter = adapterDeptos
        binding.spDepartamentoResidencia.dropDownWidth = 960
        binding.spDepartamentoResidencia.dropDownHorizontalOffset = -30
        binding.spPaisResidencia.setOnTouchListener(touchListenerS)
        binding.spDepartamentoResidencia.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seleccion = parent?.getItemAtPosition(position) as ItemSpinner
                viewModel.deptoSeleccionado(seleccion.id)
                viewModel.obtenerMunicipiosApi()
                binding.spMunicipioResidencia.setSelection(0)
            }
        }
        binding.spMunicipioResidencia.adapter = adapterMunicipios
        binding.spMunicipioResidencia.dropDownWidth = 960
        binding.spMunicipioResidencia.dropDownHorizontalOffset = -30
        binding.spPaisResidencia.setOnTouchListener(touchListenerS)
        binding.spMunicipioResidencia.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seleccion = parent?.getItemAtPosition(position) as ItemSpinner
                viewModel.municipioSeleccionado(seleccion.id)
            }
        }

        binding.fabDatosBasicos.setOnClickListener { mostrarEdicion(true) }

        binding.btnGuardarDatosBasicos.setOnClickListener {
            cerrarTeclado()

            if (validarAntesdeEnviar()){
                actualizarInfo()
            }else{
                crearSnackbar(false).show()
            }
        }

        return binding.root
    }

    fun bindearFuncionario() {
        viewModel.obtenerFuncionario()
        viewModel.funcionario.observe(this, Observer {
            funcionario = it
            binding.funcionario = funcionario
        })
    }

    private fun cerrarTeclado(){
        val vista =   activity?.currentFocus
        if (vista != null){
            val input  = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            input.hideSoftInputFromWindow(vista.windowToken, 0)
        }
    }

    private fun mostrarEdicion(mostrar: Boolean) {
        when (mostrar) {
            true -> {
                binding.fabDatosBasicos.hide()
                binding.visualizacionDatosBasicos.visibility = View.GONE
                binding.edicionDatosBasicos.visibility = View.VISIBLE

                val handler = Handler()
                handler.postDelayed({ asignarSpinnersEdicion() }, 400)

                binding.msgErrorCorreoPersonal.text = ""
                binding.msgErrorPaisResidencia.text = ""
                binding.msgErrorDepartamentoResidencia.text = ""
                binding.msgErrorMunicipioResidencia.text = ""
            }
            false -> {
                binding.fabDatosBasicos.show()
                binding.visualizacionDatosBasicos.visibility = View.VISIBLE
                binding.edicionDatosBasicos.visibility = View.GONE
            }
        }
    }

    private fun asignarSpinnersEdicion(){
        var posicionItemSpinner = 0
        val handler = Handler()

        posicionItemSpinner = adapterPaises.obtenerPosicionValor(ItemSpinner(funcionario.paisResidenciaId!!.toInt(), funcionario.paisResidenciaNombre!!))
        binding.spPaisResidencia.setSelection(posicionItemSpinner)

        posicionItemSpinner = adapterViviendas.obtenerPosicionValor(ItemSpinner(funcionario.tipoViviendaId!!.toInt(), funcionario.tipoViviendaNombre!!))
        binding.spVivienda.setSelection(posicionItemSpinner)

        posicionItemSpinner = adapterLentes.obtenerPosicionNombre(funcionario.usaLentes!!)
        binding.spLentes.setSelection(posicionItemSpinner)

        handler.postDelayed({
            posicionItemSpinner = adapterDeptos.obtenerPosicionValor(ItemSpinner(funcionario.departamentoResidenciaId!!.toInt(), funcionario.departamentoResidenciaNombre!!))
            binding.spDepartamentoResidencia.setSelection(posicionItemSpinner)
        }, 500)

        handler.postDelayed({
            posicionItemSpinner = adapterMunicipios.obtenerPosicionValor(ItemSpinner(funcionario.municipioResidenciaId!!.toInt(), funcionario.municipioResidenciaNombre!!))
            binding.spMunicipioResidencia.setSelection(posicionItemSpinner)

            binding.btnGuardarDatosBasicos.isEnabled = true
        }, 900)
    }

    //SE PUEDE HACER GENERAL EN OTRA CLASE, O EN VALIDADOR
    private fun toBool(s: String): Boolean {
        return when (s) {
            "Si" -> true
            else -> false
        }
    }

    private fun validarAntesdeEnviar() : Boolean{
        val validador = Validador()
        var retorno = true

        if (!validador.campoDireccion(binding.etDireccion, binding.msgErrorDireccion, true)) retorno = false

        if (!validador.campoTelefono(binding.etTelefonoFijo,binding.msgErrorTelefonoFijo, false)) retorno = false

        if (!validador.campoAlfabetico(binding.etTallaCamisa,binding.msgErrorTallaCamisa, false)) retorno = false

        if (!validador.campoCelular(binding.etCelular,binding.msgErrorCelular, true)) retorno = false

        if (!validador.spinnerRequerido(binding.spVivienda,binding.msgErrorTipoVivienda)) retorno = false

        if (!validador.spinnerRequerido(binding.spLentes,binding.msgErrorTipoVivienda)) retorno = false

        if (!validador.spinnerRequerido(binding.spPaisResidencia,binding.msgErrorPaisResidencia)) retorno = false

        if (!validador.spinnerRequerido(binding.spDepartamentoResidencia,binding.msgErrorDepartamentoResidencia)) retorno = false

        if (!validador.spinnerRequerido(binding.spMunicipioResidencia,binding.msgErrorMunicipioResidencia)) retorno = false

        if (!validador.campoCorreo(binding.etCorreoPersonal,binding.msgErrorCorreoPersonal, false)) retorno = false

        return retorno
    }

    private fun actualizarInfo() {
        binding.btnGuardarDatosBasicos.isEnabled = false
        val id = App.idFuncionario

        val parametros = JSONObject()
        parametros.put("id", id)
        parametros.put("divisionPoliticaNivel2ResidenciaId", viewModel.idMunicipioSeleccionado)
        parametros.put("celular", binding.etCelular.text.toString())
        parametros.put("telefonoFijo", binding.etTelefonoFijo.text.toString())
        parametros.put("tipoViviendaId", viewModel.idViviendaSeleccionada)
        parametros.put("direccion", binding.etDireccion.text.toString())
        parametros.put("tallaCamisa", binding.etTallaCamisa.text.toString())
        parametros.put("tallaPantalon", binding.etTallaPantalon.text.toString())
        parametros.put("numeroCalzado", binding.etNumeroCalzado.text.toString())
        parametros.put("usaLentes", toBool(viewModel.usaLentes!!))
        parametros.put("correoElectronicoPersonal", binding.etCorreoPersonal.text.toString())

        val callbackFuncionario = object : IRespuestaServidor{
            override fun exito(respuesta: Any?) {
                Log.i("parametrosRespo", respuesta.toString())
                crearSnackbar(true).show()
                actualizarFuncionario()

                val handler = Handler()
                val run = Runnable {
                    mostrarEdicion(false)
                }
                handler.postDelayed(run, 3000)
            }

            override fun error(error: VolleyError) {
                crearSnackbar(false).show()
                Log.d("respuestaConError", error.toString())

                binding.btnGuardarDatosBasicos.isEnabled = true

                val errors = String(error.networkResponse.data)

                obtenerMensajesBacked(JSONObject(errors).getJSONObject("errors"))
            }

        }

        editarFuncionario(context!!, callbackFuncionario, parametros, id!!)
        Log.i("parametros", parametros.toString())
    }

    private fun obtenerMensajesBacked(jsonErrors: JSONObject) {
        if (jsonErrors.has("correoElectronicoPersonal")) {
            val errores = jsonErrors.getJSONArray("correoElectronicoPersonal")
            val mensajes = StringBuilder()
            for (i in 0 until errores.length()) {
                mensajes.append(errores[i].toString() + "\n")
            }
            binding.msgErrorCorreoPersonal.text = mensajes.toString()
        }

        if (jsonErrors.has("direccion")){
            val errores = jsonErrors.getJSONArray("direccion")
            val mensajes = StringBuilder()
            for (i in 0 until errores.length()) {
                mensajes.append(errores[i].toString() + "\n")
            }
            binding.msgErrorDireccion.text = mensajes.toString()
        }

        if (jsonErrors.has("celular")){
            val errores = jsonErrors.getJSONArray("celular")
            val mensajes = StringBuilder()
            for (i in 0 until errores.length()) {
                mensajes.append(errores[i].toString() + "\n")
            }
            binding.msgErrorCelular.text = mensajes.toString()
        }
    }

    private fun actualizarFuncionario() {
        llamadoFuncionarioApi(viewModel.obtenerCedulaFuncionario())
    }

    private fun llamadoFuncionarioApi(cedula: String?) {
        val callbackFuncionario = object : IRespuestaServidor{
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val value = json.get("value")

                if (value.toString() != "[]") {
                    val valueObj = json.getJSONArray("value")[0]
                    val datosFuncionario = JSONObject(valueObj.toString())

                    viewModel.actualizarFuncionario(Funcionario(datosFuncionario))

                    bindearFuncionario()
                } else {
                    Log.i("ManuLateRespuestaValue", "Vacio/nullo")
                    //Generar alerta que no hay datos del usuario
                }
            }

            override fun error(error: VolleyError) {
                Log.d("respuestaConError", error.toString())
            }

        }
        obtenerFuncionario(context!!, cedula!!, callbackFuncionario)
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

    /*fun obtenerimagen(adjunto: String) {
        val url = getString(R.string.apiNomina).plus("api/archivos/${adjunto}/Archivo")
        Log.i("FuncionarioImagen1", adjunto)
        val call = object : MicallbackImg {
            override fun call(response: Bitmap?) {
                Log.i("FuncionarioImagen2", response.toString())

                //Guardamos Bitmap de imagen en base Local
                val imgString = bitMapToString(response!!)

                actualizarImagenEnBaseLocal(imgString)
            }
        }

        val error = object : Micallback {
            override fun call(response: Any?) {
                Log.d("respuestaConError", response.toString())
            }
        }

        LoginRequest.getInstance(this.context).img(
            url,
            100,
            100,
            ImageView.ScaleType.FIT_XY,
            Bitmap.Config.RGB_565,
            null,
            call,
            error
        )
    }

    fun actualizarImagenEnBaseLocal(imagen: String) {
        viewModel.atualizarImagenFuncionario(imagen)
    }*/
}