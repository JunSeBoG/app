package com.alcanosesp.appalcanos.utils

import org.json.JSONObject
import java.util.*
import kotlin.math.round

class JSONValidador {

    fun campoNulo(valor: String): String {
        return if (verificarCampoNulo(valor)) "" else valor
    }

    fun campoNulonumerico(valor: String): String {
        return if (verificarCampoNulo(valor)) "0" else valor
    }

    fun campoFechaHora(valor: String): String {
        return if (verificarCampoNulo(valor)) {
            ""
        } else {
            valor.substring(0, 10)
        }
    }

    fun campoHora(valor: String): String {
        return if (verificarCampoNulo(valor)) {
            ""
        } else {
            valor.substring(0, 5)
        }
    }

    fun campoBoolean(valor: String): String {
        return if (verificarCampoNulo(valor)) {
            ""
        } else {
            if (valor == "true") "Si" else "No"
        }
    }

    fun calcularEdad(fechaNacimiento: String): Int {
        return obtenerEdad(
            fechaNacimiento.substring(0, 4).toInt(),
            fechaNacimiento.substring(5, 7).toInt(),
            fechaNacimiento.substring(8, 10).toInt()
        )
    }

    fun campoLargo(valor: String, max: Int): String {
        return if (verificarCampoNulo(valor)) {
            ""
        } else {
            if (verificarCampoLargo(valor, max)) {
                valor
            } else {
                valor.substring(0, max) + "..."
            }
        }
    }

    fun jsonNuloPrimerGrado(json: JSONObject, valor: String, key: String): String {
        return if (verificarJSONObjectNulo(json, valor)) {
            ""
        } else {
            return json.getJSONObject(valor).getString(key)
        }
    }

    fun jsonNuloSegundoGrado(
        json: JSONObject,
        valor1: String,
        valor2: String,
        key: String
    ): String {
        return if (verificarJSONObjectNulo(json, valor1)) {
            ""
        } else {
            if (verificarJSONObjectNulo(json.getJSONObject(valor1), valor2)) {
                ""
            } else {
                json.getJSONObject(valor1).getJSONObject(valor2).getString(key)
            }
        }
    }

    fun jsonNuloTercerGrado(
        json: JSONObject,
        valor1: String,
        valor2: String,
        valor3: String,
        key: String
    ): String {
        return if (verificarJSONObjectNulo(json, valor1)) {
            ""
        } else {
            if (verificarJSONObjectNulo(json.getJSONObject(valor1), valor2)) {
                ""
            } else {
                if (verificarJSONObjectNulo(
                        json.getJSONObject(valor1).getJSONObject(valor2),
                        valor3
                    )
                ) {
                    ""
                } else {
                    json.getJSONObject(valor1).getJSONObject(valor2).getJSONObject(valor3)
                        .getString(key)
                }

            }
        }
    }

    private fun verificarCampoLargo(valor: String, max: Int): Boolean {
        return valor.length <= max
    }

    private fun verificarJSONObjectNulo(json: JSONObject, valor: String): Boolean {
        return json.isNull(valor)
    }

    private fun verificarCampoNulo(valor: String): Boolean {
        return valor == "null"
    }

    private fun obtenerEdad(año: Int, mes: Int, dia: Int): Int {

        var edad: Int

        val calendario: Calendar = Calendar.getInstance()

        val anoActual: Int = calendario.get(Calendar.YEAR)
        val mesActual: Int = 1 + calendario.get(Calendar.MONTH)
        val diaActual: Int = calendario.get(Calendar.DAY_OF_MONTH)

        edad = anoActual - año

        if (mes > mesActual) {
            --edad
        } else if (mes == mesActual) {
            if (dia > diaActual) {
                --edad
            }
        }

        return edad
    }

    fun obtenerCargo(contrato: String?, contratoOtroSi: String?): String {
        var cargoNombre = ""
        var json = JSONObject()
        if (contratoOtroSi != "null") {
            json = JSONObject(contratoOtroSi)
        } else if (contrato != "null") {
            json = JSONObject(contrato)
        } else {
            return cargoNombre
        }

        val cargoDependencia: String? = json.getString("cargoDependencia")
        if (!cargoDependencia.isNullOrEmpty()) {
            val cargo = JSONObject(cargoDependencia).getString("cargo")
            if (!cargo.isNullOrEmpty()) {
                cargoNombre = JSONObject(cargo).getString("nombre")
            }
        }

        return cargoNombre
    }

    fun campoNuloMoneda(valor: String): String {
        return if  (verificarCampoNulo(valor)) "00" else valor
    }

    fun redonder(s :String):Double{
        return round(s.toDouble()* 100) /100
    }
}