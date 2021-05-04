package com.alcanosesp.appalcanos.model

import android.util.Log
import com.alcanosesp.appalcanos.utils.JSONValidador
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import kotlin.math.round

class InterrupcionVacaciones {
    var fechaInicio: String = ""
    var fechaFin: String = ""
    var justificacion: String = ""
    var causalInterrupcion: String = ""


    constructor(json: JSONObject) {
        try {
            val validador = JSONValidador()

            this.fechaInicio =  validador.jsonNuloPrimerGrado(json,"ausentismoFuncionario","fechaInicio")
            this.fechaFin = validador.jsonNuloPrimerGrado(json,"ausentismoFuncionario","fechaFin")
            this.justificacion = validador.jsonNuloPrimerGrado(json,"ausentismoFuncionario","justificacion")
            this.causalInterrupcion = validador.jsonNuloSegundoGrado(json,"ausentismoFuncionario","tipoAusentismo","nombre")

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

