package com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.alcanosesp.appalcanos.api.*
import com.alcanosesp.appalcanos.db.AppDatabase
import com.alcanosesp.appalcanos.db.entity.Estudio
import com.alcanosesp.appalcanos.model.ItemSpinner
import com.alcanosesp.appalcanos.utils.BaseViewModel
import com.android.volley.VolleyError
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class EstudiosViewModel(application: Application) : BaseViewModel(application) {

    val db =  AppDatabase(getApplication())
    private val daoEstudio = db.estudioDao()

    var listaPaises : MutableLiveData<List<ItemSpinner>> = MutableLiveData()
    var idPaisSeleccionado: Int? = null

    var listaProfesiones : MutableLiveData<List<ItemSpinner>> = MutableLiveData()
    var idProfesionSeleccionada: Int? = null

    var listaNivelesEducativos : MutableLiveData<List<ItemSpinner>> = MutableLiveData()
    var idNivelEducativoSeleccionado: Int? = null

    var nombreEstadoEstudioSeleccionado: String? = null

    val estudios = MutableLiveData<List<Estudio>>()

    fun obtenerEstudios(){
        launch {
            estudios.value = daoEstudio.obtenerEstudios()
        }
    }

    fun insertarEstudio(estudio: Estudio){
        launch {
            daoEstudio.insertarEstudio(estudio)
        }
    }

    fun eliminarEstudios(){
        launch {
            daoEstudio.eliminarEstudios()
        }
    }

    fun obtenerPaisesApi() {
        val lista: ArrayList<ItemSpinner> = ArrayList()

        val callbackPaises = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    lista.add(ItemSpinner(item))
                }

                listaPaises.value = lista
            }

            override fun error(error: VolleyError) {
            }
        }

        obtenerPaises(getApplication(), callbackPaises)
    }

    fun paisSeleccionado(id: Int?) {
        idPaisSeleccionado = if (id == 0){
            null
        }else{
            id
        }
    }

    fun obtenerProfesionesApi(){
        val lista: ArrayList<ItemSpinner> = ArrayList()

        val callbackProfesiones = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    lista.add(ItemSpinner(item))
                }

                listaProfesiones.value = lista
            }

            override fun error(error: VolleyError) {
            }
        }

        obtenerProfesiones(getApplication(), callbackProfesiones)
    }

    fun profesionSeleccionada(id: Int?) {
        idProfesionSeleccionada = if (id == 0){
            null
        }else{
            id
        }
    }

    fun obtenerNivelesEducativosApi(){
        val lista: ArrayList<ItemSpinner> = ArrayList()

        val callbackNivelesEducativos = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    lista.add(ItemSpinner(item))
                }

                listaNivelesEducativos.value = lista
            }

            override fun error(error: VolleyError) {
            }
        }

        obtenerNivelesEducativos(getApplication(), callbackNivelesEducativos)
    }

    fun nivelEducativoSeleccionado(id: Int?) {
        idNivelEducativoSeleccionado = if (id == 0){
            null
        }else{
            id
        }
    }

    //HACER GENERAL
    val snakeRegex = " [a-zA-Z]".toRegex()
    fun String.snakeToLowerCamelCase(): String {
        return snakeRegex.replace(this) {
            it.value.replace(" ", "")
                .toUpperCase(Locale.getDefault())
        }
    }
    fun estadoEstudioSeleccionado(s: String?){
        nombreEstadoEstudioSeleccionado = s?.snakeToLowerCamelCase()
    }
}