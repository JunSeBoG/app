package com.alcanosesp.appalcanos.api

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import kotlin.collections.HashMap
interface Micallback {
    fun call(response: Any?)
}
interface MiErrorCallback {
    fun call(response: NetworkResponse?)
}
interface MicallbackImg {
    fun call(response: Bitmap?)
}
class LoginRequest(context: Context?) {
    private val cxt = context
    private val cola: RequestQueue
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: LoginRequest? = null
        fun getInstance(context: Context?) =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: LoginRequest(context).also {
                        INSTANCE = it
                    }
            }
    }
    init {
        this.cola = Volley.newRequestQueue(cxt)
    }
    fun post(
        url: String,
        parametros: JSONObject?,
        header: HashMap<String, String>?,
        callback: Micallback,
        errorCallback: Micallback?
    ) {
        Log.i("volleyUrlBUsqueda", url)
        val respuesta = JsonObjectRequest(url, parametros,
            Response.Listener { response ->
                callback.call(response)
            },
            Response.ErrorListener { error ->
                //                val resp = error.networkResponse.data
                errorCallback?.call(error)
            }
        )
        // override getHeader for pass session to service
        fun getHeaders(): MutableMap<String, String> {
            val headers = header!!
            // "Cookie" and "PHPSESSID=" + <session value> are default format
            return headers
        }
        this.cola.add(respuesta)
    }
    fun img(
        url: String,
        maxWidth: Int,
        maxHeight: Int,
        scaleType: ImageView.ScaleType,
        decodeConfig: Bitmap.Config,
        header: HashMap<String, String>?,
        callback: MicallbackImg,
        errorCallback: Micallback?
    ) {
        Log.i("volleyUrlBUsqueda", url)
        val respuesta = ImageRequest(url,
            Response.Listener<Bitmap> { response ->
                callback.call(response)
            }, maxWidth, maxHeight, scaleType, decodeConfig,
            Response.ErrorListener { error ->
                val resp = error.networkResponse.data
                val err = String(resp)
                try {
                    val jsonError = JSONObject(err)
                    Log.i("respuestaConE", jsonError.getString("errors"))
                    errorCallback?.call(error)
                    errorCallback?.call(error)
                } catch (e: Exception) {
                    Log.i("no se pu", e.message)
                }
                errorCallback?.call(error)
            })
        this.cola.add(respuesta)
    }
    fun patch(
        url: String,
        parametros: JSONObject?,
        header: HashMap<String, String>?,
        callback: Micallback,
        errorCallback: Micallback?
    ) {
        Log.i("volleyUrlBUsqueda", url)
        val respuesta = JsonObjectRequest(Request.Method.PATCH, url, parametros,
            Response.Listener { response ->
                callback.call(response)
            },
            Response.ErrorListener { error ->
                val resp = error.networkResponse.data
                val err = String(resp)
                try {
                    val jsonError = JSONObject(err)
                    Log.i("respuestaConE", jsonError.getString("errors"))
                    errorCallback?.call(error)
                    errorCallback?.call(error)
                } catch (e: Exception) {
                    Log.i("no se pu", e.message)
                }
                errorCallback?.call(error)
            }
        )
        this.cola.add(respuesta)
    }
    fun delete(
        url: String,
        parametros: JSONObject?,
        header: HashMap<String, String>?,
        callback: Micallback,
        errorCallback: Micallback?
    ) {
        Log.i("volleyUrlBUsqueda", url)
        val respuesta = JsonObjectRequest(Request.Method.DELETE, url, parametros,
            Response.Listener { response ->
                callback.call(response)
            },
            Response.ErrorListener { error ->
                val resp = error.networkResponse.data
                val status = error.networkResponse.statusCode
                val jsonError = JSONObject()
                jsonError.put("status", status)
                errorCallback?.call(jsonError)
            }
        )
        this.cola.add(respuesta)
    }
}
/*
val hash = HashMap<String, String>().apply {
            put("Thor", "")
            put("SpiderMan", "")
            put("NickFury", "")
        }
 */