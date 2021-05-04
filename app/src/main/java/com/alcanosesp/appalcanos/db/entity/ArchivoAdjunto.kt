package com.alcanosesp.appalcanos.db.entity

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alcanosesp.appalcanos.utils.JSONValidador
import org.json.JSONObject
import java.lang.Exception

@Entity(tableName = "ArchivoAdjunto")
class ArchivoAdjunto {
    @PrimaryKey
    @ColumnInfo(name = "Archivo")
    var archivo: String = ""

    @ColumnInfo(name = "ObjectId")
    var objectId: String = ""

    @ColumnInfo(name = "Etiqueta")
    var etiqueta: String = ""

    constructor(json: JSONObject){
        var  valiador =JSONValidador()
        try {
            this.archivo = json.getString("archivo")
            this.objectId = json.getString("objectId")
            this.etiqueta = valiador.campoNulo( json.getString("etiqueta"))
        }catch (e :Exception){
            Log.i("archivoAdjunto", e.message)
        }
    }
    constructor()
}