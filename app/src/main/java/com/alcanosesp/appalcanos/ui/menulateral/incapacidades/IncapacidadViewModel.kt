package com.alcanosesp.appalcanos.ui.menulateral.incapacidades

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.alcanosesp.appalcanos.api.IRespuestaServidor
import com.alcanosesp.appalcanos.api.obtenerDiagnosticoCie
import com.alcanosesp.appalcanos.api.obtenerIncapaciadProrrogaPorFecha
import com.alcanosesp.appalcanos.api.obtenerTipoIncapaciad
import com.alcanosesp.appalcanos.db.AppDatabase
import com.alcanosesp.appalcanos.db.entity.AusentismoFuncionario
import com.alcanosesp.appalcanos.model.ItemAutocompletable
import com.alcanosesp.appalcanos.model.ItemSpinner
import com.alcanosesp.appalcanos.utils.BaseViewModel
import com.android.volley.VolleyError
import kotlinx.coroutines.launch
import org.json.JSONObject

class IncapacidadViewModel(application: Application) : BaseViewModel(application) {
    val db = AppDatabase(getApplication())
    private val daoAusentismoFuncionario = db.ausentismoFuncionarioDao()

    var listaDiagnosticoCie: MutableLiveData<List<ItemAutocompletable>> = MutableLiveData()
    var idDiagnosticoCieSeleccionado: Int? = null

    var listaTipoIncapacidad: MutableLiveData<List<ItemSpinner>> = MutableLiveData()
    var idTipoIncapacidadSeleccionado: Int? = null

    var listaProrrogaDe: MutableLiveData<List<ItemSpinner>> = MutableLiveData()
    var idProrrogaDeSelecionda: Int? = null

    val incapacidad = MutableLiveData<List<AusentismoFuncionario>>()

    var listaIdIncapacidades : MutableLiveData<List<Int>> = MutableLiveData()

    fun obtenerIncapacidad() {
        launch {
            incapacidad.value = daoAusentismoFuncionario.obtenerAusentismoFuncionario()
        }
    }

    fun obtenerListaIdIncaPacidades() {
        launch {
            listaIdIncapacidades.value = daoAusentismoFuncionario.obtenerIdIncaPacidades()
        }
    }

    fun insertarIncapacidad(incapacidad: AusentismoFuncionario) {
        launch {
            daoAusentismoFuncionario.insertarAusentismoFuncionario(incapacidad)
        }
    }

    fun ActualizarIncapacidadProroga(prorroga:String, ausentimosId:Int) {
        launch {
            daoAusentismoFuncionario.actualizarProrrogaAusentismoFuncionario(prorroga, ausentimosId)
        }
    }

    fun eliminarIncapacidad() {
        launch {
            daoAusentismoFuncionario.eliminarAusentismoFuncionario()
        }
    }

    fun obtenerDiagnosticoCieApi(like: CharSequence) {
        val lista: ArrayList<ItemAutocompletable> = ArrayList()

        val callbackDiagnostico = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")
                lista.clear()
                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    lista.add(ItemAutocompletable(item, "id", "codigo"))
                }

                listaDiagnosticoCie.value = lista
            }

            override fun error(error: VolleyError) {
            }
        }

        obtenerDiagnosticoCie(getApplication(), callbackDiagnostico, like)
    }

    fun diagnosticoCieSeleccionadoSeleccionado(id: Int?) {
        idDiagnosticoCieSeleccionado = id
    }


    fun obtenerTipoIncapacidadApi() {
        val lista: ArrayList<ItemSpinner> = ArrayList()

        val callbackTipoIncapacidad = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    lista.add(ItemSpinner(item))
                }
                listaTipoIncapacidad.value = lista
            }

            override fun error(error: VolleyError) {
            }
        }
        obtenerTipoIncapaciad(getApplication(), callbackTipoIncapacidad)
    }

    fun tipoIncapacidadSeleccionada(id: Int?) {
        idTipoIncapacidadSeleccionado = id
    }


    fun obtenerProrrogaDeApi(fechaInicio: String, idFuncionario: Int) {
        val lista: ArrayList<ItemSpinner> = ArrayList()

        val callbackIncapacidadAPrrorogar = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    lista.add(ItemSpinner(item, "prorrogaDe"))
                }
                listaProrrogaDe.value = lista
            }

            override fun error(error: VolleyError) {
            }
        }

        obtenerIncapaciadProrrogaPorFecha(
            getApplication(),
            fechaInicio,
            idFuncionario,
            callbackIncapacidadAPrrorogar
        )
    }

    fun prorrogaDeSeleccionada(id: Int?) {
        idProrrogaDeSelecionda = id
    }
}