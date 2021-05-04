package com.alcanosesp.appalcanos.ui.menulateral.consultas.desprendibles

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alcanosesp.appalcanos.App

import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.adapter.AdapterRecyclerView
import com.alcanosesp.appalcanos.api.IRespuestaServidor
import com.alcanosesp.appalcanos.api.obtenerDesprendible
import com.alcanosesp.appalcanos.api.obtenerDesprendibles
import com.alcanosesp.appalcanos.databinding.FragmentDesprendiblesBinding
import com.alcanosesp.appalcanos.model.Desprendible
import com.android.volley.VolleyError
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DesprendiblesFragment : Fragment(), AdapterRecyclerView.OnRecyclerClickListener {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(DesprendiblesViewModel::class.java)
    }

    private var vistaAMostrar = "PROGRESO"

    private lateinit var binding: FragmentDesprendiblesBinding
    private lateinit var adaptadorRVDesprendibles: AdapterRecyclerView

    //Vistas
    private lateinit var progreso: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshRV: SwipeRefreshLayout
    private lateinit var emptyState: LinearLayout

    var lista = ArrayList<Desprendible>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_desprendibles, container, false)

        recyclerView = binding.desprendiblesRv
        refreshRV = binding.refreshDesprendibles
        emptyState = binding.emptyDesprendibles
        progreso = binding.pbDesprendibles

        recyclerView.layoutManager = LinearLayoutManager(activity)
        adaptadorRVDesprendibles = AdapterRecyclerView(context, this)
        recyclerView.adapter = adaptadorRVDesprendibles

        obtenerDesprendiblesApi()

        return binding.root
    }

    private fun obtenerDesprendiblesApi(){
        lista.clear()
        adaptadorRVDesprendibles.notifyDataSetChanged()

        val callbackDesprendibles = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {

                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                vistaAMostrar = if (valueArr.length() != 0) {
                    for (i in 0 until valueArr.length()) {
                        val item = valueArr.getJSONObject(i)
                        val desprendible = Desprendible(item)

                        lista.add(desprendible)
                    }
                    "LISTA"
                }else {
                    "EMPTY"
                }
                mostrarVista(vistaAMostrar)
            }

            override fun error(error: VolleyError) {
                if (error.networkResponse.statusCode == 500){
                    if (String(error.networkResponse.data).isNotEmpty()){
                        mostrarVista("EMPTY")
                    }
                }
            }
        }
        obtenerDesprendibles(context!!, callbackDesprendibles, App.idFuncionario.toString())
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
                adaptadorRVDesprendibles.crearListaElementos(lista)
                adaptadorRVDesprendibles.notifyDataSetChanged()

                refreshRV.visibility = View.VISIBLE
                emptyState.visibility = View.GONE
                progreso.visibility = View.GONE
            }
        }
    }

    override fun seleccionarItem(item: Any?) {
        mostrarVista("PROGRESO")
        val desprendible = item as Desprendible
        val parametros = JSONObject().apply {
            put("nominaFuncionarioId", desprendible.nominaDesprendible)
        }

        val callbackDesprendible = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {

                val json = JSONObject(respuesta.toString())

                mostrarVista("LISTA")
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(json.getString("url").plus(json.getString("file"))))
                startActivity(i)
               // descargarDesprendible(json.getString("url").plus(json.getString("file")))

                /*
                 "url": "http://nomintegra.alcanosesp.com:9009",
    "file": "/public/desprendibledepago_20200706222935.pdf"*/
            }

            override fun error(error: VolleyError) {
            }
        }

        obtenerDesprendible(context!!, callbackDesprendible, parametros)
    }

    fun descargarDesprendible(url: String){
        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE);
            setTitle("Desprendible de pago");
            setDescription("Downloading");
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            //setDestinationUri(Uri.parse("file://ghestic"))
        }
        val f = activity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        f.enqueue(request)
    }
}