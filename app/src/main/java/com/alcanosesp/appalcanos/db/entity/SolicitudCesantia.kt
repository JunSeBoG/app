package com.alcanosesp.appalcanos.db.entity

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alcanosesp.appalcanos.utils.JSONValidador
import com.alcanosesp.appalcanos.utils.estadosBeneficiosNombres
import org.json.JSONObject
import java.lang.Exception
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import kotlin.math.round

@Entity(tableName = "SolicitudCesantia")
class SolicitudCesantia {
    @PrimaryKey
    @ColumnInfo(name = "Id")
    var id: Int? = null

    @ColumnInfo(name = "MotivoSolicitudCesantiaId")
    var motivoSolicitudCesantiaId: Int? = null

    @ColumnInfo(name = "MotivoSolicitudCesantiaNombre")
    var motivoSolicitudCesantiaNombre: String = ""

    @ColumnInfo(name = "BaseCalculoCesantia")
    var baseCalculoCesantia: String = ""

    @ColumnInfo(name = "ValorSolicitado")
    var valorSolicitado: String = ""

    @ColumnInfo(name = "Soporte")
    var soporte: String = ""

    @ColumnInfo(name = "Observacion")
    var observacion: String = ""

    @ColumnInfo(name = "RespuestaAlaSolicitud")
    var respuestaAlaSolicitud: String = ""

    @ColumnInfo(name = "Estado")
    var estado: String = ""

    @ColumnInfo(name = "FechaSolicitud")
    var fechaSolicitud: String = ""

    @ColumnInfo(name = "Posicion")
    var posicion: String? = ""

    constructor(json: JSONObject) {
        try {
            val validador = JSONValidador()

            this.id = json.getInt("id")
            this.motivoSolicitudCesantiaId = validador.jsonNuloPrimerGrado(json,"motivoSolicitudCesantia","id").toInt()
            this.motivoSolicitudCesantiaNombre = validador.jsonNuloPrimerGrado(json,"motivoSolicitudCesantia","nombre")
            this.baseCalculoCesantia = validador.campoNulonumerico(json.getString("baseCalculoCesantia"))
            this.valorSolicitado = validador.redonder (validador.campoNuloMoneda(json.getString("valorSolicitado")) ).toString()
            this.soporte = validador.campoNulo(json.getString("soporte"))
            this.estado = validador.campoNulo(json.getString("estado"))
            this.observacion = validador.campoNulo(json.getString("observacion"))
            this.fechaSolicitud = validador.campoFechaHora(json.getString("fechaSolicitud"))
            this.respuestaAlaSolicitud = validador.campoNulo(json.getString("justificacion"))

        }catch (e :Exception){
            Log.e("EntitySolicitudCesantia",e.message.toString())
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

    fun estado(s: String): String{
        return estadosBeneficiosNombres[s]!!
    }

    fun moneda(s: String?): String{
        val a = s
        return if (s == "0" || s == "" || s == null){
            "0.00"
        }else{
            val symbols = DecimalFormatSymbols().apply {
                groupingSeparator = '.'
                decimalSeparator = ','
            }

            val decimalFormat = DecimalFormat("#,###.##", symbols)

            val redondear2deciamles = round(s.toDouble()* 100)/100

            decimalFormat.format(redondear2deciamles)
        }
    }
}