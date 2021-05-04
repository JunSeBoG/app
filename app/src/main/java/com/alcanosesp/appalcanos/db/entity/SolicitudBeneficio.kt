package com.alcanosesp.appalcanos.db.entity

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alcanosesp.appalcanos.utils.JSONValidador
import com.alcanosesp.appalcanos.utils.estadosBeneficiosNombres
import com.alcanosesp.appalcanos.utils.opcionAxulioEducativoNombres
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import kotlin.math.round

@Entity(tableName = "SolicitudBeneficio")
class SolicitudBeneficio {

    @PrimaryKey
    @ColumnInfo(name = "Id")
    var id: Int? = null

    @ColumnInfo(name = "TipoBeneficioId")
    var tipoBeneficioId: String? = ""

    @ColumnInfo(name = "TipoBeneficioNombre")
    var tipoBeneficioNombre: String? = ""

    @ColumnInfo(name = "FechaSolicitud")
    var fechaSolicitud: String? = ""

    @ColumnInfo(name = "ValorSolicitud")
    var valorSolicitud: String? = ""

    @ColumnInfo(name = "PlazoMaximo")
    var plazoMaximo: String? = ""

    @ColumnInfo(name = "TipoPeriodoId")
    var tipoPeriodoId: String? = ""

    @ColumnInfo(name = "TipoPeriodoNombre")
    var tipoPeriodoNombre: String? = ""

    @ColumnInfo(name = "OpcionAuxilioEducativo")
    var opcionAuxilioEducativo: String? = ""

    @ColumnInfo(name = "CantidadHoraSemana")
    var cantidadHoraSemana: String? = ""

    @ColumnInfo(name = "FechaInicioEstudio")
    var fechaInicioEstudio: String? = ""

    @ColumnInfo(name = "FechaFinalizacionEstudio")
    var fechaFinalizacionEstudio: String? = ""

    @ColumnInfo(name = "Observacion")
    var observacion: String? = ""

    @ColumnInfo(name = "Estado")
    var estado: String? = ""

    @ColumnInfo(name = "ObservacionAprobacion")
    var observacionAprobacion: String? = ""

    @ColumnInfo(name = "ObservacionAutorizacion")
    var observacionAutorizacion: String? = ""

    @ColumnInfo(name = "ValorAutorizado")
    var valorAutorizado: String? = ""

    @ColumnInfo(name = "ValorCobrar")
    var valorCobrar: String? = ""

    @ColumnInfo(name = "NotaAcademica")
    var notaAcademica: String? = ""

    @ColumnInfo(name = "Saldo")
    var saldo: String? = ""

    @ColumnInfo(name = "ValorSolicitado")
    var permiteValorSolicitado: String? = ""

    @ColumnInfo(name = "PlazoMes")
    var permitePlazoMes: String? = ""

    @ColumnInfo(name = "PeriodoPago")
    var permitePeriodoPago: String? = ""

    @ColumnInfo(name = "PermiteAuxilioEducativo")
    var permiteAuxilioEducativo: String? = ""

    @ColumnInfo(name = "PermiteDescuentoNomina")
    var permiteDescuentoNomina: String? = ""
    /*
          //"id":1058,
          //"funcionarioId":691,
          //"tipoBeneficioId":1,
          //"fechaSolicitud":"2020-05-05",
          //"valorSolicitud":200000.0,
          //"plazoMaximo":null,
          //"tipoPeriodoId":2,
          //"opcionAuxilioEducativo":null,
          //"cantidadHoraSemana":null,
          //"fechaInicioEstudio":null,
          //"fechaFinalizacionEstudio":null,
          //"observacion":null,
          //"estado":"EnTramite",
          //"observacionAprobacion":null,
          //"observacionAutorizacion":null,
          //"valorAutorizado":null,
          //"valorCobrar":null,
          //"notaAcademica":null,
          //"saldo":0.0,
          "tipoBeneficio":{
             "id":1,
             "nombre":"Auxilio optico"
             "conceptoNominaDevengoId": 3,
                "conceptoNominaDeduccionId": null,
                "conceptoNominaCalculoId": null,
                "requiereAprobacionJefe": true,
                "montoMaximo": 0,
                "valorSolicitado": false,
                "plazoMes": false,
                "cuotaPermitida": 8,
                "periodoPago": false,
                "permiteAuxilioEducativo": false,
                "permiteDescuentoNomina": false,
                "valorAutorizado": false,
                "diasAntiguedad": 3,
                "vecesAnio": 5,
                "descripcion": "Préstamo educativo condonable",
                "estadoRegistro": "Activo",
                "creadoPor": null,
                "fechaCreacion": null,
                "modificadoPor": "null",
                "fechaModificacion": "2020-04-07T09:05:00-05:00",
                "eliminadoPor": null,
                "fechaEliminacion": null
          },
          "tipoPeriodo":{
             "id":2,
             "nombre":"Mensual"
          }
         */

