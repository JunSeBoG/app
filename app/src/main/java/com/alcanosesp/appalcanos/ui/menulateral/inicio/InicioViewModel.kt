package com.alcanosesp.appalcanos.ui.menulateral.inicio

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.alcanosesp.appalcanos.App
import com.alcanosesp.appalcanos.api.IRespuestaServidor
import com.alcanosesp.appalcanos.api.optenerDatosGraficasInicio
import com.alcanosesp.appalcanos.model.Graficas
import com.alcanosesp.appalcanos.utils.BaseViewModel
import com.alcanosesp.appalcanos.utils.JSONValidador
import com.android.volley.VolleyError
import com.google.gson.Gson
import org.json.JSONObject

class InicioViewModel(application: Application) : BaseViewModel(application) {
    val inicioGraficasActualizarDatos = MutableLiveData<Graficas>()
    val inicioGraficasBeneficios = MutableLiveData<List<Graficas>>()
    val inicioGraficasPermisos = MutableLiveData<List<Graficas>>()

    fun optenerDatosGraficasInicioAPI(contexto: Context) {
        var callbackGraficas = object : IRespuestaServidor {

            override fun exito(respuesta: Any?) {

                val validador: JSONValidador
                val gson = Gson()
                val obj = JSONObject(respuesta.toString())


                var actualizadatos = obj.getJSONArray("actualizarDatos")
                if (actualizadatos.length() > 0) {
                    inicioGraficasActualizarDatos.value =
                        gson.fromJson(actualizadatos[0].toString(), Graficas::class.java)

                }


                var permisos = obj.getJSONArray("permiso")
                var listaPermisos = ArrayList<Graficas>()
                listaPermisos.clear()
                if (actualizadatos.length() > 0) {
                    for (i in 0 until permisos.length()) {
                        val item = permisos.getString(i)
                            .replace("cantidad", "valor")
                            .replace("estado", "descripcion")

                        val graficaa = gson.fromJson(item, Graficas::class.java)
                        listaPermisos.add(graficaa)
                    }
                    inicioGraficasPermisos.value = listaPermisos
                }


                val beneficios = obj.getJSONArray("beneficios")
                var listaBenficios = ArrayList<Graficas>()
                listaBenficios.clear()

                if (actualizadatos.length() > 0) {
                    for (i in 0 until beneficios.length()) {
                        val item = beneficios.getString(i)
                            .replace("cantidad", "valor")
                            .replace("estado", "descripcion")

                        val graficaa = gson.fromJson(item, Graficas::class.java)
                        listaBenficios.add(graficaa)
                    }
                    inicioGraficasBeneficios.value = listaBenficios

                }
            }

            override fun error(error: VolleyError) {
                var codigo = error.networkResponse?.statusCode
                if (codigo == null) {
                    codigo = 404
                }
                Log.e("Errorima", codigo.toString())
            }

        }
        optenerDatosGraficasInicio(contexto, callbackGraficas, App.idFuncionario.toString())
    }

}