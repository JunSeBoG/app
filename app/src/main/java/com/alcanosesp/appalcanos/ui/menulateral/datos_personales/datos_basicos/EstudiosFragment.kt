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
import com.alcanosesp.appalcanos.databinding.FragmentEstudiosBinding
import com.alcanosesp.appalcanos.db.entity.Estudio
import com.alcanosesp.appalcanos.utils.estadosInformacion
import com.android.volley.VolleyError
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject

class EstudiosFragment : Fragment(), AdapterRecyclerView.OnRecyclerClickListener{

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(EstudiosViewModel::class.java)
    }

    private var vistaAMostrar = "PROGRESO"
    private lateinit var navController : NavController
    private var funcionarioId: String = App.idFuncionario.toString()

    private lateinit var binding: FragmentEstudiosBinding
    private lateinit var adaptadorRVEstudios: AdapterRecyclerView

    //Vistas
    private lateinit var progreso: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshRV: SwipeRefreshLayout
    private lateinit var emptyState: LinearLayout
    private lateinit var visualizacion: LinearLayout
    private lateinit var fab: FloatingActionButton

    var lista = ArrayList<Estudio>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.obtenerEstudios()
        obtenerEstudiosDB()
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_estudios, container, false)

        recyclerView = binding.ibEstudiosRv
        refreshRV = binding.refreshEstudios
        emptyState = binding.ibEmptyEstudios
        visualizacion = binding.visualizacionEstudio
        progreso = binding.pbEstudios
        fab = binding.fabEstudio

        recyclerView.layoutManager = LinearLayoutManager(activity)
        adaptadorRVEstudios = AdapterRecyclerView(context, this)
        recyclerView.adapter = adaptadorRVEstudios

        mostrarVista(vistaAMostrar)

        fab.setOnClickListener {
            navegarA()
        }

        refreshRV.setOnRefreshListener {
            mostrarVista("PROGRESO")
            obtenerEstudiosApi()
            refreshRV.isRefreshing = false
        }

        return binding.root
    }

    private fun obtenerEstudiosDB(){
        viewModel.estudios.observe(this, Observer {
            val estudios = it
            if (estudios.isEmpty()) {
                obtenerEstudiosApi()
            } else {
                lista.clear()

                for (element in estudios) {
                    lista.add(element)
                }

                vistaAMostrar = "LISTA"
                mostrarVista(vistaAMostrar)
            }
        })
    }

    private fun obtenerEstudiosApi(){
        lista.clear()
        adaptadorRVEstudios.notifyDataSetChanged()
        viewModel.eliminarEstudios()
        val callbackEstudios = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                vistaAMostrar = if (valueArr.length() != 0) {
                    for (i in 0 until valueArr.length()) {
                        val item = valueArr.getJSONObject(i)
                        val estudio = Estudio(item)

                        viewModel.insertarEstudio(estudio)
                        lista.add(estudio)
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
        obtenerEstudios(context!!, callbackEstudios, funcionarioId)
    }

    override fun seleccionarItem(item: Any?) {
        binding.estudio = item as Estudio
        App.estudio = item as Estudio

        context?.getColor(estadosInformacion[App.estudio?.estado]!!)
            ?.let { binding.estudioEstado.background.setTint(it) }


        vistaAMostrar = "VISUALIZACION"
        mostrarVista(vistaAMostrar)
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
                adaptadorRVEstudios.crearListaElementos(lista)
                adaptadorRVEstudios.notifyDataSetChanged()

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
                App.estudio = null
            }
        }
        navController.navigate(R.id.action_nav_datos_personales_to_estudioFormularioFragment)
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