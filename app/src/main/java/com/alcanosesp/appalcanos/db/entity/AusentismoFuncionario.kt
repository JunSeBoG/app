package com.alcanosesp.appalcanos.db.entity

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alcanosesp.appalcanos.utils.JSONValidador
import org.json.JSONObject

@Entity(tableName = "AusentismoFuncionario")
class AusentismoFuncionario {
    @PrimaryKey
    @ColumnInfo(name = "Id")
    var id: Int? = null

    @ColumnInfo(name = "FuncionarioId")
    var funcionarioId: Int? = null

    @ColumnInfo(name = "FechaInicio")
    var fechaInicio: String = ""

    @ColumnInfo(name = "FechaFin")
    var fechaFin: String = ""

    @ColumnInfo(name = "HoraInicio")
    var horaInicio: String = ""

    @ColumnInfo(name = "HoraFin")
    var horaFin: String = ""

    @ColumnInfo(name = "NumeroIncapacidad")
    var numeroIncapacidad: String = ""

    @ColumnInfo(name = "Adjunto")
    var adjunto: String = ""

    @ColumnInfo(name = "Estado")
    var estado: String = ""

    @ColumnInfo(name = "Justificacion")
    var justificacion: String = ""

    @ColumnInfo(name = "EstadoRegistro")
    var estadoRegistro: String = ""

    @ColumnInfo(name = "TipoAusentismoId")
    var tipoAusentismoId: Int? = null

    @ColumnInfo(name = "TipoAusentismoCodigo")
    var tipoAusentismoCodigo: String = ""

    @ColumnInfo(name = "TipoAusentismoNombre")
    var tipoAusentismoNombre: String = ""

    @ColumnInfo(name = "TipoAusentismoUnidadTiempo")
    var tipoAusentismoUnidadTiempo: String = ""

    @ColumnInfo(name = "ClaseAusentismoId")
    var claseAusentismoId: Int? = null

    @ColumnInfo(name = "ClaseAusentismoCodigo")
    var claseAusentismoCodigo: String = ""

    @ColumnInfo(name = "ClaseAusentismoNombre")
    var claseAusentismoNombre: String = ""

    @ColumnInfo(name = "DiagnosticoCieId")
    var diagnosticoCieId: Int? = null

    @ColumnInfo(name = "DiagnosticoCieCodigo")
    var diagnosticoCieCodigo: String = ""

    @ColumnInfo(name = "DiagnosticoCieNombre")
    var diagnosticoCieNombre: String = ""

    @ColumnInfo(name = "ProrrogaDe")
    var prorrogaDe: String = ""

    @ColumnInfo(name = "Posicion")
    var posicion: String? = ""

    constructor(json: JSONObject, posicion: String) {
        try {
            val validador = JSONValidador()

            this.posicion = posicion

            this.id = json.getInt("id")
            this.funcionarioId = json.getInt("funcionarioId")
            this.fechaInicio = validador.campoFechaHora(json.getString("fechaInicio"))
            this.fechaFin = validador.campoFechaHora(json.getString("fechaFin"))
            this.horaInicio = validador.campoFechaHora(json.getString("horaInicio"))
            this.horaFin = validador.campoFechaHora(json.getString("horaFin"))
            this.numeroIncapacidad = validador.campoNulo(json.getString("numeroIncapacidad"))
            this.adjunto = validador.campoNulo(json.getString("adjunto"))
            this.estado = validador.campoNulo(json.getString("estado"))
            this.justificacion = validador.campoNulo(json.getString("justificacion"))
            this.estadoRegistro = validador.campoNulo(json.getString("estadoRegistro"))
            this.tipoAusentismoId = json.getInt("tipoAusentismoId")
            this.tipoAusentismoCodigo = validador.jsonNuloPrimerGrado(json, "tipoAusentismo", "codigo")
            this.tipoAusentismoNombre = validador.jsonNuloPrimerGrado(json, "tipoAusentismo", "nombre")
            this.tipoAusentismoUnidadTiempo = validador.jsonNuloPrimerGrado(json, "tipoAusentismo", "unidadTiempo")
            this.claseAusentismoId = validador.jsonNuloPrimerGrado(json, "tipoAusentismo", "claseAusentismoId").toInt()
            this.claseAusentismoCodigo =validador.jsonNuloSegundoGrado(json, "tipoAusentismo", "claseAusentismo", "codigo")
            this.claseAusentismoNombre = validador.jsonNuloSegundoGrado(json, "tipoAusentismo", "claseAusentismo", "nombre")
            this.diagnosticoCieId = json.getInt("diagnosticoCieId")
            this.diagnosticoCieCodigo = validador.jsonNuloPrimerGrado(json, "diagnosticoCie", "codigo")
            this.diagnosticoCieNombre = validador.jsonNuloPrimerGrado(json, "diagnosticoCie", "nombre")
            this.prorrogaDe = ""


        } catch (e: Exception) {
            Log.e("EntityAusentismo: ", e.message!!)
        }
    }

    constructor()
}