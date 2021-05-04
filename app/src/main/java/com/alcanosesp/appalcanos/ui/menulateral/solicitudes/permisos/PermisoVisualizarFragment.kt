package com.alcanosesp.appalcanos.ui.menulateral.solicitudes.permisos

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alcanosesp.appalcanos.App
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.adapter.AdapterRVSoportePermiso
import com.alcanosesp.appalcanos.api.IRespuestaServidor
import com.alcanosesp.appalcanos.api.cancelarSolicitudPermiso
import com.alcanosesp.appalcanos.api.obtenerSolicitudPermiso
import com.alcanosesp.appalcanos.databinding.FragmentPermisoVisualizarBinding
import com.alcanosesp.appalcanos.db.entity.SolicitudPermiso
import com.alcanosesp.appalcanos.db.entity.SolicitudPermisoSoporte
import com.alcanosesp.appalcanos.utils.construirAlerta
import com.alcanosesp.appalcanos.utils.estadosPermisos
import com.android.volley.VolleyError
import org.json.JSONObject
import kotlin.math.abs


class PermisoVisualizarFragment : Fragment(), AdapterRVSoportePermiso.OnSoportePermisosListener {

    private val vmPermiso by lazy {
        ViewModelProviders.of(this).get(PermisoViewModel::class.java)
    }
    private lateinit var binding: FragmentPermisoVisualizarBinding
    private val solicituPermiso = App.solicitudPermiso
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterRVSoportePermiso: AdapterRVSoportePermiso
    private var listaSoporte = ArrayList<SolicitudPermisoSoporte>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_permiso_visualizar,
            container,
            false
        )

        binding.fabAprobar.setImageResource(R.drawable.ic_edit)
        binding.fabCancelar.setImageResource(R.drawable.ic_block)
        recyclerView = binding.rvSoportePermiso
        binding.solicitudpermiso = solicituPermiso

        context?.getColor(estadosPermisos[solicituPermiso?.estado]!!)?.let {
            binding.permisoEstado.background.setTint(it)
        }


        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapterRVSoportePermiso = AdapterRVSoportePermiso(context!!, this)
        recyclerView.adapter = adapterRVSoportePermiso

        mostrarVista()
        binding.fabCancelar.setOnClickListener {
            mostarDialogoCancelar()
        }

        binding.fabAprobar.setOnClickListener {
            findNavController().navigate(R.id.action_permisoVisualizarFragment_to_permisoFormularioFragment)
        }

        vmPermiso.obtenerSoporteSolicitudPermisoByPermisoId(solicituPermiso?.id!!)
        vmPermiso.soportePermiso.observe(this, Observer {
            Log.i("obser", "obser")
            adapterRVSoportePermiso.notifyDataSetChanged()
            listaSoporte.clear()
            val soporte = it
            var numeroLineas  = 0
            if (soporte.isNotEmpty()) {
                binding.tvNohaySoportePermisoFormulario.isVisible = false
                for (i in soporte) {
                    var linea = 0
                    if (i.comentario.length > 100) {
                        linea = abs(i.comentario.length / 50) - 1
                    }
                    numeroLineas += linea

                    listaSoporte.add(i)
                    adapterRVSoportePermiso.crearListaElementos(listaSoporte)
                }
            }
            adapterRVSoportePermiso.notifyDataSetChanged()
        })



        return binding.root
    }

    private fun mostrarVista(vista: String? = null) {
        when (solicituPermiso?.estado) {
            "Solicitada" -> {
                binding.fabAprobar.show()
                binding.fabCancelar.show()

                //binding.tvSoportePermiso.setOnClickListener {

                //}
            }
        }

        if (vista == "Cargando") {
            binding.pbSolicitudPermiso.isVisible = true
            binding.svSolicitudPermiso.isVisible = false
            binding.fabAprobar.hide()
            binding.fabCancelar.hide()
        } else {
            binding.pbSolicitudPermiso.isVisible = false
            binding.svSolicitudPermiso.isVisible = true
        }
    }

    private fun mostarDialogoCancelar() {
        val vista = LayoutInflater.from(context).inflate(R.layout.dialogo_botones, null)
        val textView = vista.findViewById<TextView>(R.id.texto_dialog)
        textView.text = getString(R.string.pregunta_anular_solicitud)

        val botonAceptar = vista.findViewById<Button>(R.id.boton_dialog)
        val botonCancelar = vista.findViewById<Button>(R.id.boton_dialog_cancel)

        val builder = AlertDialog.Builder(context)
        builder.apply {
            setView(vista)
            create()
        }
        val dialogo = builder.show()
        botonAceptar.setOnClickListener {
            dialogo.dismiss()
            mostrarVista("Cargando")
            cancelarSolicitudPermisoApi()
        }

        botonCancelar.setOnClickListener {
            dialogo.dismiss()
        }
    }

    private fun cancelarSolicitudPermisoApi() {
        val parametros = JSONObject()
        parametros.put("id", solicituPermiso?.id)
        parametros.put("estado", "Cancelada")

        val callbackCancelarSolicitudPermiso = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                recargarSolicitudesPermiso()
            }

            override fun error(error: VolleyError) {
                mostrarVista()
                val codigo = error.networkResponse?.statusCode
                construirAlerta(
                    context!!,
                    R.layout.toas_login_warning,
                    getString(R.string.mensaje_eror_404, codigo.toString())
                )
            }
        }
        cancelarSolicitudPermiso(
            context!!,
            solicituPermiso?.id!!,
            parametros,
            callbackCancelarSolicitudPermiso
        )

    }

    private fun recargarSolicitudesPermiso() {
        vmPermiso.eliminarSolicitudPermiso()

        val callbackSolicitudPermiso = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    val solicitud = SolicitudPermiso(item, (i + 1).toString())

                    vmPermiso.insertarSolicitudPermiso(solicitud)
                }

                val handler = Handler()

                handler.postDelayed(
                    {
                        findNavController().navigate(R.id.action_permisoVisualizarFragment_to_permisoFragment)
                        App.experiencia = null
                    },
                    700
                )
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
        obtenerSolicitudPermiso(context!!, callbackSolicitudPermiso, App.idFuncionario.toString())
    }

    override fun seleccionarSoportePermiso(soportePermiso: SolicitudPermisoSoporte?) {
        val url =
            getString(R.string.apiNomina).plus("api/archivos/${soportePermiso?.adjunto}/Archivo")
        val pilaIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(pilaIntent)
    }
}
