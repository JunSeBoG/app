package com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos
//278
import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.alcanosesp.appalcanos.App
import com.alcanosesp.appalcanos.adapter.SpinnerAdapter
import com.alcanosesp.appalcanos.api.*
import com.alcanosesp.appalcanos.db.AppDatabase
import com.alcanosesp.appalcanos.db.entity.Funcionario
import com.alcanosesp.appalcanos.model.ItemAutocompletable
import com.alcanosesp.appalcanos.model.ItemSpinner
import com.alcanosesp.appalcanos.utils.BaseViewModel
import com.android.volley.VolleyError
import kotlinx.coroutines.launch
import org.json.JSONObject

class BasicosViewModel(application: Application) : BaseViewModel(application) {

    private val db = AppDatabase(getApplication())
    private val dao = db.funcionarioDao()

    val funcionario = MutableLiveData<Funcionario>()

    private var cedulaFuncionario: String? = null

    var listaTiposVivienda: MutableLiveData<List<ItemSpinner>> = MutableLiveData()
    var idViviendaSeleccionada: Int? = null

    var listaPaises: MutableLiveData<List<ItemSpinner>> = MutableLiveData()
    private var idPaisSeleccionado: Int? = null

    var listaDepartamentos: MutableLiveData<List<ItemSpinner>> = MutableLiveData()
    private var idDeptoSeleccionado: Int? = null

    var listaMunicipios: MutableLiveData<List<ItemSpinner>> = MutableLiveData()
    var idMunicipioSeleccionado: Int? = null

    var usaLentes: String? = null


    //Acciones para el funcionario
    fun insertarFuncionario(funcionario: Funcionario) {
        eliminarFuncionario()
        launch {
            dao.agregarFuncionario(funcionario)
        }
    }

    fun obtenerFuncionario() {
        launch {
            funcionario.value = dao.obtenerFuncionario()
            App.idFuncionario = funcionario.value?.id?.toInt()
        }
    }

    fun obtenerCedulaFuncionario(): String? {
        cedulaFuncionario = funcionario.value?.numeroDocumento

        return cedulaFuncionario
    }

    fun actualizarFuncionario(funcionario: Funcionario) {
        launch {
            dao.actualizarFuncionario(funcionario)
            obtenerFuncionario()
        }
    }

    fun eliminarFuncionario() {
        launch {
            dao.eliminarFuncionarios()
        }
    }

    fun atualizarImagenFuncionario(imagenB64: String) {
        launch {
            dao.actualizarImagenFuncionarios(imagenB64)
        }
    }

    fun actualizaNullAdjuntoFoto() {
        launch {
            dao.actualizaNullAdjuntoFoto()
        }
    }

    fun atualizarAdjuntoFuncionario(objectId: String) {
        launch {
            dao.atualizarAdjuntoFuncionario(objectId)
        }
    }

    //Obtener informacion del funcionario
    fun obtenerTipoViviendasApi() {
        val lista: ArrayList<ItemSpinner> = ArrayList()

        val callbackTiposVivienda = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    lista.add(ItemSpinner(item))
                }

                listaTiposVivienda.value = lista
            }

            override fun error(error: VolleyError) {
            }
        }

        obtenerTiposVivienda(getApplication(), callbackTiposVivienda)
    }
    fun tipoViviendaSeleccionada(id: Int?) {
        idViviendaSeleccionada = if (id == 0){
            null
        }else{
            id
        }
    }

    fun obtenerOpcionesUsaLentes(){

    }
    fun usaLentesSeleccion(s: String?){
        usaLentes = s
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

    fun obtenerDeptosApi() {
        val lista: ArrayList<ItemSpinner> = ArrayList()

        val callbackDepartamentos = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    lista.add(ItemSpinner(item))
                }
                listaDepartamentos.value = lista
            }

            override fun error(error: VolleyError) {
            }
        }
        obtenerDepartamentos(getApplication(), callbackDepartamentos, idPaisSeleccionado)
    }
    fun deptoSeleccionado(id: Int?) {
        idDeptoSeleccionado = if (id == 0){
            null
        }else{
            id
        }
    }

    fun obtenerMunicipiosApi() {
        val lista: ArrayList<ItemSpinner> = ArrayList()

        val callbackMunicipios = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    lista.add(ItemSpinner(item))
                }
                listaMunicipios.value = lista
            }

            override fun error(error: VolleyError) {
            }
        }
        obtenerMunicipios(getApplication(), callbackMunicipios, idDeptoSeleccionado)
    }
    fun municipioSeleccionado(id: Int?) {
        idMunicipioSeleccionado = if (id == 0){
            null
        }else{
            id
        }
    }
}