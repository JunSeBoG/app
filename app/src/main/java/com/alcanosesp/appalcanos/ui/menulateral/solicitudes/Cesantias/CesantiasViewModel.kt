package com.alcanosesp.appalcanos.ui.menulateral.solicitudes.cesantias

import android.app.Application
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import com.alcanosesp.appalcanos.App
import com.alcanosesp.appalcanos.api.*
import com.alcanosesp.appalcanos.db.AppDatabase
import com.alcanosesp.appalcanos.db.entity.SolicitudCesantia
import com.alcanosesp.appalcanos.model.DatosCesantias
import com.alcanosesp.appalcanos.model.ItemSpinner
import com.alcanosesp.appalcanos.utils.BaseViewModel
import com.android.volley.VolleyError
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.json.JSONObject

class CesantiasViewModel(application: Application) : BaseViewModel(application) {
    val db = AppDatabase(getApplication())
    private val daoCesantia = db.solicitudCesantiasDao()

    var cesantias = MutableLiveData<List<SolicitudCesantia>>()
    var redireccionar = MutableLiveData<Boolean>()
    var guardoSolicitudCesantias = MutableLiveData<String>()
    var datosActualesCesantias = MutableLiveData<DatosCesantias>()
    var listaMotivoCesantia: MutableLiveData<List<ItemSpinner>> = MutableLiveData()
    var idMotivoCesantia: Int? = null
    var mensajeDialogoError = MutableLiveData<String?>()


    fun obtenerSolicidCesantias() {
        launch {
            cesantias.value = daoCesantia.obtenerSolicitudCesantias()
        }
    }

    fun eliminarSolicidCesantias() {
        launch {
            daoCesantia.eliminarSolicitudCesantias()
        }
    }

    fun insertarSolicidCesantias(solicitudCesantia: SolicitudCesantia) {
        launch {
            daoCesantia.insertarSolicitudCesantias(solicitudCesantia)
        }
    }

    fun obtenerSolicitudCesantiasApi() {
        val callBackCesantia = object : IRespuestaServidor {

            override fun exito(respuesta: Any?) {
                eliminarSolicidCesantias()
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val posiccion = (i + 1).toString()
                    val item = valueArr.getJSONObject(i)
                    val cesantias = SolicitudCesantia(item)

                    insertarSolicidCesantias(cesantias)
                }

                val handler = Handler()
                handler.postDelayed({ obtenerSolicidCesantias() }, 2000)

            }

            override fun error(error: VolleyError) {
                var codigo = error.networkResponse?.statusCode
                if (codigo == null) {
                    codigo = 404
                }
                mensajeDialogoError.value = "Error al obtener solicitud de secantias. $codigo"
            }
        }
        optenerSolicitudCesantias(getApplication(), callBackCesantia, App.idFuncionario.toString())
    }


    fun cancelarSolicitudCesantiasApi(idSolicitudCesantia: Int?) {
        val callbackCancelar = object : IRespuestaServidor {

            override fun exito(respuesta: Any?) {
                recargarSolicitudCesantiasApi()
            }

            override fun error(error: VolleyError) {
                var codigo = error.networkResponse?.statusCode
                if (codigo == null) {
                    codigo = 404
                }
                mensajeDialogoError.value = "Error al cancelar la solicitud. $codigo"

            }
        }

        val headers = JSONObject()
        headers.put("id", idSolicitudCesantia)
        headers.put("Estado", "Cancelada")

        cancelarSolicitudCesantias(
            getApplication(),
            idSolicitudCesantia!!,
            headers,
            callbackCancelar
        )
    }

    fun obtenerMotivoSolicitiudApi() {
        val callBackCesantia = object : IRespuestaServidor {
            val lista: ArrayList<ItemSpinner> = ArrayList()

            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")
                var gson = Gson()

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    lista.add(ItemSpinner(item))
                }
                listaMotivoCesantia.value = lista
            }

            override fun error(error: VolleyError) {
                var codigo = error.networkResponse?.statusCode
                if (codigo == null) {
                    codigo = 404
                }
                mensajeDialogoError.value = "Error al consultar los motivos $codigo"
            }
        }
        optenerMotivoSolicitudCesantias(getApplication(), callBackCesantia)
    }

    fun motivoCesantia(id: Int?) {
        idMotivoCesantia = if (id == 0) {
            null
        } else {
            id
        }
    }

    fun obtenerDatosActualesSolicitiudApi() {
        val callBackCesantia = object : IRespuestaServidor {
            val lista: ArrayList<ItemSpinner> = ArrayList()

            override fun exito(respuesta: Any?) {
                val gson = Gson()
                datosActualesCesantias.value =
                    gson.fromJson(respuesta.toString(), DatosCesantias::class.java)
            }

            override fun error(error: VolleyError) {
                var codigo = error.networkResponse?.statusCode
                if (codigo == null) {
                    codigo = 404
                }
                mensajeDialogoError.value = "Error al consultar datos actuales $codigo"

            }
        }
        obtenerDatosActualesSolicitiud(
            getApplication(),
            callBackCesantia,
            App.idFuncionario.toString()
        )
    }

    fun enviarSolicitudCesantiasApi(parametros: JSONObject) {
        val callbackCrearCesantias = object : IRespuestaServidor {

            override fun exito(respuesta: Any?) {
                guardoSolicitudCesantias.value = "EXITO"
            }

            override fun error(error: VolleyError) {
                guardoSolicitudCesantias.value = String(error.networkResponse.data)
            }
        }

        if (App.solicitudCesantia == null) {
            registrarSolicitudCesantias(getApplication(), callbackCrearCesantias, parametros)
        } else {
            parametros.put("id", App.solicitudCesantia?.id)
            editarSolicitudCesantias(
                getApplication(),
                callbackCrearCesantias,
                parametros,
                App.solicitudCesantia?.id!!
            )
        }
    }

    fun recargarSolicitudCesantiasApi() {
        val callbackRecargaCesantias = object : IRespuestaServidor {

            override fun exito(respuesta: Any?) {
                eliminarSolicidCesantias()

                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    val cesantias = SolicitudCesantia(item)

                    insertarSolicidCesantias(cesantias)
                }


                val handler = Handler()
                handler.postDelayed({
                    obtenerSolicidCesantias()
                    redireccionar.value = true
                }, 1500)
            }

            override fun error(error: VolleyError) {
                var codigo = error.networkResponse?.statusCode
                if (codigo == null) {
                    codigo = 404
                }
                mensajeDialogoError.value = "Error al recargar solicitude de cesantias $codigo"
            }
        }
        optenerSolicitudCesantias(
            getApplication(),
            callbackRecargaCesantias,
            App.idFuncionario.toString()
        )

    }


}