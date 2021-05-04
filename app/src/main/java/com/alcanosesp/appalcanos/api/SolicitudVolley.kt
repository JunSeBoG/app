package com.alcanosesp.appalcanos.api

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class SolicitudVolley(context: Context?) {

    private val cxt = context
    private val cola: RequestQueue

    companion object {
        private var INSTANCE: SolicitudVolley? = null
        fun getInstance(context: Context?) =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: SolicitudVolley(context).also {
                        INSTANCE = it
                    }
            }
    }

    init {
        this.cola = Volley.newRequestQueue(cxt)
    }

    fun stringRequest(
        url: String,
        parametros: JSONObject?,
        headers: HashMap<String, String>?,
        iRespuesta: IRespuestaServidor?,
        i401: I401?
    ) {
        val stringRequest = object : StringRequest(Method.GET, url,
            Response.Listener<String> { respuesta ->
                iRespuesta?.exito(respuesta)
            }, Response.ErrorListener { error ->
                if (error.networkResponse.statusCode == 401 && i401 != null) {
                    i401.call(cxt!!, url, parametros, iRespuesta)
                } else {
                    iRespuesta?.error(error)
                }
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                if (headers != null) {
                    return headers
                }
                return super.getHeaders()
            }
        }
        this.cola.add(stringRequest)
    }

    fun jsonRequest(
        url: String,
        parametros: JSONObject?,
        headers: HashMap<String, String>?,
        iRespuesta: IRespuestaServidor?,
        i401: I401?
    ) {
        Log.i("JsonRequestURl", url)
        Log.i("JsonRequestPara", parametros.toString())
        val jsonObjReq = object : JsonObjectRequest(url, parametros,
            Response.Listener { response ->

                iRespuesta?.exito(response)
            },
            Response.ErrorListener { error ->
                if (error.networkResponse != null) {
                    if (error.networkResponse.statusCode == 401 && i401 != null) {
                        i401.call(cxt!!, url, parametros, iRespuesta)
                    } else {
                        iRespuesta?.error(error)
                    }
                } else {
                    iRespuesta?.error(error)
                }
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                if (headers != null) {
                    Log.i("Tokennn", headers.toString())
                    return headers
                }
                return super.getHeaders()
            }
        }

        jsonObjReq.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        this.cola.add(jsonObjReq)
    }

    fun jsonPutRequest(
        url: String,
        parametros: JSONObject?,
        headers: HashMap<String, String>?,
        iRespuesta: IRespuestaServidor?,
        i401: I401?
    ) {
        Log.i("JsonRequestURl", url)
        val jsonObjReq = object : JsonObjectRequest(Method.PUT, url, parametros,
            Response.Listener { response ->

                iRespuesta?.exito(response)
            },
            Response.ErrorListener { error ->

                if (error.networkResponse.statusCode == 401 && i401 != null) {
                    i401.call(cxt!!, url, parametros, iRespuesta)
                } else {
                    iRespuesta?.error(error)
                }
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                if (headers != null) {
                    Log.i("Tokennn", headers.toString())
                    return headers
                }
                return super.getHeaders()
            }
        }

        jsonObjReq.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        this.cola.add(jsonObjReq)
    }

    fun imgRequest(
        url: String,
        maxWidth: Int,
        maxHeight: Int,
        scaleType: ImageView.ScaleType,
        decodeConfig: Bitmap.Config,
        header: HashMap<String, String>?,
        iRespuesta: IRespuestaServidor?,
        i401: I401?
    ) {
        Log.i("imgRequestUrlBUsqueda", url)
        val respuesta = ImageRequest(url,
            Response.Listener<Bitmap> { response ->

                iRespuesta?.exito(response)
            }, maxWidth, maxHeight, scaleType, decodeConfig,
            Response.ErrorListener { error ->

                iRespuesta?.error(error)
            })
        this.cola.add(respuesta)
    }

    fun jsonRequestDelete(
        url: String,
        parametros: JSONObject?,
        headers: HashMap<String, String>?,
        iRespuesta: IRespuestaServidor?,
        i401: I401?
    ) {
        Log.i("JsonRequestDeleteURl", url)
        val jsonObjReq = object : JsonObjectRequest(Method.DELETE, url, parametros,
            Response.Listener { response ->

                iRespuesta?.exito(response)
            },
            Response.ErrorListener { error ->
                if (error.networkResponse != null) {
                    if (error.networkResponse.statusCode == 401 && i401 != null) {
                        i401.call(cxt!!, url, parametros, iRespuesta)
                    } else {
                        iRespuesta?.error(error)
                    }
                } else {
                    iRespuesta?.error(error)
                }
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                if (headers != null) {
                    Log.i("Tokennn", headers.toString())
                    return headers
                }
                return super.getHeaders()
            }
        }

        jsonObjReq.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        this.cola.add(jsonObjReq)
    }


    fun jsonPatchRequest(
        url: String,
        parametros: JSONObject?,
        headers: HashMap<String, String>?,
        iRespuesta: IRespuestaServidor?,
        i401: I401?
    ) {
        Log.i("JsonRequestPatchURl", url)
        Log.i("JsonRequestPatchParam", parametros.toString())

        val jsonObjReq = object : JsonObjectRequest(Method.PATCH, url, parametros,
            Response.Listener { response ->

                iRespuesta?.exito(response)
            },
            Response.ErrorListener { error ->
                if (error.networkResponse != null) {
                    if (error.networkResponse.statusCode == 401 && i401 != null) {
                        i401.call(cxt!!, url, parametros, iRespuesta)
                    } else {
                        iRespuesta?.error(error)
                    }
                } else {
                    iRespuesta?.error(error)
                }
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                if (headers != null) {
                    Log.i("Tokennn", headers.toString())
                    return headers
                }
                return super.getHeaders()
            }
        }

        jsonObjReq.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        this.cola.add(jsonObjReq)
    }
}