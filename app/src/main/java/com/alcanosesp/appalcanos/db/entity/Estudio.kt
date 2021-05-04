package com.alcanosesp.appalcanos.db.entity

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alcanosesp.appalcanos.utils.JSONValidador
import org.json.JSONObject

@Entity(tableName = "Estudio")
class Estudio {

    @PrimaryKey
    @ColumnInfo(name = "Id")
    var id: Int? = null

    @ColumnInfo(name = "NivelEducativoId")
    var nivelEducativoId: String? = ""

    @ColumnInfo(name = "NivelEducativoNombre")
    var nivelEducativoNombre: String? = ""

    @ColumnInfo(name = "Institucion")
    var institucion: String? = ""

    @ColumnInfo(name = "PaisId")
    var paisId: String? = ""

    @ColumnInfo(name = "PaisNombre")
    var paisNombre: String? = ""

    @ColumnInfo(name = "ProfesionId")
    var profesionId: String? = ""

    @ColumnInfo(name = "ProfesionNombre")
    var profesionNombre: String? = ""

    @ColumnInfo(name = "FechaInicio")
    var fechaInicio: String? = ""

    @ColumnInfo(name = "FechaFin")
    var fechaFin: String? = ""

    @ColumnInfo(name = "EstadoEstudio")
    var estadoEstudio: String? = ""

    @ColumnInfo(name = "TarjetaProfesional")
    var tarjetaProfesional: String? = ""

    @ColumnInfo(name = "Titulo")
    var titulo: String? = ""

    @ColumnInfo(name = "Observacion")
    var observacion: String? = ""

    @ColumnInfo(name = "Justificacion")
    var justificacion: String? = ""

    @ColumnInfo(name = "Estado")
    var estado: String? = ""

    constructor(json: JSONObject) {
        try {
            val validador = JSONValidador()

            this.id = json.getInt("id")
            this.nivelEducativoId = validador.campoNulonumerico(json.getString("nivelEducativoId"))
            this.nivelEducativoNombre = validador.jsonNuloPrimerGrado(json, "nivelEducativo", "nombre")
            this.institucion = validador.campoNulo(json.getString("institucionEducativa"))
            this.paisId = validador.campoNulonumerico(json.getString("paisId"))
            this.paisNombre = validador.jsonNuloPrimerGrado(json, "pais", "nombre")
            this.profesionId = validador.campoNulonumerico(json.getString("profesionId"))
            this.profesionNombre = validador.jsonNuloPrimerGrado(json, "profesion", "nombre")
            this.fechaInicio = validador.campoNulo(json.getString("anioDeInicio"))
            this.fechaFin = validador.campoNulo(json.getString("anioDeFin"))
            this.estadoEstudio = validador.campoNulo(json.getString("estadoEstudio"))
            this.tarjetaProfesional = validador.campoNulo(json.getString("tarjetaProfesional"))
            this.titulo = validador.campoNulo(json.getString("titulo"))
            this.observacion = validador.campoNulo(json.getString("observacion"))
            this.estado = validador.campoNulo(json.getString("estado"))
            this.justificacion = validador.campoNulo(json.getString("justificacion"))

        } catch (e: Exception) {
            Log.e("error", e.message!!)
        }
    }

    constructor()

    fun substring(max: Int): String {
        val validador = JSONValidador()
        return validador.campoLargo(nivelEducativoNombre!!, max)
    }

    fun campoVacio(s: String): String{
        return if (s.isEmpty()){
            "N/A"
        }else{
            s
        }
    }
}

