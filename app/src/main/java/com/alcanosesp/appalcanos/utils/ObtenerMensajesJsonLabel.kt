package com.alcanosesp.appalcanos.utils

import android.widget.TextView
import org.json.JSONObject

fun obtenerMensajesJsonLabel(jsonErrors : JSONObject, key :String, textView: TextView){
    if (jsonErrors.has(key)) {
        val errores = jsonErrors.getJSONArray(key)
        val mensajes = StringBuilder()
        for (i in 0 until errores.length()) {
            mensajes.append(errores[i].toString() + "\n")
        }
        textView.text = mensajes.toString()
    }
}