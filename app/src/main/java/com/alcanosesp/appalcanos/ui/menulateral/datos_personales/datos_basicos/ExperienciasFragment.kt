package com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
import com.alcanosesp.appalcanos.adapter.AdapterRecyclerView
import com.alcanosesp.appalcanos.api.*
import com.alcanosesp.appalcanos.databinding.FragmentExperienciasBinding
import com.alcanosesp.appalcanos.db.entity.Experiencia
import com.alcanosesp.appalcanos.utils.estadosInformacion
import com.android.volley.VolleyError
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject

class ExperienciasFragment : Fragment(), AdapterRecyclerView.OnRecyclerClickListener {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(ExperienciasViewModel::class.java)
    }

    private var vistaAMostrar = "PROGRESO"
    private lateinit var navController: NavController
    private var funcionarioId: String = App.idFuncionario.toString()

    private lateinit var binding: FragmentExperienciasBinding
    private lateinit var adaptadorRVExperiencia: AdapterRecyclerView

    //Vistas
    private lateinit var progreso: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshRV: SwipeRefreshLayout
    private lateinit var emptyState: LinearLayout
    private lateinit var visualizacion: LinearLayout
    private lateinit var fab: FloatingActionButton

    var lista = ArrayList<Experiencia>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.obtenerExperiencias()
        obtenerExperienciasDB()
        navController = this.findNavController()
    }

    override fun onResume() {
        super.onResume()

        vistaAMostrar = if(vistaAMostrar == "EMPTY"){
            "EMPTY"
        }else{
            "LISTA"
        }

        mostrarVista(vistaAMostrar)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_experiencias, container, false)

        recyclerView = binding.ibExperienciaRv
        refreshRV = binding.refreshExperiencias
        emptyState = binding.ibEmptyExperiencia
        visualizacion = binding.visualizacionExperiencia
        progreso = binding.pbExperiencias
        fab = binding.fabExperiencia

        recyclerView.layoutManager = LinearLayoutManager(activity)
        adaptadorRVExperiencia = AdapterRecyclerView(context, this)
        recyclerView.adapter = adaptadorRVExperiencia

        mostrarVista(vistaAMostrar)

        fab.setOnClickListener {
            navegarA()
        }

        refreshRV.setOnRefreshListener {
            mostrarVista("PROGRESO")
            obtenerExperienciasApi()
            refreshRV.isRefreshing = false
        }

        return binding.root
    }

    private fun obtenerExperienciasDB(){
        viewModel.experiencias.observe(this, Observer {
            val experiencias = it
            if (experiencias.isEmpty()) {
                obtenerExperienciasApi()
            } else {
                lista.clear()

                for (element in experiencias) {
                    lista.add(element)
                }

                vistaAMostrar = "LISTA"
                mostrarVista(vistaAMostrar)
            }
        })
    }

    private fun obtenerExperienciasApi(){
        lista.clear()
        adaptadorRVExperiencia.notifyDataSetChanged()
        viewModel.eliminarExperiencias()
        val callbackExperiencias = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                vistaAMostrar = if (valueArr.length() != 0) {
                    for (i in 0 until valueArr.length()) {
                        val item = valueArr.getJSONObject(i)
                        val experiencia = Experiencia(item)

                        viewModel.insertarExperiencia(experiencia)
                        lista.add(experiencia)
                    }
                    "LISTA"
                }else {
                    "EMPTY"
                }
                mostrarVista(vistaAMostrar)
            }

            override fun error(error: VolleyError) {
            }
        }
        obtenerExperiencias(context!!, callbackExperiencias, funcionarioId)
    }

    override fun seleccionarItem(item: Any?) {
        binding.experiencia = item as Experiencia
        App.experiencia = item as Experiencia

        context?.getColor(estadosInformacion[App.experiencia?.estado]!!)
            ?.let { binding.experienciaEstado.background.setTint(it) }

        vistaAMostrar = "VISUALIZACION"
        mostrarVista("VISUALIZACION")

    }

    private fun mostrarVista(vista: String){
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
                adaptadorRVExperiencia.crearListaElementos(lista)
                adaptadorRVExperiencia.notifyDataSetChanged()

                refreshRV.visibility = View.VISIBLE
                emptyState.visibility = View.GONE
                progreso.visibility = View.GONE
                visualizacion.visibility = View.GONE
            }
        }
        navegacionFAB(vista)
    }

    private fun navegarA(){
        when(vistaAMostrar){
            "LISTA", "EMPTY" -> {
                App.experiencia = null
            }
        }
        navController.navigate(R.id.action_datos_personales_to_experienciaFormularioFragment)
    }

    //Podria ser general
    private fun navegacionFAB(estado: String){
        when(estado){
            "PROGRESO" -> {
                fab.hide()
            }
            "VISUALIZACION" -> {
                fab.show()
                fab.setImageResource(R.drawable.ic_edit)
            }
            "LISTA", "EMPTY" -> {
                fab.show()
                fab.setImageResource(R.drawable.ic_add)
            }
        }
    }
}