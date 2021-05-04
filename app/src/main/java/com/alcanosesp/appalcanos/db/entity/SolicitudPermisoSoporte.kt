package com.alcanosesp.appalcanos.db.entity

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alcanosesp.appalcanos.utils.JSONValidador
import org.json.JSONObject

@Entity(tableName = "PermisoSoporte")
class SolicitudPermisoSoporte {
    @PrimaryKey
    @ColumnInfo(name = "Id")
    var id: Int? = null

    @ColumnInfo(name = "SolicitudPermisoId")
    var solicitudPermisoId: Int? = null

    @ColumnInfo(name = "Comentario")
    var comentario: String = ""

    @ColumnInfo(name = "Adjunto")
    var adjunto: String = ""

    @ColumnInfo(name = "TipoSoporteId")
    var tipoSoporteId: String = ""

    @ColumnInfo(name = "TipoSoporteNombre")
    var tipoSoporteNombre: String = ""

    constructor(json: JSONObject, posicion: String) {
        try {
            val validador = JSONValidador()
            this.id = json.getInt("id")
            this.solicitudPermisoId = json.getInt("solicitudPermisoId")
            this.comentario = json.getString("comentario")
            this.adjunto = json.getString("adjunto")
            this.tipoSoporteId = validador.jsonNuloPrimerGrado(json, "tipoSoporte", "id")
            this.tipoSoporteNombre = validador.jsonNuloPrimerGrado(json, "tipoSoporte", "nombre")

        } catch (e: Exception) {
            Log.e("EntityPermisosSoporte: ", e.message!!)
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