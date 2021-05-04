package com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.alcanosesp.appalcanos.db.AppDatabase
import com.alcanosesp.appalcanos.db.entity.Experiencia
import com.alcanosesp.appalcanos.utils.BaseViewModel
import kotlinx.coroutines.launch

class ExperienciasViewModel(application: Application) : BaseViewModel(application) {

    val db =  AppDatabase(getApplication())
    private val daoExperiencia = db.experienciaDao()
    var trabajaActualmente: String? = null

    val experiencias = MutableLiveData<List<Experiencia>>()

    fun obtenerExperiencias(){
        launch {
            experiencias.value = daoExperiencia.obtenerExperiencias()
        }
    }

    fun insertarExperiencia(experiencia: Experiencia){
        launch {
            daoExperiencia.insertarExperiencia(experiencia)
        }
    }

    fun eliminarExperiencias(){
        launch {
            daoExperiencia.eliminarExperiencias()
        }
    }

    fun trabajaActualmenteSeleccion(s: String?) {
        trabajaActualmente = s
    }
}