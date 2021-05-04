package com.alcanosesp.appalcanos.db.entity

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alcanosesp.appalcanos.utils.JSONValidador
import org.json.JSONObject

@Entity(tableName = "Experiencia")
class Experiencia {

    @PrimaryKey
    @ColumnInfo(name = "Id")
    var id: Int? = null

    @ColumnInfo(name = "Cargo")
    var cargo: String = ""

    @ColumnInfo(name = "Empresa")
    var empresa: String? = ""

    @ColumnInfo(name = "Telefono")
    var telefono: String? = ""

    @ColumnInfo(name = "Salario")
    var salario: String? = ""

    @ColumnInfo(name = "JefeInmediato")
    var jefeInmediato: String? = ""

    @ColumnInfo(name = "FechaInicio")
    var fechaInicio: String? = ""

    @ColumnInfo(name = "FechaFin")
    var fechaFin: String? = ""

    @ColumnInfo(name = "Funciones")
    var funciones: String? = ""

    @ColumnInfo(name = "TrabajaActualmente")
    var trabajaActualmente: String? = ""

    @ColumnInfo(name = "MotivoRetiro")
    var motivoRetiro: String? = ""

    @ColumnInfo(name = "Observaciones")
    var observaciones: String? = ""

    @ColumnInfo(name = "Justificacion")
    var justificacion: String? = ""

    @ColumnInfo(name = "Estado")
    var estado: String? = ""

    constructor(json: JSONObject) {
        try {
            val validador = JSONValidador()

            this.id = json.getInt("id")
            this.cargo = validador.campoNulo(json.getString("nombreCargo"))
            this.empresa = validador.campoNulo(json.getString("nombreEmpresa"))
            this.telefono = validador.campoNulo(json.getString("telefono"))
            this.salario = validador.campoNulo(json.getString("salario"))
            this.jefeInmediato = validador.campoNulo(json.getString("nombreJefeInmediato"))
            this.fechaInicio = validador.campoFechaHora(json.getString("fechaInicio"))
            this.fechaFin = validador.campoFechaHora(json.getString("fechaFin"))
            this.funciones = validador.campoNulo(json.getString("funcionesCargo"))
            this.trabajaActualmente = validador.campoBoolean(json.getString("trabajaActualmente"))
            this.motivoRetiro = validador.campoNulo(json.getString("motivoRetiro"))
            this.observaciones = validador.campoNulo(json.getString("observaciones"))
            this.estado = validador.campoNulo(json.getString("estado"))
            this.justificacion = validador.campoNulo(json.getString("justificacion"))

        } catch (e: Exception) {
            Log.e("error", e.message!!)
        }
    }

    constructor()

    fun campoVacio(s: String): String{
        return if (s.isEmpty()){
            "N/A"
        }else{
            s
        }
    }
}



