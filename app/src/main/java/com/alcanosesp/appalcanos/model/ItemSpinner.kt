package com.alcanosesp.appalcanos.model

import android.util.Log
import com.alcanosesp.appalcanos.utils.JSONValidador
import org.json.JSONObject

class ItemSpinner {
    var id: Int? = null

    var nombre: String? = null

    constructor(json: JSONObject) {
        try {
            this.id = json.getInt("id")
            this.nombre = json.getString("nombre")
        } catch (e: Exception) {
            Log.e("error", e.message!!)
        }
    }

    constructor(id:Int, nombre:String){
        this.id = id
        this.nombre = nombre
    }

    constructor(json: JSONObject, string: String){
        if (string =="prorrogaDe"){
            try {
                val validador = JSONValidador()
                val codigo = validador.jsonNuloPrimerGrado(json,"diagnosticoCie", "codigo")
                val fechainicial = validador.campoNulo(json.getString("fechaInicio"))
                val fechaFinal = validador.campoNulo(json.getString("fechaFin"))
                val prorroga = codigo.plus(" (").plus(fechainicial).plus(" - ").plus(fechaFinal).plus(" )")

                this.id = json.getInt("id")
                this.nombre = prorroga
            } catch (e: Exception) {
                Log.e("error", e.message!!)
            }
        }
    }

    override fun hashCode(): Int {
        return this.id!!
    }
}