package com.alcanosesp.appalcanos.ui.menulateral.solicitudes.vacaciones

import android.app.Application
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import com.alcanosesp.appalcanos.App
import com.alcanosesp.appalcanos.api.*
import com.alcanosesp.appalcanos.db.AppDatabase
import com.alcanosesp.appalcanos.db.entity.SolicitudVacaciones
import com.alcanosesp.appalcanos.model.InterrupcionVacaciones
import com.alcanosesp.appalcanos.utils.BaseViewModel
import com.android.volley.VolleyError
import kotlinx.coroutines.launch
import org.json.JSONObject

class VacacionesViewModel(application: Application) : BaseViewModel(application) {
    val db = AppDatabase(getApplication())
    private val daoVacaciones = db.solicitudVacacionesDao()
    var guardoSolicitudVacaciones = MutableLiveData<String>()

    val vacaciones = MutableLiveData<List<SolicitudVacaciones>>()
    val interrupcion = MutableLiveData<List<InterrupcionVacaciones>>()

    var redireccionar = MutableLiveData<Boolean>()
    var mensajeDialogoErrorVacaciones = MutableLiveData<String?>()

    fun obtenerSolicitudVacaciones() {
        launch {
            vacaciones.value = daoVacaciones.obtenerSolicitudVacaciones()
        }
    }

    fun eliminarSolicitudVacaciones() {
        launch {
            daoVacaciones.eliminarSolicitudVacaciones()
        }
    }

    fun insertarSolicitudVacaciones(solicitudVacaciones: SolicitudVacaciones) {
        launch {
            daoVacaciones.insertarSolicitudVacaciones(solicitudVacaciones)
        }
    }


    fun obtenerSolicitudVacacionesApi() {
        val callBackVacaciones = object : IRespuestaServidor {

            override fun exito(respuesta: Any?) {
                eliminarSolicitudVacaciones()
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    val vacaciones = SolicitudVacaciones(item)

                    insertarSolicitudVacaciones(vacaciones)
                }

                val handler = Handler()
                handler.postDelayed({ obtenerSolicitudVacaciones() }, 2000)

            }

            override fun error(error: VolleyError) {
                var codigo = error.networkResponse?.statusCode
                if (codigo == null) {
                    codigo = 404
                }
                mensajeDialogoErrorVacaciones.value = "$codigo Error al obtener solicitud de vacaciones."
            }
        }
        optenerSolicitudVacaciones(
            getApplication(),
            callBackVacaciones,
            App.idFuncionario.toString()
        )
    }

    fun cancelarSolicitudVacacionesApi(idSolicitudVacaciones: Int?) {
        val callbackCancelar = object : IRespuestaServidor {

            override fun exito(respuesta: Any?) {
                recargarSolicitudVacacionesApi()
            }

            override fun error(error: VolleyError) {
                var codigo = error.networkResponse?.statusCode
                if (codigo == null) {
                    codigo = 404
                }
                mensajeDialogoErrorVacaciones.value = "$codigo Error al cancelar la solicitud."

            }
        }

        val headers = JSONObject()
        headers.put("id", idSolicitudVacaciones)
        headers.put("Estado", "Cancelada")

        cancelarSolicitudVacaciones(
            getApplication(),
            idSolicitudVacaciones!!,
            headers,
            callbackCancelar
        )
    }

    fun recargarSolicitudVacacionesApi() {
        val callbackRecargaVacaciones = object : IRespuestaServidor {

            override fun exito(respuesta: Any?) {
                eliminarSolicitudVacaciones()

                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    val vacaciones = SolicitudVacaciones(item)

                    insertarSolicitudVacaciones(vacaciones)
                }


                val handler = Handler()
                handler.postDelayed({
                    obtenerSolicitudVacaciones()
                    redireccionar.value = true
                }, 1500)
            }

            override fun error(error: VolleyError) {
                var codigo = error.networkResponse?.statusCode
                if (codigo == null) {
                    codigo = 404
                }
                mensajeDialogoErrorVacaciones.value = "$codigo Error al recargar solicitude de vacaciones."
            }
        }
        optenerSolicitudVacaciones(
            getApplication(),
            callbackRecargaVacaciones,
            App.idFuncionario.toString()
        )

    }

    fun obtenerInterrupcionesApi() {
        val callBackVacaciones = object : IRespuestaServidor {

            override fun exito(respuesta: Any?) {
                val listaInterrupciones = ArrayList<InterrupcionVacaciones>()

                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    val data = InterrupcionVacaciones(item)
                    listaInterrupciones.add(data)
                }

                interrupcion.value = listaInterrupciones

            }

            override fun error(error: VolleyError) {
                var codigo = error.networkResponse?.statusCode
                if (codigo == null) {
                    codigo = 404
                }
                mensajeDialogoErrorVacaciones.value = "Error al obtener interrupcipón de vacaciones. $codigo"
            }
        }
        optenerInterrupcionesVacaciones(
            getApplication(),
            callBackVacaciones,
            App.solicitudVacaciones?.id.toString()
        )
    }

    fun obtenerPeriodoMasAntiguoApi(contratoId: String) {
        val callBackVacaciones = object : IRespuestaServidor {

            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")
                val data = SolicitudVacaciones(JSONObject(valueArr[0].toString()), "CREAR")
                val lista = ArrayList<SolicitudVacaciones>()
                lista.add(data)
                vacaciones.value = lista
            }

            override fun error(error: VolleyError) {
                var codigo = error.networkResponse?.statusCode
                if (codigo == null) {
                    codigo = 404
                }
                mensajeDialogoErrorVacaciones.value = "Error al obtener periodo mas antiguo de vacaciones. $codigo"
            }
        }
        obtenerPeriodoMasAntiguo(
            getApplication(),
            callBackVacaciones,
            contratoId
        )
    }

    fun enviarSolicitudVacacionesApi(parametros: JSONObject) {
        val callbackCrearVacaciones = object : IRespuestaServidor {

            override fun exito(respuesta: Any?) {
                guardoSolicitudVacaciones.value = "EXITO"
            }

            override fun error(error: VolleyError) {
                guardoSolicitudVacaciones.value = String(error.networkResponse.data)
            }
        }

        if (App.solicitudVacaciones == null) {
            registrarSolicitudVacaciones(getApplication(), callbackCrearVacaciones, parametros)
        } else {
            parametros.put("id", App.solicitudVacaciones?.id)
            editarSolicitudVacaciones(
                getApplication(),
                callbackCrearVacaciones,
                parametros,
                App.solicitudVacaciones?.id!!
            )
        }
    }
}