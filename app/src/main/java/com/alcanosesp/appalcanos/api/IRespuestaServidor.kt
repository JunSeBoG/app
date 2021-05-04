package com.alcanosesp.appalcanos.api

import com.android.volley.VolleyError


interface IRespuestaServidor {

    fun exito(respuesta: Any?)

    fun error(error: VolleyError)
}