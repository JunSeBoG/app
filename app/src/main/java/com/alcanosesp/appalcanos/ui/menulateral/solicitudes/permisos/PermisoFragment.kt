package com.alcanosesp.appalcanos.ui.menulateral.solicitudes.permisos

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
import com.alcanosesp.appalcanos.api.IRespuestaServidor
import com.alcanosesp.appalcanos.api.obtenerSolicitudPermiso
import com.alcanosesp.appalcanos.api.obtenerSoportePermiso
import com.alcanosesp.appalcanos.databinding.FragmentSolicitudPermisoBinding
import com.alcanosesp.appalcanos.db.entity.SolicitudPermiso
import com.alcanosesp.appalcanos.db.entity.SolicitudPermisoSoporte
import com.alcanosesp.appalcanos.utils.construirAlerta
import com.android.volley.VolleyError
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject

class PermisoFragment : Fragment(), AdapterRecyclerView.OnRecyclerClickListener {

    private val vmPermiso by lazy {
        ViewModelProviders.of(this).get(PermisoViewModel::class.java)
    }
    private var vistaAMostrar = "PROGRESO"
    private lateinit var binding: FragmentSolicitudPermisoBinding
    private lateinit var adapterRVPermiso: AdapterRecyclerView
    private lateinit var progreso: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var fab: FloatingActionButton
    private lateinit var refreshRV: SwipeRefreshLayout
    private lateinit var navController: NavController

    private var lista = ArrayList<SolicitudPermiso>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vmPermiso.obtenerSolicitudPermiso()
        obtenerPermisoDB()
        App.solicitudPermiso = null
        navController = this.findNavController()
    }

    override fun onResume() {
        super.onResume()
        App.solicitudPermiso = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_solicitud_permiso, container, false)
        recyclerView = binding.rvPermiso
        emptyState = binding.llEmptyPermiso
        refreshRV = binding.refreshPermiso
        progreso = binding.pbPermiso
        fab = binding.fabPermiso

        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapterRVPermiso = AdapterRecyclerView(context, this)
        recyclerView.adapter = adapterRVPermiso

        mostrarVista(vistaAMostrar)

        fab.setOnClickListener {
            navController.navigate(R.id.action_permisoFragment_to_permisoFormularioFragment)
        }

        refreshRV.setOnRefreshListener {
            mostrarVista("PROGRESO")
            obtenerPermisoApi()
            refreshRV.isRefreshing = false
        }

        return binding.root
    }

    private fun obtenerPermisoDB() {
        vmPermiso.permiso.observe(this, Observer {
            val permiso = it
            if (permiso.isEmpty()) {
                obtenerPermisoApi()
            } else {
                lista.clear()
                for (element in permiso) {
                    lista.add(element)
                }
                vistaAMostrar = "LISTA"
                mostrarVista(vistaAMostrar)
            }
        })
    }

    private fun obtenerPermisoApi() {
        lista.clear()
        adapterRVPermiso.notifyDataSetChanged()
        vmPermiso.eliminarSolicitudPermiso()

        val callbackPermiso = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                vistaAMostrar = if (valueArr.length() != 0) {
                    for (i in 0 until valueArr.length()) {
                        val item = valueArr.getJSONObject(i)
                        val permiso = SolicitudPermiso(item, (i + 1).toString())

                        vmPermiso.insertarSolicitudPermiso(permiso)
                        lista.add(permiso)
                    }
                    "LISTA"
                } else {
                    "EMPTY"
                }
                obtenerSoportePermisoApi()
            }

            override fun error(error: VolleyError) {
                val codigo = error.networkResponse?.statusCode
                construirAlerta(
                    context!!,
                    R.layout.toas_login_warning,
                    getString(R.string.mensaje_eror_404, codigo.toString())
                )
            }
        }
        obtenerSolicitudPermiso(context!!, callbackPermiso, App.idFuncionario.toString())
    }

    fun obtenerSoportePermisoApi() {
        val callbackSoportePermiso = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                vmPermiso.eliminarSoporteSolicitudPermiso()
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    val permiso = SolicitudPermisoSoporte(item, (i + 1).toString())

                    vmPermiso.insertarSoporteSolicitudPermiso(permiso)
                }

                mostrarVista(vistaAMostrar)
            }

            override fun error(error: VolleyError) {
                val codigo = error.networkResponse.statusCode
                construirAlerta(
                    context!!,
                    R.layout.toas_login_warning,
                    getString(R.string.mensaje_eror_404, codigo.toString())
                )
            }
        }
        obtenerSoportePermiso(context!!, callbackSoportePermiso, App.idFuncionario.toString())
    }

    override fun seleccionarItem(item: Any?) {
        App.solicitudPermiso = item as SolicitudPermiso
        navController.navigate(R.id.action_permisoFragment_to_permisoVisualizarFragment)
    }

    private fun mostrarVista(vista: String) {
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
                adapterRVPermiso.crearListaElementos(lista)
                adapterRVPermiso.notifyDataSetChanged()

                refreshRV.visibility = View.VISIBLE
                emptyState.visibility = View.GONE
                progreso.visibility = View.GONE
            }
        }
        navegacionFAB(vista)
    }

    private fun navegacionFAB(estado: String) {
        when (estado) {
            "PROGRESO" -> {
                fab.hide()
            }
            "LISTA", "EMPTY" -> {
                fab.show()
                fab.setImageResource(R.drawable.ic_add)
            }
        }
    }
}