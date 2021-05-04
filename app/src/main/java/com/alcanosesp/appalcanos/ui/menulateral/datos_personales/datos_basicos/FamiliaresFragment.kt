package com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos

import android.os.Bundle
import android.util.Log
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
import com.alcanosesp.appalcanos.databinding.FragmentFamiliaresBinding
import com.alcanosesp.appalcanos.db.entity.Familiar
import com.alcanosesp.appalcanos.utils.estadosInformacion
import com.android.volley.VolleyError
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject

class FamiliaresFragment : Fragment(), AdapterRecyclerView.OnRecyclerClickListener{

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(FamiliaresViewModel::class.java)
    }

    private var vistaAMostrar = "PROGRESO"
    private lateinit var navController : NavController
    private var funcionarioId: String = App.idFuncionario.toString()

    private lateinit var binding: FragmentFamiliaresBinding
    private lateinit var adaptadorRVFamiliares : AdapterRecyclerView

    //Vistas
    private lateinit var progreso: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshRV: SwipeRefreshLayout
    private lateinit var emptyState: LinearLayout
    private lateinit var visualizacion: LinearLayout
    private lateinit var fab: FloatingActionButton

    var lista = ArrayList<Familiar>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.obtenerFamiliares()
        obtenerFamiliaresDB()
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_familiares, container, false)

        recyclerView = binding.ibFamiliaresRv
        refreshRV = binding.refreshFamiliares
        emptyState = binding.ibEmptyFamiliares
        visualizacion = binding.visualizacionFamiliar
        progreso = binding.pbFamiliares
        fab = binding.fabFamiliares

        recyclerView.layoutManager = LinearLayoutManager(activity)
        adaptadorRVFamiliares = AdapterRecyclerView(context, this)
        recyclerView.adapter = adaptadorRVFamiliares

        mostrarVista(vistaAMostrar)

        fab.setOnClickListener {
            navegarA()
        }

        refreshRV.setOnRefreshListener {
            mostrarVista("PROGRESO")
            obtenerFamiliaresApi()
            refreshRV.isRefreshing = false
        }

        return binding.root
    }

    private fun obtenerFamiliaresDB(){
        viewModel.familiares.observe(this, Observer {
            val familiares = it
            if (familiares.isEmpty()) {
                obtenerFamiliaresApi()
            } else {
                lista.clear()

                for (element in familiares) {
                    lista.add(element)
                }

                vistaAMostrar = "LISTA"
                mostrarVista(vistaAMostrar)
            }
        })
    }

    private fun obtenerFamiliaresApi(){
        lista.clear()
        adaptadorRVFamiliares.notifyDataSetChanged()
        viewModel.eliminarFamiliares()
        val callbackFamiliares = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                Log.i("KOSA", respuesta.toString())
                vistaAMostrar = if (valueArr.length() != 0) {
                    for (i in 0 until valueArr.length()) {
                        val item = valueArr.getJSONObject(i)
                        val familiar = Familiar(item)

                        viewModel.insertarFamiliar(familiar)
                        lista.add(familiar)
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
        obtenerFamiliares(context!!, callbackFamiliares, funcionarioId)
    }

    override fun seleccionarItem(item: Any?) {
        binding.familiar = item as Familiar
        App.familiar = item as Familiar

        context?.getColor(estadosInformacion[App.familiar?.estado]!!)
            ?.let { binding.familiarEstado.background.setTint(it) }

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
                adaptadorRVFamiliares.crearListaElementos(lista)
                adaptadorRVFamiliares.notifyDataSetChanged()

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
                App.familiar = null
            }
        }
        navController.navigate(R.id.action_nav_datos_personales_to_familiaresFormularioFragment)
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