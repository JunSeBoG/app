package com.alcanosesp.appalcanos.utils

import android.app. Application
import androidx.lifecycle.MutableLiveData
import com.alcanosesp.appalcanos.db.AppDatabase
import com.alcanosesp.appalcanos.db.entity.ArchivoAdjunto
import kotlinx.coroutines.launch

class ArchivoAdjuntoViewModel(application: Application):BaseViewModel(application) {
    val db = AppDatabase(getApplication())
    private  val daoArchivoAdjunto = db.archivoAdjuntoDao ()

    val archivoAdjunto = MutableLiveData<List<ArchivoAdjunto>>()

    fun obtenerArchivoAdjunto(){
        launch {

            archivoAdjunto.value = daoArchivoAdjunto.obtenerArchivoAdjunto()
        }
    }

    fun insertarArchivoAdjunto(archivoAdjunto: ArchivoAdjunto){
        eliminarArchivoAdjunto()
        launch {
            daoArchivoAdjunto.insertarArchivoAdjunto(archivoAdjunto)
        }
    }

    fun eliminarArchivoAdjunto(){
        launch {
            daoArchivoAdjunto.eliminarArchivoAdjunto()
        }
    }
}