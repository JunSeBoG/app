package com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos
//
import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.alcanosesp.appalcanos.api.*
import com.alcanosesp.appalcanos.db.AppDatabase
import com.alcanosesp.appalcanos.db.entity.Familiar
import com.alcanosesp.appalcanos.model.ItemAutocompletable
import com.alcanosesp.appalcanos.model.ItemSpinner
import com.alcanosesp.appalcanos.utils.BaseViewModel
import com.android.volley.VolleyError
import kotlinx.coroutines.launch
import org.json.JSONObject

class FamiliaresViewModel (application: Application) : BaseViewModel(application){

    val db =  AppDatabase(getApplication())
    private val daoFamiliar = db.familiarDao()

    var listaPaises: MutableLiveData<List<ItemSpinner>> = MutableLiveData()
    private var idPaisSeleccionado: Int? = null

    var listaDepartamentos: MutableLiveData<List<ItemSpinner>> = MutableLiveData()
    private var idDeptoSeleccionado: Int? = null

    var listaMunicipios: MutableLiveData<List<ItemSpinner>> = MutableLiveData()
    var idMunicipioSeleccionado: Int? = null

    var listaSexos : MutableLiveData<List<ItemSpinner>> = MutableLiveData()
    var idSexoSeleccionado: Int? = null

    var listaParentescos : MutableLiveData<List<ItemSpinner>> = MutableLiveData()
    var idParentescoSeleccionado: Int? = null

    var listaTiposDocumento : MutableLiveData<List<ItemSpinner>> = MutableLiveData()
    var idTipoDocumentoSeleccionado: Int? = null

    var listaNivelesEducativos : MutableLiveData<List<ItemSpinner>> = MutableLiveData()
    var idNivelEducativoSeleccionado: Int? = null

    var dependiente: String? = null

    val familiares = MutableLiveData<List<Familiar>>()

    fun obtenerFamiliares(){
        launch {
            familiares.value = daoFamiliar.obtenerFamiliares()
        }
    }

    fun insertarFamiliar(familiar: Familiar){
        launch {
            daoFamiliar.insertarFamiliar(familiar)
        }
    }

    fun eliminarFamiliares(){
        launch {
            daoFamiliar.eliminarFamiliares()
        }
    }

    fun obtenerSexosApi(){
        val lista: ArrayList<ItemSpinner> = ArrayList()

        val callbackSexos = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    lista.add(ItemSpinner(item))
                }
                listaSexos.value = lista
            }

            override fun error(error: VolleyError) {
            }
        }
        obtenerSexos(getApplication(), callbackSexos)
    }
    fun sexoSeleccionado(id: Int?) {
        idSexoSeleccionado = if (id == 0){
            null
        }else{
            id
        }
    }

    fun obtenerParentescosApi(){
        val lista: ArrayList<ItemSpinner> = ArrayList()

        val callbackParentescos = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    lista.add(ItemSpinner(item))
                }
                listaParentescos.value = lista
            }

            override fun error(error: VolleyError) {
            }
        }
        obtenerParentescos(getApplication(), callbackParentescos)
    }
    fun parentescoSeleccionado(id: Int?) {
        idParentescoSeleccionado = if (id == 0){
            null
        }else{
            id
        }
    }

    fun dependienteSeleccion(s: String?) {
        dependiente = s
    }

    fun obtenerTiposDocumentoApi(){
        val lista: ArrayList<ItemSpinner> = ArrayList()

        val callbackTiposDocumento = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")

                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    lista.add(ItemSpinner(item))
                }
                listaTiposDocumento.value = lista
            }

            override fun error(error: VolleyError) {
            }
        }
        obtenerTiposDocumento(getApplication(), callbackTiposDocumento)
    }
    fun tipoDocumentoSeleccionado(id: Int?) {
        idTipoDocumentoSeleccionado = if (id == 0){
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