package com.alcanosesp.appalcanos.model

import android.util.Log
import com.alcanosesp.appalcanos.utils.JSONValidador
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import kotlin.math.round

class DatosCesantias {
    var baseCesantias: String = ""
    var valorCesantiasAcumuladas: String = ""
    var cantidadDiasAcumulados: String = ""
    var valorInteresCesantiasAcumuladas: String = ""
    var anticiposSolicitados: String = ""

    constructor(json: JSONObject) {
        try {
            val validador = JSONValidador()
            this.baseCesantias = moneda(json.getString("baseCesantias"))
            this.valorCesantiasAcumuladas = moneda(json.getString("valorCesantiasAcumuladas"))
            this.cantidadDiasAcumulados = json.getString("cantidadDiasAcumulados")
            this.valorInteresCesantiasAcumuladas =
                moneda(json.getString("valorInteresCesantiasAcumuladas"))
            this.anticiposSolicitados = moneda(json.getString("anticiposSolicitados"))

        } catch (e: Exception) {
            Log.e("DatosCesantias", e.message.toString())
        }
    }

    constructor()

    fun moneda(s: String?): String {
        val a = s
        return if (s == "0" || s == "" || s == null) {
            "0.00"
        } else {
            val symbols = DecimalFormatSymbols().apply {
                groupingSeparator = '.'
                decimalSeparator = ','
            }

            val decimalFormat = DecimalFormat("#,###.##", symbols)

            val redondear2deciamles = round(s.toDouble() * 100) / 100

            decimalFormat.format(redondear2deciamles)
        }
    }
}