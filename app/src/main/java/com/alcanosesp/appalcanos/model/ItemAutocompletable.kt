package com.alcanosesp.appalcanos.model

import android.util.Log
import com.alcanosesp.appalcanos.utils.JSONValidador
import org.json.JSONObject

class ItemAutocompletable {

    var id: Int? = null

    var nombre: String? = null

    constructor(id : Int?, nombre : String?){
        this.id = id
        this.nombre = nombre
    }

    constructor(json: JSONObject) {
        try {
            this.id = json.getInt("id")
            this.nombre = json.getString("nombre")
        } catch (e: Exception) {
            Log.e("error", e.message!!)
        }
    }

    constructor(json: JSONObject, llave:String, valor :String) {
        try {
            this.id = json.getInt(llave)
            this.nombre = json.getString(valor)
        } catch (e: Exception) {
            Log.e("error", e.message!!)
        }
    }

    override fun hashCode(): Int {
        return this.id!!
    }
}