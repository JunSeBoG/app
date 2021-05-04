package com.alcanosesp.appalcanos.db.entity

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alcanosesp.appalcanos.utils.JSONValidador
import com.alcanosesp.appalcanos.utils.estadosBeneficiosNombres
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import kotlin.math.round

@Entity(tableName = "SolicitudVacaciones")
class SolicitudVacaciones {
    @PrimaryKey
    @ColumnInfo(name = "Id")
    var id: Int? = null

    @ColumnInfo(name = "FechaInicioDisfrute")
    var fechaInicioDisfrute: String = ""

    @ColumnInfo(name = "DiasDisfrute")
    var diasDisfrute: String = ""

    @ColumnInfo(name = "FechaFinDisfrute")
    var fechaFinDisfrute: String = ""

    @ColumnInfo(name = "DiasDinero")
    var diasDinero: String = ""

    @ColumnInfo(name = "Observacion")
    var observacion: String = ""

    @ColumnInfo(name = "Estado")
    var estado: String = ""

    @ColumnInfo(name = "Justificacion")
    var justificacion: String = ""

    @ColumnInfo(name = "LibroVacacionesId")
    var libroVacacionesId: String = ""

    @ColumnInfo(name = "InicioCausacion")
    var inicioCausacion: String = ""

    @ColumnInfo(name = "FinCausacion")
    var finCausacion: String = ""

    @ColumnInfo(name = "TipoPeriodo")
    var tipoPeriodo: String = ""

    @ColumnInfo(name = "DiasTrabajados")
    var diasTrabajados: String = ""

    @ColumnInfo(name = "DiasDisponibles")
    var diasDisponibles: String = ""

    constructor(json: JSONObject) {
        try {

            val validador = JSONValidador()
            this.id = json.getInt("id")
            this.fechaInicioDisfrute =
                validador.campoFechaHora(json.getString("fechaInicioDisfrute"))
            this.diasDisfrute = validador.campoNulo(json.getString("diasDisfrute"))
            this.fechaFinDisfrute = validador.campoFechaHora(json.getString("fechaFinDisfrute"))
            this.diasDinero = validador.campoNulo(json.getString("diasDinero"))
            this.observacion = validador.campoNulo(json.getString("observacion"))
            this.estado = validador.campoNulo(json.getString("estado"))
            this.justificacion = validador.campoNulo(json.getString("justificacion"))
            this.libroVacacionesId = validador.jsonNuloPrimerGrado(json,"libroVacaciones","id")
            this.inicioCausacion = validador.jsonNuloPrimerGrado(json,"libroVacaciones","inicioCausacion")
            this.finCausacion = validador.jsonNuloPrimerGrado(json,"libroVacaciones","finCausacion")
            this.tipoPeriodo = validador.jsonNuloPrimerGrado(json,"libroVacaciones","tipo")
            this.diasTrabajados = validador.jsonNuloPrimerGrado(json,"libroVacaciones","diasTrabajados")
            this.diasDisponibles = validador.jsonNuloPrimerGrado(json,"libroVacaciones","diasDisponibles")

        } catch (e: Exception) {
            Log.e("EntitySolicitudVacacion", e.message.toString())
        }
    }

    constructor(json: JSONObject, crear:String) {
        try {
            this.libroVacacionesId = json.getString("id")
            this.inicioCausacion = json.getString("inicioCausacion")
            this.finCausacion = json.getString("finCausacion")
            this.tipoPeriodo = json.getString("tipo")
            this.diasTrabajados = json.getString("diasTrabajados")
            this.diasDisponibles = json.getString("diasDisponibles")

        } catch (e: Exception) {
            Log.e("EntitySolicitudVacacion", e.message.toString())
        }
    }

    constructor()

    fun campoVacio(s: String): String {
        return if (s.isEmpty()) {
            "N/A"
        } else {
            s
        }
    }

    fun estado(s: String): String {
        return estadosBeneficiosNombres[s]!!
    }

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
