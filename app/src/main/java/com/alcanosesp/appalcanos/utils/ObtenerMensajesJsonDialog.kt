package com.alcanosesp.appalcanos.utils

import android.content.Context
import com.alcanosesp.appalcanos.R
import org.json.JSONObject

fun obtenerMensajesJsonDialog(jsonErrors :JSONObject, key :String, context:Context){
    if (jsonErrors.has(key)) {
        val errores = jsonErrors.getJSONArray(key)
        val mensajes = StringBuilder()
        for (i in 0 until errores.length()) {
            mensajes.append(errores[i].toString() + "\n")
        }
        construirAlerta(context!!, R.layout.toas_login_warning, mensajes.toString())
    }
}

