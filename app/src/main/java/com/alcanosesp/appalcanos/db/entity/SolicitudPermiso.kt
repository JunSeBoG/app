package com.alcanosesp.appalcanos.db.entity

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alcanosesp.appalcanos.utils.JSONValidador
import org.json.JSONObject

@Entity(tableName = "Permiso")
class SolicitudPermiso {
    @PrimaryKey
    @ColumnInfo(name = "Id")
    var id: Int? = null

    @ColumnInfo(name = "FechaInicio")
    var fechaInicio: String = ""

    @ColumnInfo(name = "FechaFin")
    var fechaFin: String = ""

    @ColumnInfo(name = "HoraSalida")
    var horaSalida: String = ""

    @ColumnInfo(name = "HoraLlegada")
    var horaLlegada: String = ""

    @ColumnInfo(name = "Observaciones")
    var observaciones: String = ""

    @ColumnInfo(name = "Estado")
    var estado: String = ""

    @ColumnInfo(name = "Justificacion")
    var justificacion: String = ""

    @ColumnInfo(name = "TipoAusentismoId")
    var tipoAusentismoId: Int? = null

    @ColumnInfo(name = "TipoAusentismoCodigo")
    var tipoAusentismoCodigo: String = ""

    @ColumnInfo(name = "TipoAusentismoNombre")
    var tipoAusentismoNombre: String = ""

    @ColumnInfo(name = "ClaseAusentismoId")
    var claseAusentismoId: Int? = null

    @ColumnInfo(name = "ClaseAusentismoCodigo")
    var claseAusentismoCodigo: String = ""

    @ColumnInfo(name = "ClaseAusentismoNombre")
    var claseAusentismoNombre: String = ""

    @ColumnInfo(name = "Posicion")
    var posicion: String? = ""

    constructor(json: JSONObject, posicion: String) {
        try {
            val validador = JSONValidador()
            this.posicion = posicion

            this.id = json.getInt("id")
            this.fechaInicio = json.getString("fechaInicio")
            this.fechaFin = json.getString("fechaFin")
            this.horaSalida = validador.campoHora( json.getString("horaSalida"))
            this.horaLlegada =validador.campoHora( json.getString("horaLlegada"))
            this.observaciones = validador.campoNulo( json.getString("observaciones"))
            this.estado = json.getString("estado")
            this.justificacion = validador.campoNulo(json.getString("justificacion"))
            this.tipoAusentismoId =
                validador.jsonNuloPrimerGrado(json, "tipoAusentismo", "id").toInt()
            this.tipoAusentismoCodigo =
                validador.jsonNuloPrimerGrado(json, "tipoAusentismo", "codigo")
            this.tipoAusentismoNombre =
                validador.jsonNuloPrimerGrado(json, "tipoAusentismo", "nombre")
            this.claseAusentismoId = validador.jsonNuloSegundoGrado(
                json,
                "tipoAusentismo",
                "claseAusentismo",
                "id"
            ).toInt()
            this.claseAusentismoCodigo = validador.jsonNuloSegundoGrado(
                json,
                "tipoAusentismo",
                "claseAusentismo",
                "codigo"
            )
            this.claseAusentismoNombre = validador.jsonNuloSegundoGrado(
                json,
                "tipoAusentismo",
                "claseAusentismo",
                "nombre"
            )

        } catch (e: Exception) {
            Log.e("EntitySolicitudPermiso", e.message!!)
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