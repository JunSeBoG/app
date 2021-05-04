package com.alcanosesp.appalcanos.ui.menulateral.solicitudes.permisos

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.api.IRespuestaServidor
import com.alcanosesp.appalcanos.api.obtenerClasePermiso
import com.alcanosesp.appalcanos.api.obtenerTipoPermiso
import com.alcanosesp.appalcanos.api.obtenerTipoSoporte
import com.alcanosesp.appalcanos.db.AppDatabase
import com.alcanosesp.appalcanos.db.entity.SolicitudPermiso
import com.alcanosesp.appalcanos.db.entity.SolicitudPermisoSoporte
import com.alcanosesp.appalcanos.model.ItemSpinner
import com.alcanosesp.appalcanos.utils.BaseViewModel
import com.alcanosesp.appalcanos.utils.construirAlerta
import com.android.volley.VolleyError
import kotlinx.coroutines.launch
import org.json.JSONObject

class PermisoViewModel(application: Application):BaseViewModel(application) {
    val db = AppDatabase(getApplication())
    private val daoPermiso = db.solicitudPermisoDao()


    val permiso = MutableLiveData<List<SolicitudPermiso>>()
    val soportePermiso =  MutableLiveData<List<SolicitudPermisoSoporte>>()
    var listaClasePermiso : MutableLiveData<List<ItemSpinner>> = MutableLiveData()
    var idClasePermiso :Int? =null

    var listaTipoPermiso : MutableLiveData<List<ItemSpinner>> = MutableLiveData()
    var idTipoPermiso :Int? =null

    var listaTipoSoporte : MutableLiveData<List<ItemSpinner>> = MutableLiveData()
    var idTipoSoporte :Int? =null

    fun obtenerSolicitudPermiso(){
        launch {
            permiso.value = daoPermiso.obtenerPermiso()
        }
    }

    fun eliminarSolicitudPermiso(){
        launch {
            daoPermiso.eliminarPermisos()
        }
    }

    fun insertarSolicitudPermiso(solicitudPermiso: SolicitudPermiso){
        launch {
            daoPermiso.insertarPermiso(solicitudPermiso)
        }
    }

    fun insertarSoporteSolicitudPermiso(soporte: SolicitudPermisoSoporte){
        launch {
            daoPermiso.insertarSoportePermiso(soporte)
        }
    }

    fun obtenerSoporteSolicitudPermisoByPermisoId(permioId:Int){
        launch {
            soportePermiso.value = daoPermiso.obtenerSoportePermiso(permioId)
        }
    }

    fun eliminarSoporteSolicitudPermiso(){
        launch {
            daoPermiso.eliminarSoportePermisos()
        }
    }

    fun eliminarSoporteSolicitudPermisoById(id:Int){
        launch {
            daoPermiso.eliminarSoportePermisosById(id)
        }
    }

    fun obtenerClasePermisoApi(context: Context) {
        val lista: ArrayList<ItemSpinner> = ArrayList()

        val callbackClasePermiso = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    lista.add(ItemSpinner(item))
                }

                listaClasePermiso.value = lista
            }

            override fun error(error: VolleyError) {
                var codigo = error.networkResponse?.statusCode
                if (codigo==null){
                    codigo =404
                }
                construirAlerta(context, R.layout.toas_login_warning, context.getString(R.string.mensaje_eror_404,codigo.toString()))
            }
        }
        val filtroDiferenteAIncapacidad = "codigo ne 'i' "
        obtenerClasePermiso(getApplication(), filtroDiferenteAIncapacidad,callbackClasePermiso)
    }

    fun clasePermisoSeleccionado(id: Int?){
        idClasePermiso = if (id == 0){
            null
        }else{
            id
        }
    }

    fun obtenerTipoPermisoApi(context: Context) {
        val lista: ArrayList<ItemSpinner> = ArrayList()
        lista.clear()

        val callbackClasePermiso = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    lista.add(ItemSpinner(item))
                }

                listaTipoPermiso.value = lista
            }

            override fun error(error: VolleyError) {
                var codigo = error.networkResponse?.statusCode
                if (codigo==null){
                    codigo =404
                }

                construirAlerta(context, R.layout.toas_login_warning, context.getString(R.string.mensaje_eror_404,codigo.toString()))
            }
        }

        var filtroClasePermiso = " "
        if(idClasePermiso!=null){
            filtroClasePermiso = "and claseAusentismoId eq $idClasePermiso "
        }
        obtenerTipoPermiso(getApplication(), filtroClasePermiso, callbackClasePermiso)
    }

    fun tipoPermisoSeleccionado(id: Int?){
        idTipoPermiso = if (id == 0){
            null
        }else{
            id
        }
    }

    fun obtenerTipoSoporteApi(context: Context) {
        val lista: ArrayList<ItemSpinner> = ArrayList()

        val callbackClasePermiso = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    lista.add(ItemSpinner(item))
                }

                listaTipoSoporte.value = lista
            }

            override fun error(error: VolleyError) {
                var codigo = error?.networkResponse?.statusCode
                if (codigo==null){
                    codigo =404
                }
                construirAlerta(context, R.layout.toas_login_warning, context.getString(R.string.mensaje_eror_404,codigo.toString()))
            }
        }

        obtenerTipoSoporte(getApplication(), callbackClasePermiso)
    }

    fun tipoSoporteSeleccionado(id: Int?){
        idTipoSoporte = if (id == 0){
            null
        }else{
            id
        }
    }
}