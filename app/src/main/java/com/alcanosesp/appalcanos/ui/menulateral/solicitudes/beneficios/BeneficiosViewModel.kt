package com.alcanosesp.appalcanos.ui.menulateral.solicitudes.beneficios

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.alcanosesp.appalcanos.api.*
import com.alcanosesp.appalcanos.db.AppDatabase
import com.alcanosesp.appalcanos.db.entity.SolicitudBeneficio
import com.alcanosesp.appalcanos.model.ItemSpinner
import com.alcanosesp.appalcanos.utils.BaseViewModel
import com.alcanosesp.appalcanos.utils.opcionAxulioEducativoNombreC
import com.android.volley.VolleyError
import kotlinx.coroutines.launch
import org.json.JSONObject

class BeneficiosViewModel(application: Application) : BaseViewModel(application) {

    val db =  AppDatabase(getApplication())
    private val daoSolicitudBeneficio = db.solicitudBeneficioDao()

    var listaTiposBeneficio : MutableLiveData<List<ItemSpinner>> = MutableLiveData()
    var idTipoBeneficioSeleccionado: Int? = null
    var nombreOpcionAuxilioEducativoSeleccionado: String? = ""

    var permitePlazoMes: MutableLiveData<String> = MutableLiveData() //: String? = "null"
    var permitePeriodoPago: MutableLiveData<String> = MutableLiveData()// : String? = "null"
    var permiteValorSolicitado: MutableLiveData<String> = MutableLiveData()//permiteValorSolicitado : String? = "null"
    var permiteValorAutorizado: MutableLiveData<String> = MutableLiveData()
    var permiteAuxilioEducativo: MutableLiveData<String> = MutableLiveData()// : String? = "null"
    var permitePermisoEstudio: MutableLiveData<String> = MutableLiveData()// : String? = "null"

    var permitePlazoMesS:String? = "null"
    var permitePeriodoPagoS: String? = "null"
    var permiteValorSolicitadoS:String? = "null"
    var permiteAuxilioEducativoS: String? = "null"
    var permitePermisoEstudioS: String? = "null"

    val solicitudesBeneficios = MutableLiveData<List<SolicitudBeneficio>>()

    var adjuntos =  mutableListOf<HashMap<String, String>>()

    var adjuntosBeneficio = MutableLiveData<List<HashMap<String, String>>>()
    var requisitosBeneficio = MutableLiveData<List<HashMap<String, String>>>()

    fun obtenerSolicitudes(){
        launch {
            solicitudesBeneficios.value = daoSolicitudBeneficio.obtenerSolicitudes()
        }
    }

    fun insertarSolicitud(solicitudBeneficio: SolicitudBeneficio){
        launch {
            daoSolicitudBeneficio.insertarSolicitud(solicitudBeneficio)
        }
    }

    fun eliminarSolicitudes(){
        launch {
            daoSolicitudBeneficio.eliminarSolicitudes()
        }
    }

    fun obtenerTiposBeneficioApi(){
        val lista: ArrayList<ItemSpinner> = ArrayList()

        val callbackTiposBeneficio = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    lista.add(ItemSpinner(item))
                }
                listaTiposBeneficio.value = lista
            }

            override fun error(error: VolleyError) {
            }
        }
        obtenerTiposBeneficio(getApplication(), callbackTiposBeneficio)
    }

    fun tipoBeneficioSeleccionado(id: Int?){
        idTipoBeneficioSeleccionado = if (id == 0){
            null
        }else{
            id
        }
    }

    fun opcionAuxilioSeleccionado(s: String?){
        nombreOpcionAuxilioEducativoSeleccionado = opcionAxulioEducativoNombreC[s]
    }

    fun obtenerParametrosTipoBeneficio(){
        val callbackTipoBeneficio = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")
                Log.i("RESPUESTAB", respuesta.toString())

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)

                    permitePlazoMes(item.getString("plazoMes"))
                    permitePeriodoPago(item.getString("periodoPago"))
                    permiteValorSolicitado(item.getString("valorSolicitado"))
                    permiteAuxilioEducativo(item.getString("permiteAuxilioEducativo"))
                    permitePermisoEstudio(item.getString("permisoEstudio"))
                    permiteValorAutorizado(item.getString("valorAutorizado"))
                }
            }

            override fun error(error: VolleyError) {
            }
        }
        obtenerTipoBeneficio(getApplication(), callbackTipoBeneficio, idTipoBeneficioSeleccionado!!)
    }

    fun obtenerRequisitosBeneficioApi(id: Int?){
        val callbackRequisitosBeneficio = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                adjuntos.clear()
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")
                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)

                    val adjunto = HashMap<String, String>().apply {
                        put("nombre", item.getJSONObject("tipoSoporte").getString("nombre"))
                        //put("adjunto", item.getString("adjunto"))
                    }

                    Log.i("requisito", "nombre ".plus(adjunto["nombre"]))
                    //Log.i("Adjunto", "adjunto ".plus(adjunto["adjunto"]))

                    adjuntos.add(adjunto)
                }
                requisitosBeneficio.value = adjuntos
            }

            override fun error(error: VolleyError) {
                Log.i("RESPUESGFJ", "HASHA")
            }
        }
        obtenerRequisitosTipoBeneficio(getApplication(), callbackRequisitosBeneficio, id!!)
    }

    fun obtenerAdjuntosBeneficioApi(id: Int?){
        Log.i("RESPUESTAB", "HASHA")
        val callbackAdjuntosBeneficio = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                adjuntos.clear()
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")
                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)

                    val adjunto = HashMap<String, String>().apply {
                        put("nombre", item.getJSONObject("tipoBeneficioRequisito").getJSONObject("tipoSoporte").getString("nombre"))
                        put("adjunto", item.getString("adjunto"))
                    }

                    adjuntos.add(adjunto)
                }
                adjuntosBeneficio.value = adjuntos
            }

            override fun error(error: VolleyError) {
                Log.i("RESPUESGFJ", "HASHA")
            }
        }
        obtenerAdjuntosBeneficio(getApplication(), callbackAdjuntosBeneficio, id!!)
    }

    fun permiteValorSolicitado(s: String?){
        permiteValorSolicitado.value = s
        permiteValorSolicitadoS = s
    }
    fun permiteValorAutorizado(s: String?){
        permiteValorAutorizado.value = s
    }
    fun permitePlazoMes(s: String?){
        permitePlazoMes.value = s
        permitePlazoMesS = s
    }
    fun permitePeriodoPago(s: String?){
        permitePeriodoPago.value = s
        permitePeriodoPagoS = s
    }
    fun permiteAuxilioEducativo(s: String?){
        Log.i("RESPUESTAB", s.toString())
        permiteAuxilioEducativo.value = s
        permiteAuxilioEducativoS = s
    }
    fun permitePermisoEstudio(s: String?){
        Log.i("RESPUESTAB", s.toString())
        permitePermisoEstudio.value = s
        permitePermisoEstudioS = s
    }
}