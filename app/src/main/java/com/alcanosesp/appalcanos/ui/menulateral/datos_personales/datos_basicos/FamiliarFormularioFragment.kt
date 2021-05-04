package com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos
//507 - 475
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
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
import com.alcanosesp.appalcanos.api.editarFamiliar
import com.alcanosesp.appalcanos.api.obtenerFamiliares
import com.alcanosesp.appalcanos.api.registrarFamiliar
import com.alcanosesp.appalcanos.databinding.FragmentFamiliarFormularioBinding
import com.alcanosesp.appalcanos.db.entity.Familiar
import com.alcanosesp.appalcanos.model.ItemSpinner
import com.alcanosesp.appalcanos.utils.Validador
import com.android.volley.VolleyError
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

class FamiliarFormularioFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(FamiliaresViewModel::class.java)
    }
    private var familiar: Familiar? = App.familiar
    private var id: Int? = null

    private lateinit var binding: FragmentFamiliarFormularioBinding

    //SE PUEDE HACER GENERAL CON VIEW MODEL Y OTRA CLASE
    private val dependiente: ArrayList<ItemSpinner> = ArrayList()

    private lateinit var adapterSexos: SpinnerAdapter
    private lateinit var adapterPaises: SpinnerAdapter
    private lateinit var adapterMunicipios: SpinnerAdapter
    private lateinit var adapterParentescos: SpinnerAdapter
    private lateinit var adapterDependiente: SpinnerAdapter
    private lateinit var adapterDepartamentos: SpinnerAdapter
    private lateinit var adapterTiposDocumento: SpinnerAdapter
    private lateinit var adapterNivelesEducativos: SpinnerAdapter

    @SuppressLint("ClickableViewAccessibility")
    private val touchListenerS = OnTouchListener { _, _ ->
        cerrarTeclado()
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (familiar == null){
            (activity as AppCompatActivity?)!!.supportActionBar?.title = "Crear familiar"
        }else {
            (activity as AppCompatActivity?)!!.supportActionBar?.title = "Editar familiar"
            id = familiar?.id
        }

        adapterSexos = SpinnerAdapter(context!!)
        adapterPaises = SpinnerAdapter(context!!)
        adapterMunicipios = SpinnerAdapter(context!!)
        adapterParentescos = SpinnerAdapter(context!!)
        adapterDependiente = SpinnerAdapter(context!!)
        adapterDepartamentos = SpinnerAdapter(context!!)
        adapterTiposDocumento = SpinnerAdapter(context!!)
        adapterNivelesEducativos = SpinnerAdapter(context!!)

        //SE PUEDE HACER GENERAL CON VIEW MODEL Y OTRA CLASE
        dependiente.add(ItemSpinner(1,"Si"))
        dependiente.add(ItemSpinner(2,"No"))

        viewModel.listaSexos.observe(this, Observer { this.adapterSexos.setItems(it) })
        viewModel.listaPaises.observe(this, Observer { this.adapterPaises.setItems(it) })
        viewModel.listaMunicipios.observe(this, Observer { this.adapterMunicipios.setItems(it) })
        viewModel.listaParentescos.observe(this, Observer { this.adapterParentescos.setItems(it) })
        viewModel.listaDepartamentos.observe(this, Observer { this.adapterDepartamentos.setItems(it) })
        viewModel.listaTiposDocumento.observe(this, Observer { this.adapterTiposDocumento.setItems(it) })
        viewModel.listaNivelesEducativos.observe(this, Observer { this.adapterNivelesEducativos.setItems(it) })
        //SE PUEDE HACER GENERAL CON VIEW MODEL Y OTRA CLASE
        adapterDependiente.setItems(dependiente)

        viewModel.obtenerSexosApi()
        viewModel.obtenerPaisesApi()
        viewModel.obtenerParentescosApi()
        viewModel.obtenerTiposDocumentoApi()
        viewModel.obtenerNivelesEducativosApi()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_familiar_formulario, container, false)

        binding.familiar = familiar

        binding.btnGuardarFamiliar.setOnClickListener {
            crearFamiliar()
        }

        binding.etFamiliarFechaNacimiento.setOnClickListener {
            mostrarDateDialog(it as EditText)
        }

        binding.spFamiliarSexo.adapter = adapterSexos
        binding.spFamiliarSexo.setOnTouchListener(touchListenerS)
        binding.spFamiliarSexo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seleccion = parent?.getItemAtPosition(position) as ItemSpinner
                viewModel.sexoSeleccionado(seleccion.id)
            }
        }
        binding.spFamiliarPais.adapter = adapterPaises
        binding.spFamiliarPais.dropDownWidth = 960
        binding.spFamiliarPais.dropDownHorizontalOffset = -30
        binding.spFamiliarPais.setOnTouchListener(touchListenerS)
        binding.spFamiliarPais.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seleccion = parent?.getItemAtPosition(position) as ItemSpinner
                viewModel.paisSeleccionado(seleccion.id)
                viewModel.obtenerDeptosApi()
            }
        }
        binding.spFamiliarMpio.adapter = adapterMunicipios
        binding.spFamiliarMpio.dropDownWidth = 960
        binding.spFamiliarMpio.dropDownHorizontalOffset = -30
        binding.spFamiliarMpio.setOnTouchListener(touchListenerS)
        binding.spFamiliarMpio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seleccion = parent?.getItemAtPosition(position) as ItemSpinner
                viewModel.municipioSeleccionado(seleccion.id)
            }
        }
        binding.spFamiliarParentesco.adapter = adapterParentescos
        binding.spFamiliarParentesco.setOnTouchListener(touchListenerS)
        binding.spFamiliarParentesco.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seleccion = parent?.getItemAtPosition(position) as ItemSpinner
                viewModel.parentescoSeleccionado(seleccion.id)
            }
        }
        binding.spFamiliarDependiente.adapter = adapterDependiente
        binding.spFamiliarDependiente.setOnTouchListener(touchListenerS)
        binding.spFamiliarDependiente.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seleccion = parent?.getItemAtPosition(position) as ItemSpinner
                viewModel.dependienteSeleccion(seleccion.nombre)
            }
        }
        binding.spFamiliarDepto.adapter = adapterDepartamentos
        binding.spFamiliarDepto.dropDownWidth = 960
        binding.spFamiliarDepto.dropDownHorizontalOffset = -30
        binding.spFamiliarDepto.setOnTouchListener(touchListenerS)
        binding.spFamiliarDepto.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seleccion = parent?.getItemAtPosition(position) as ItemSpinner
                viewModel.deptoSeleccionado(seleccion.id)
                viewModel.obtenerMunicipiosApi()
                binding.spFamiliarMpio.setSelection(0)

            }
        }
        binding.spFamiliarTipoDocumento.adapter = adapterTiposDocumento
        binding.spFamiliarTipoDocumento.setOnTouchListener(touchListenerS)
        binding.spFamiliarTipoDocumento.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seleccion = parent?.getItemAtPosition(position) as ItemSpinner
                viewModel.tipoDocumentoSeleccionado(seleccion.id)
            }
        }
        binding.etFamiliarNivelEducativo.adapter = adapterNivelesEducativos
        binding.etFamiliarNivelEducativo.setOnTouchListener(touchListenerS)
        binding.etFamiliarNivelEducativo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seleccion = parent?.getItemAtPosition(position) as ItemSpinner
                viewModel.nivelEducativoSeleccionado(seleccion.id)
            }
        }

        //Seteo la info en los spinners cuando hay edicion
        //OCULTAR FORMULARIO MIENTRAS CARGA POSIBLEMENTE
        if (familiar != null){
            val handler = Handler()
            handler.postDelayed({ asignarSpinnersEdicion() }, 500)
        }

        return binding.root
    }

    private fun asignarSpinnersEdicion(){
        var posicionItemSpinner = 0
        val handler = Handler()

        posicionItemSpinner = adapterSexos.obtenerPosicionValor(ItemSpinner(familiar?.sexoId!!.toInt(), familiar?.sexoNombre!!))
        binding.spFamiliarSexo.setSelection(posicionItemSpinner)

        posicionItemSpinner = adapterParentescos.obtenerPosicionValor(ItemSpinner(familiar?.parentescoId!!.toInt(), familiar?.parentescoNombre!!))
        binding.spFamiliarParentesco.setSelection(posicionItemSpinner)

        posicionItemSpinner = adapterDependiente.obtenerPosicionNombre(familiar?.dependiente!!)
        binding.spFamiliarDependiente.setSelection(posicionItemSpinner)

        posicionItemSpinner = adapterTiposDocumento.obtenerPosicionValor(ItemSpinner(familiar?.tipoDocumentoId!!.toInt(), familiar?.tipoDocumentoNombre!!))
        binding.spFamiliarTipoDocumento.setSelection(posicionItemSpinner)

        posicionItemSpinner = adapterNivelesEducativos.obtenerPosicionValor(ItemSpinner(familiar?.nivelEducativoId!!.toInt(), familiar?.nivelEducativoNombre!!))
        binding.etFamiliarNivelEducativo.setSelection(posicionItemSpinner)

        posicionItemSpinner = adapterPaises.obtenerPosicionValor(ItemSpinner(familiar?.paisId!!.toInt(), familiar?.paisNombre!!))
        binding.spFamiliarPais.setSelection(posicionItemSpinner)

        handler.postDelayed({
            posicionItemSpinner = adapterDepartamentos.obtenerPosicionValor(ItemSpinner(familiar?.departamentoId!!.toInt(), familiar?.departamentoNombre!!))
            binding.spFamiliarDepto.setSelection(posicionItemSpinner)
        }, 400)

        handler.postDelayed({
            posicionItemSpinner = adapterMunicipios.obtenerPosicionValor(ItemSpinner(familiar?.municipioId!!.toInt(), familiar?.municipioNombre!!))
            binding.spFamiliarMpio.setSelection(posicionItemSpinner)
        }, 800)
    }

    private fun crearFamiliar(){
        cerrarTeclado()
        if (validarAntesdeEnviar()){
            enviarFamiliar()
        }else{
            crearSnackbar(false).show()
        }
    }

    private fun enviarFamiliar() {
        binding.btnGuardarFamiliar.isEnabled = false

        val parametros = JSONObject()
        parametros.put("funcionarioId", App.idFuncionario)
        parametros.put("primerNombre", binding.etFamiliarPrimerNombre.text.toString())
        parametros.put("segundoNombre", binding.etFamiliarSegundoNombre.text.toString())
        parametros.put("primerApellido", binding.etFamiliarPrimerApellido.text.toString())
        parametros.put("segundoApellido", binding.etFamiliarSegundoApellido.text.toString())
        parametros.put("sexoId", viewModel.idSexoSeleccionado)
        parametros.put("parentescoId", viewModel.idParentescoSeleccionado)
        parametros.put("dependiente", toBool(viewModel.dependiente!!))
        parametros.put("tipoDocumentoId", viewModel.idTipoDocumentoSeleccionado)
        parametros.put("numeroDocumento", binding.etFamiliarNDocumento.text.toString())
        parametros.put("nivelEducativoId", viewModel.idNivelEducativoSeleccionado)
        parametros.put("fechaNacimiento", binding.etFamiliarFechaNacimiento.text.toString())
        parametros.put("divisionPoliticaNivel2Id", viewModel.idMunicipioSeleccionado)
        parametros.put("celular", binding.etFamiliarCelular.text.toString())
        parametros.put("telefonoFijo", binding.etFamiliarTelefonoFijo.text.toString())
        parametros.put("direccion", binding.etFamiliarDireccion.text.toString())

        val callbackFamiliar = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                crearSnackbar(true).show()
                recargarFamiliares()
            }

            override fun error(error: VolleyError) {
                binding.btnGuardarFamiliar.isEnabled = true

                val errors = String(error.networkResponse.data)
                crearSnackbar(false).show()

                obtenerMensajesBacked(JSONObject(errors).getJSONObject("errors"))
            }
        }

        if (familiar == null){
            registrarFamiliar(context!!, callbackFamiliar, parametros)
        }else{
            parametros.put("id", id)
            editarFamiliar(context!!, callbackFamiliar, parametros, id!!)
        }
    }

    private fun obtenerMensajesBacked(jsonErrors: JSONObject) {
        if (jsonErrors.has("numeroDocumento")) {
            val errores = jsonErrors.getJSONArray("numeroDocumento")
            val mensajes = StringBuilder()
            for (i in 0 until errores.length()) {
                mensajes.append(errores[i].toString() + "\n")
            }
            binding.msgErrorFamiliarNDocumento.text = mensajes.toString()
        }
        if (jsonErrors.has("parentescoId")) {
            val errores = jsonErrors.getJSONArray("parentescoId")
            val mensajes = StringBuilder()
            for (i in 0 until errores.length()) {
                mensajes.append(errores[i].toString() + "\n")
            }
            binding.msgErrorFamiliarParentesco.text = mensajes.toString()
        }
        if (jsonErrors.has("fechaNacimiento")) {
            val errores = jsonErrors.getJSONArray("fechaNacimiento")
            val mensajes = StringBuilder()
            for (i in 0 until errores.length()) {
                mensajes.append(errores[i].toString() + "\n")
            }
            binding.msgErrorFamiliarFechaNacimiento.text = mensajes.toString()
        }
        if (jsonErrors.has("divisionPoliticaNivel2Id")) {
            val errores = jsonErrors.getJSONArray("divisionPoliticaNivel2Id")
            val mensajes = StringBuilder()
            for (i in 0 until errores.length()) {
                mensajes.append(errores[i].toString() + "\n")
            }
            binding.msgErrorFamiliarMpioResidencia.text = mensajes.toString()
        }
        if (jsonErrors.has("celular")) {
            val errores = jsonErrors.getJSONArray("celular")
            val mensajes = StringBuilder()
            for (i in 0 until errores.length()) {
                mensajes.append(errores[i].toString() + "\n")
            }
            binding.msgErrorFamiliarCelular.text = mensajes.toString()
        }
    }

    private fun recargarFamiliares(){
        val callbackFamiliares = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                viewModel.eliminarFamiliares()

                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    val familiar = Familiar(item)

                    viewModel.insertarFamiliar(familiar)
                }
                findNavController().navigate(R.id.action_familiaresFormularioFragment_to_nav_datos_personales)
            }

            override fun error(error: VolleyError) {
            }
        }
        obtenerFamiliares(context!!, callbackFamiliares, App.idFuncionario.toString())
    }

    private fun toBool(s: String): Boolean {
        return when (s) {
            "Si" -> true
            else -> false
        }
    }

    private fun validarAntesdeEnviar() : Boolean{
        val validador = Validador()
        var retorno = true

        if(!validador.campoAlfabetico(binding.etFamiliarPrimerNombre, binding.msgErrorFamiliarPrimerNombre, true)) retorno = false

        if(!validador.campoAlfabetico(binding.etFamiliarSegundoNombre, binding.msgErrorFamiliarSegundoNombre, false)) retorno = false

        if(!validador.campoAlfabetico(binding.etFamiliarPrimerApellido, binding.msgErrorFamiliarPrimerApellido, true)) retorno = false

        if(!validador.campoAlfabetico(binding.etFamiliarSegundoApellido, binding.msgErrorFamiliarSegundoApellido, false)) retorno = false

        if(!validador.spinnerRequerido(binding.spFamiliarSexo, binding.msgErrorFamiliarSexo)) retorno = false

        if(!validador.spinnerRequerido(binding.spFamiliarParentesco, binding.msgErrorFamiliarParentesco)) retorno = false

        if(!validador.spinnerRequerido(binding.spFamiliarDependiente, binding.msgErrorFamiliarDependiente)) retorno = false

        if(!validador.spinnerRequerido(binding.spFamiliarTipoDocumento, binding.msgErrorFamiliarTipoDocumento)) retorno = false

        if(!validador.campoAlfanumerico(binding.etFamiliarNDocumento, binding.msgErrorFamiliarNDocumento, true)) retorno = false

        if(!validador.spinnerRequerido(binding.etFamiliarNivelEducativo, binding.msgErrorFamiliarNivelEducativo)) retorno = false

        if(!validador.campoRequerido(binding.etFamiliarFechaNacimiento, binding.msgErrorFamiliarFechaNacimiento)) retorno = false

        if(!validador.spinnerRequerido(binding.spFamiliarPais, binding.msgErrorFamiliarPaisResidencia)) retorno = false

        if(!validador.spinnerRequerido(binding.spFamiliarDepto, binding.msgErrorFamiliarDeptoResidencia)) retorno = false

        if(!validador.spinnerRequerido(binding.spFamiliarMpio, binding.msgErrorFamiliarMpioResidencia)) retorno = false

        if(!validador.campoCelular(binding.etFamiliarCelular, binding.msgErrorFamiliarCelular, true)) retorno = false

        if(!validador.campoTelefono(binding.etFamiliarTelefonoFijo, binding.msgErrorFamiliarTelefonoFijo, false)) retorno = false

        if(!validador.campoDireccion(binding.etFamiliarDireccion, binding.msgErrorFamiliarDireccion, true)) retorno = false

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
        return Snackbar.make(requireView(), "Snackbar", 2600).apply {

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