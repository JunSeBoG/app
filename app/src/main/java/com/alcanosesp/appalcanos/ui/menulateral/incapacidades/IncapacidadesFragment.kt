package com.alcanosesp.appalcanos.ui.menulateral.incapacidades

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alcanosesp.appalcanos.App
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.adapter.AdapterRVIncapacidad
import com.alcanosesp.appalcanos.api.IRespuestaServidor
import com.alcanosesp.appalcanos.api.cancelarIncapacidad
import com.alcanosesp.appalcanos.api.obtenerIncapaciadProrrogaPorId
import com.alcanosesp.appalcanos.api.obtenerIncapacidad
import com.alcanosesp.appalcanos.databinding.FragmentIncapacidadesBinding
import com.alcanosesp.appalcanos.db.entity.AusentismoFuncionario
import com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos.BasicosViewModel
import com.alcanosesp.appalcanos.utils.JSONValidador
import com.alcanosesp.appalcanos.utils.estadosAusentismoFuncionario
import com.android.volley.VolleyError
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject

class IncapacidadesFragment : Fragment(), AdapterRVIncapacidad.OnIncapacidadListener {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(IncapacidadViewModel::class.java)
    }

    private val viewModelF by lazy {
        ViewModelProviders.of(this).get(BasicosViewModel::class.java)
    }

    private var vistaAMostrar = "PROGRESO"
    private lateinit var navController: NavController
    private var funcionarioId: String = App.idFuncionario.toString()

    private lateinit var binding: FragmentIncapacidadesBinding
    private lateinit var adaptadorRVIncapacidad: AdapterRVIncapacidad

    //Vistas
    private lateinit var progreso: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var visualizacion: LinearLayout
    private lateinit var fab: FloatingActionButton
    private lateinit var fabCancelar: FloatingActionButton
    private lateinit var refreshRV: SwipeRefreshLayout

    private var lista = ArrayList<AusentismoFuncionario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.obtenerIncapacidad()
        obtenerIncapacidadDB()
        navController = this.findNavController()
    }

    override fun onResume() {
        super.onResume()
        vistaAMostrar = if (vistaAMostrar == "EMPTY") {
            "EMPTY"
        } else {
            "LISTA"
        }

        mostrarVista(vistaAMostrar)
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_incapacidades, container, false)
        recyclerView = binding.ibIncapacidadRv
        emptyState = binding.ibEmptyIncapacidad
        visualizacion = binding.visualizacionIncapacidad
        progreso = binding.pbIncapacidads
        fab = binding.fabAprobar
        fabCancelar = binding.fabCancelar
        refreshRV = binding.refreshIncapaciad

        recyclerView.layoutManager = LinearLayoutManager(activity)
        adaptadorRVIncapacidad = AdapterRVIncapacidad(context, this)
        recyclerView.adapter = adaptadorRVIncapacidad

        mostrarVista(vistaAMostrar)

        fab.setOnClickListener {
            navegarA()
        }

        fabCancelar.setOnClickListener {
            val vista = LayoutInflater.from(context).inflate(R.layout.toas_login_warning, null)
            val textView = vista.findViewById<TextView>(R.id.texto_dialog)
            textView.text = getText(R.string.pregunta_cancelar_incapacidad)

            val botonAceptar = vista.findViewById<Button>(R.id.boton_dialog)

            val builder = AlertDialog.Builder(context)
            builder.apply {
                setView(vista)
                create()
            }
            val dialogo = builder.show()
            botonAceptar.setOnClickListener {
                dialogo.dismiss()
                cancelarIncapacidadApi()
            }
        }

        refreshRV.setOnRefreshListener {
            mostrarVista("PROGRESO")
            obtenerIncapacidadApi()
            refreshRV.isRefreshing = false
        }




        return binding.root
    }

    override fun seleccionarIncapacidad(incapacidad: AusentismoFuncionario?) {
        binding.incapacidad = incapacidad
        App.incapacidad = incapacidad

        if (App.incapacidad?.prorrogaDe.isNullOrEmpty()) {
            binding.llProrrogaDe.isVisible = false
        }

        if (App.incapacidad?.justificacion.isNullOrEmpty()) {
            binding.llJustificacion.isVisible = false
        }

        binding.tvSoporteIncapacidad.setOnClickListener {
            val url =
                getString(R.string.apiNomina).plus("api/archivos/${App.incapacidad?.adjunto}/Archivo")
            val pilaIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(pilaIntent)
        }
        context?.getColor(estadosAusentismoFuncionario[incapacidad?.estado]!!)
            ?.let { binding.incapacidadEstado.background.setTint(it) }

        vistaAMostrar = "VISUALIZACION"
        mostrarVista("VISUALIZACION")
    }

    private fun obtenerIncapacidadDB() {
        viewModelF.obtenerFuncionario()
        viewModel.incapacidad.observe(this, Observer {
            val incapacidad = it
            if (incapacidad.isEmpty()) {
                viewModelF.funcionario.observe(this, Observer { funcionario ->
                    funcionarioId = funcionario.id
                    obtenerIncapacidadApi()
                })
            } else {
                lista.clear()
                for (element in incapacidad) {
                    lista.add(element)
                }

                vistaAMostrar = "LISTA"
                mostrarVista(vistaAMostrar)
            }
        })
    }

    private fun obtenerIncapacidadApi() {
        val callbackIncapacidad = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                viewModel.eliminarIncapacidad()
                lista.clear()
                var filtro = " or ausentismoId eq "
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")
                val numroIncapacidades = valueArr.length() - 1
                vistaAMostrar = if (valueArr.length() != 0) {
                    for (i in 0 until valueArr.length()) {
                        val item = valueArr.getJSONObject(i)
                        val incapacidad = AusentismoFuncionario(item, i.plus(1).toString())
                        viewModel.insertarIncapacidad(incapacidad)
                        lista.add(incapacidad)
                        filtro += " or ausentismoId eq " + item.getString("id")

                        if (numroIncapacidades == i) {
                            obtenerPrrorrogasApi(filtro.replace("or ausentismoId eq  or ", " "))
                        }
                    }
                    "LISTA"
                } else {
                    "EMPTY"
                }
                mostrarVista(vistaAMostrar)
            }

            override fun error(error: VolleyError) {
                Log.i("optenerincapcidadApi", "optener incapcidad Api")
            }
        }
        obtenerIncapacidad(context!!, callbackIncapacidad, funcionarioId)
    }

    private fun mostrarVista(vista: String) {
        when (vista) {
            "PROGRESO" -> {
                progreso.visibility = View.VISIBLE
                emptyState.visibility = View.GONE
                refreshRV.visibility = View.GONE
                visualizacion.visibility = View.GONE
            }
            "EMPTY" -> {
                emptyState.visibility = View.VISIBLE
                refreshRV.visibility = View.GONE
                progreso.visibility = View.GONE
                visualizacion.visibility = View.GONE
            }
            "VISUALIZACION" -> {
                visualizacion.visibility = View.VISIBLE
                emptyState.visibility = View.GONE
                refreshRV.visibility = View.GONE
                progreso.visibility = View.GONE
            }
            "LISTA" -> {
                adaptadorRVIncapacidad.crearListaElementos(lista)
                adaptadorRVIncapacidad.notifyDataSetChanged()

                refreshRV.visibility = View.VISIBLE
                emptyState.visibility = View.GONE
                progreso.visibility = View.GONE
                visualizacion.visibility = View.GONE
            }
        }
        navegacionFAB(vista)
    }

    private fun navegarA() {
        when (vistaAMostrar) {
            "LISTA", "EMPTY" -> {
                App.incapacidad = null
            }
        }
        navController.navigate(R.id.action_nav_incapacidades_to_incapacidadFormularioFragment)
    }

    //Podria ser general
    private fun navegacionFAB(estado: String) {
        when (estado) {
            "PROGRESO" -> {
                fab.hide()
                fabCancelar.hide()
            }
            "VISUALIZACION" -> {
                fab.hide()
                if (App.incapacidad?.estado == "Registrado") {
                    fab.show()
                    fab.setImageResource(R.drawable.ic_edit)
                    fabCancelar.show()
                    fabCancelar.setImageResource(R.drawable.ic_block)
                }
            }
            "LISTA", "EMPTY" -> {
                fab.show()
                fab.setImageResource(R.drawable.ic_add)
                fabCancelar.hide()
            }
        }
    }

    private fun obtenerPrrorrogasApi(filtro: String) {

        val callbackCancelarIncapacidad = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val validador = JSONValidador()
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")
                for (i in 0 until valueArr.length()) {

                    val item = valueArr.getJSONObject(i)
                    val ausentismoId = item.getString("ausentismoId")

                    val codigo = validador.jsonNuloSegundoGrado(
                        item,
                        "prorroga",
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
        obtenerIncapaciadProrrogaPorId(context!!, filtro, callbackCancelarIncapacidad)
    }

    private fun cancelarIncapacidadApi() {
        val parametros = JSONObject().apply {
            put("id", App.incapacidad?.id)
            put("aprobado", false)
            put("justificacion", "Eliminado por el usuario desde el dispositivo movil.")
        }
        val callbackCancelarIncapacidad = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                obtenerIncapacidadApi()
            }


            override fun error(error: VolleyError) {
                Toast.makeText(context, "Intenta de nuevo", Toast.LENGTH_LONG).show()
            }
        }
        cancelarIncapacidad(
            context!!,
            App.incapacidad?.id!!,
            parametros,
            callbackCancelarIncapacidad
        )
    }


}