    constructor(json: JSONObject) {
        try {
            val validador = JSONValidador()

            this.id = json.getInt("id")
            this.tipoBeneficioId = validador.campoNulonumerico(json.getString("tipoBeneficioId"))
            this.tipoBeneficioNombre = validador.jsonNuloPrimerGrado(json, "tipoBeneficio", "nombre")
            this.fechaSolicitud = validador.campoNulo(json.getString("fechaSolicitud"))
            this.valorSolicitud = validador.campoNulonumerico(json.getString("valorSolicitud"))
            this.plazoMaximo = validador.campoNulonumerico(json.getString("plazoMaximo"))
            this.tipoPeriodoId = validador.campoNulonumerico(json.getString("tipoPeriodoId"))
            this.tipoPeriodoNombre = validador.jsonNuloPrimerGrado(json, "tipoPeriodo", "nombre")
            this.opcionAuxilioEducativo = validador.campoNulo(json.getString("opcionAuxilioEducativo"))
            this.cantidadHoraSemana = validador.campoNulonumerico(json.getString("cantidadHoraSemana"))
            this.fechaInicioEstudio = validador.campoNulo(json.getString("fechaInicioEstudio"))
            this.fechaFinalizacionEstudio = validador.campoNulo(json.getString("fechaFinalizacionEstudio"))
            this.observacion = validador.campoNulo(json.getString("observacion"))
            this.estado = validador.campoNulo(json.getString("estado"))
            this.observacionAprobacion = validador.campoNulo(json.getString("observacionAprobacion"))
            this.observacionAutorizacion = validador.campoNulo(json.getString("observacionAutorizacion"))
            this.valorAutorizado = validador.campoNulonumerico(json.getString("valorAutorizado"))
            this.valorCobrar = validador.campoNulonumerico(json.getString("valorCobrar"))
            this.notaAcademica = validador.campoNulonumerico(json.getString("notaAcademica"))
            this.saldo = validador.campoNulonumerico(json.getString("saldo"))

            this.permiteValorSolicitado = validador.jsonNuloPrimerGrado(json, "tipoBeneficio", "valorSolicitado")
            this.permitePlazoMes = validador.jsonNuloPrimerGrado(json, "tipoBeneficio", "plazoMes")
            this.permitePeriodoPago = validador.jsonNuloPrimerGrado(json, "tipoBeneficio", "periodoPago")
            this.permiteAuxilioEducativo = validador.jsonNuloPrimerGrado(json, "tipoBeneficio", "permiteAuxilioEducativo")
            this.permiteDescuentoNomina = validador.jsonNuloPrimerGrado(json, "tipoBeneficio", "permiteDescuentoNomina")

        } catch (e: Exception) {
            Log.e("error", e.message!!)
        }
    }

    constructor()

    fun campoVacio(s: String): String{
        return if (s.isEmpty() || s == "0"){
            "N/A"
        }else{
            s
        }
    }

    fun estado(s: String): String{
        return estadosBeneficiosNombres[s]!!
    }

    fun opcionAuxilio(s: String): String{
        return opcionAxulioEducativoNombres[s]!!
    }

    fun moneda(s: String): String{
        return if (s == "0"){
            "0.00"
        }else{
            val symbols = DecimalFormatSymbols().apply {
                groupingSeparator = ','
                decimalSeparator = '.'
            }

            val decimalFormat = DecimalFormat("#,###.00", symbols)
            val redondear2deciamles = round(s.toDouble()* 100) /100

            decimalFormat.format(redondear2deciamles)
        }
    }

    fun aEnt(s: String?): Int{
        return s?.toInt() ?: 0
    }

    fun valoresAutorizados(s: String?): String{
        return if (campoVacio(s!!) == "N/A"){
            campoVacio(s!!)
        }else{
           // "$ ".plus(moneda(s))
            moneda(s)
        }
    }
}