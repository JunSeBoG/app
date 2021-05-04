package com.alcanosesp.appalcanos.model

import android.util.Log
import com.alcanosesp.appalcanos.utils.JSONValidador
import org.json.JSONObject

class Desprendible {

    var nominaDesprendible: String? = ""

    var anioDesprendible: String? = ""

    var mesDesprendible: String? = ""

    var subPeriodoDesprendible: String? = ""

    constructor(json: JSONObject) {
        try {
            val validador = JSONValidador()

            this.nominaDesprendible = json.getString("nominaFuncionarioId")
            this.anioDesprendible = json.getString("anio")
            this.mesDesprendible = validador.campoNulo(json.getString("mes"))
            this.subPeriodoDesprendible = validador.campoNulo(json.getString("subperiodo"))

        } catch (e: Exception) {
            Log.e("error", e.message!!)
        }
    }

    constructor()
}