package com.alcanosesp.appalcanos.db.entity

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alcanosesp.appalcanos.utils.JSONValidador
import org.json.JSONObject

@Entity(tableName = "Token")
class Token {

    @ColumnInfo(name = "Token")
    var token: String? = ""

    @ColumnInfo(name = "RefreshToken")
    var refreshToken: String? = ""

    @PrimaryKey
    @ColumnInfo(name = "Cedula")
    var cedula: String = ""

    @ColumnInfo(name = "Usuario")
    var usuario: String? = ""

    @ColumnInfo(name = "Vencimiento")
    var vencimiento: String? = ""

    @ColumnInfo(name = "Aplicaciones")
    var aplicaciones: String? = ""

    @ColumnInfo(name = "Error")
    var error: String? = ""

    @ColumnInfo(name = "Adjunto")
    var adjunto: String? = ""


    constructor(json: JSONObject) {
        try {
            this.token = json.getString("token")
            this.refreshToken = json.getString("refreshToken")
            this.cedula = json.getString("cedula")
            this.usuario = json.getString("usuario")
            this.vencimiento = json.getString("vencimiento")
            this.aplicaciones = json.getString("aplicaciones")
            this.error = json.getString("error")
            this.adjunto = json.getString("adjunto")
        } catch (e: Exception) {
            Log.e("errorTokenModelo", e.message!!)
        }
    }

    constructor()
}