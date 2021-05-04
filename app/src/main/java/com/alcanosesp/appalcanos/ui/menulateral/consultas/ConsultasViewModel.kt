package com.alcanosesp.appalcanos.ui.menulateral.consultas

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.model.ModeloLista
import com.alcanosesp.appalcanos.utils.BaseViewModel
import java.util.ArrayList

class ConsultasViewModel(application: Application, val context: Context) : BaseViewModel(application){

    val listaIndiceConsultas = MutableLiveData<ArrayList<ModeloLista>>()

    fun cargarIndiceConsultas(){
        listaIndiceConsultas.value = cargarListaConsultas()
    }

    private fun cargarListaConsultas() : ArrayList<ModeloLista>{
        val lista = ArrayList<ModeloLista>()
        //Pago PILA
        lista.add(
            ModeloLista(
                R.drawable.ic_local_hospital,
                context.getString(R.string.consulta_pila_titulo),
                context.getString(R.string.consulta_pila_desc),
                context.getString(R.string.URLPagoPila)
            )
        )

        return lista
    }
}