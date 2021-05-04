package com.alcanosesp.appalcanos.ui.menulateral.solicitudes.beneficios

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
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
import com.alcanosesp.appalcanos.api.IRespuestaServidor
import com.alcanosesp.appalcanos.api.obtenerSolicitudesBeneficios
import com.alcanosesp.appalcanos.databinding.FragmentBeneficiosBinding
import com.alcanosesp.appalcanos.db.entity.SolicitudBeneficio
import com.android.volley.VolleyError
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject

class BeneficiosFragment : Fragment(), AdapterRecyclerView.OnRecyclerClickListener{

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(BeneficiosViewModel::class.java)
    }

    private var vistaAMostrar = "PROGRESO"
    private lateinit var navController: NavController
    private var funcionarioId: String = App.idFuncionario.toString()

    private lateinit var binding: FragmentBeneficiosBinding
    private lateinit var adaptadorRVBeneficios: AdapterRecyclerView

    //Vistas
    private lateinit var progreso: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshRV: SwipeRefreshLayout
    private lateinit var emptyState: LinearLayout
    private lateinit var fab: FloatingActionButton

    var lista = ArrayList<SolicitudBeneficio>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.obtenerSolicitudes()
        obtenerSolicitudesDB()
        App.solicitudBeneficio = null
        navController = this.findNavController()
    }

    override fun onResume() {
        super.onResume()
        App.solicitudBeneficio = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_beneficios, container, false)

        recyclerView = binding.beneficiosRv
        refreshRV = binding.refreshBeneficios
        emptyState = binding.ibEmptyBeneficios
        progreso = binding.pbBeneficios
        fab = binding.fabSolicitudes

        recyclerView.layoutManager = LinearLayoutManager(activity)
        adaptadorRVBeneficios = AdapterRecyclerView(context, this)
        recyclerView.adapter = adaptadorRVBeneficios

        mostrarVista(vistaAMostrar)

        fab.setOnClickListener {
            navController.navigate(R.id.action_beneficiosFragment_to_beneficioFormularioFragment)
        }

        refreshRV.setOnRefreshListener {
            mostrarVista("PROGRESO")
            obtenerSolicitudesApi()
            refreshRV.isRefreshing = false
        }

        return binding.root
    }

    private fun obtenerSolicitudesDB(){
        viewModel.solicitudesBeneficios.observe(this, Observer {
            val solicitudesBeneficio = it
            if (solicitudesBeneficio.isEmpty()) {
                obtenerSolicitudesApi()
            } else {
                lista.clear()

                for (element in solicitudesBeneficio) {
                    lista.add(element)
                }

                vistaAMostrar = "LISTA"
                mostrarVista(vistaAMostrar)
            }
        })
    }

    private fun obtenerSolicitudesApi(){
        lista.clear()
        adaptadorRVBeneficios.notifyDataSetChanged()
        viewModel.eliminarSolicitudes()

        val callbackSolicitudes = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                vistaAMostrar = if (valueArr.length() != 0) {
                    for (i in 0 until valueArr.length()) {
                        val item = valueArr.getJSONObject(i)
                        val solicitud = SolicitudBeneficio(item)

                        viewModel.insertarSolicitud(solicitud)
                        lista.add(solicitud)
                    }
                    "LISTA"
                }else {
                    "EMPTY"
                }
                mostrarVista(vistaAMostrar)
            }

            override fun error(error: VolleyError) {
                //DIALOGO DE ERROR O ALGO
            }
        }
        obtenerSolicitudesBeneficios(context!!, callbackSolicitudes, funcionarioId)
    }

    private fun mostrarVista(vista: String){
        when (vista) {
            "PROGRESO" -> {
                progreso.visibility = View.VISIBLE
                emptyState.visibility = View.GONE
                refreshRV.visibility = View.GONE
            }
            "EMPTY" -> {
                emptyState.visibility = View.VISIBLE
                refreshRV.visibility = View.GONE
                progreso.visibility = View.GONE
            }
            "LISTA" -> {
                adaptadorRVBeneficios.crearListaElementos(lista)
                adaptadorRVBeneficios.notifyDataSetChanged()

                refreshRV.visibility = View.VISIBLE
                emptyState.visibility = View.GONE
                progreso.visibility = View.GONE
            }
        }
        navegacionFAB(vista)
    }

    //Podria ser general
    private fun navegacionFAB(estado: String){
        when(estado){
            "PROGRESO" -> {
                fab.hide()
            }
            "LISTA", "EMPTY" -> {
                fab.show()
                fab.setImageResource(R.drawable.ic_add)
            }
        }
    }

    override fun seleccionarItem(item: Any?) {
        App.solicitudBeneficio = item as SolicitudBeneficio

        navController.navigate(R.id.action_beneficiosFragment_to_beneficioVisualizarFragment)
    }
}