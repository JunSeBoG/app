package com.alcanosesp.appalcanos.api

import android.content.Context
import org.json.JSONObject


interface I401 {

    fun call(
        context: Context,
        url: String,
        parametros: JSONObject?,
        iRespuesta: IRespuestaServidor?
    )
}