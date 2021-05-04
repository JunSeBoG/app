package com.alcanosesp.appalcanos.db.entity

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alcanosesp.appalcanos.utils.JSONValidador
import org.json.JSONObject

@Entity(tableName = "Familiar")
class Familiar {

    @PrimaryKey
    @ColumnInfo(name = "Id")
    var id: Int? = null

    @ColumnInfo(name = "PrimerNombre")
    var primerNombre: String? = ""

    @ColumnInfo(name = "SegundoNombre")
    var segundoNombre: String? = ""

    @ColumnInfo(name = "PrimerApellido")
    var primerApellido: String? = ""

    @ColumnInfo(name = "SegundoApellido")
    var segundoApellido: String? = ""

    @ColumnInfo(name = "SexoId")
    var sexoId: String? = ""

    @ColumnInfo(name = "SexoNombre")
    var sexoNombre: String? = ""

    @ColumnInfo(name = "ParentescoId")
    var parentescoId: String? = ""

    @ColumnInfo(name = "ParentescoNombre")
    var parentescoNombre: String? = ""

    @ColumnInfo(name = "Dependiente")
    var dependiente: String? = ""

    @ColumnInfo(name = "FechaNacimiento")
    var fechaNacimiento: String? = ""

    @ColumnInfo(name = "TipoDocumentoId")
    var tipoDocumentoId: String? = ""

    @ColumnInfo(name = "TipoDocumentoNombre")
    var tipoDocumentoNombre: String? = ""

    @ColumnInfo(name = "NumeroDocumento")
    var numeroDocumento: String? = ""

    @ColumnInfo(name = "NivelEducativoId")
    var nivelEducativoId: String? = ""

    @ColumnInfo(name = "NivelEducativoNombre")
    var nivelEducativoNombre: String? = ""

    @ColumnInfo(name = "TelefonoFijo")
    var telefonoFijo: String? = ""

    @ColumnInfo(name = "Celular")
    var celular: String? = ""

    @ColumnInfo(name = "MunicipioId")
    var municipioId: String? = ""

    @ColumnInfo(name = "MunicipioNombre")
    var municipioNombre: String? = ""

    @ColumnInfo(name = "DepartamentoId")
    var departamentoId: String? = ""

    @ColumnInfo(name = "DepartamentoNombre")
    var departamentoNombre: String? = ""

    @ColumnInfo(name = "PaisId")
    var paisId: String? = ""

    @ColumnInfo(name = "PaisNombre")
    var paisNombre: String? = ""

    @ColumnInfo(name = "Direccion")
    var direccion: String? = ""

    @ColumnInfo(name = "Edad")
    var edad: Int? = 0

    @ColumnInfo(name = "Justificacion")
    var justificacion: String? = ""

    @ColumnInfo(name = "Estado")
    var estado: String? = ""

    constructor(json: JSONObject) {
        try {
            val validador = JSONValidador()

            this.id = json.getInt("id")
            this.primerNombre = validador.campoNulo(json.getString("primerNombre"))
            this.segundoNombre = validador.campoNulo(json.getString("segundoNombre"))
            this.primerApellido = validador.campoNulo(json.getString("primerApellido"))
            this.segundoApellido = validador.campoNulo(json.getString("segundoApellido"))
            this.sexoId = validador.campoNulonumerico(json.getString("sexoId"))
            this.sexoNombre = validador.jsonNuloPrimerGrado(json, "sexo", "nombre")
            this.parentescoId = validador.campoNulonumerico(json.getString("parentescoId"))
            this.parentescoNombre = validador.jsonNuloPrimerGrado(json, "parentesco", "nombre")
            this.dependiente = validador.campoBoolean(json.getString("dependiente"))
            this.fechaNacimiento = validador.campoFechaHora(json.getString("fechaNacimiento"))
            this.tipoDocumentoId = validador.campoNulonumerico(json.getString("tipoDocumentoId"))
            this.tipoDocumentoNombre = validador.jsonNuloPrimerGrado(json, "tipoDocumento", "codigoPila")
            this.numeroDocumento = validador.campoNulo(json.getString("numeroDocumento"))
            this.nivelEducativoId = validador.campoNulonumerico(json.getString("nivelEducativoId"))
            this.nivelEducativoNombre = validador.jsonNuloPrimerGrado(json, "nivelEducativo", "nombre")
            this.telefonoFijo = validador.campoNulo(json.getString("telefonoFijo"))
            this.celular = validador.campoNulo(json.getString("celular"))
            this.municipioId = validador.campoNulo(json.getString("divisionPoliticaNivel2Id"))
            this.municipioNombre = validador.jsonNuloPrimerGrado(json, "divisionPoliticaNivel2", "nombre")
            this.departamentoId = validador.jsonNuloPrimerGrado(json, "divisionPoliticaNivel2", "divisionPoliticaNivel1Id")
            this.departamentoNombre = validador.jsonNuloSegundoGrado(json, "divisionPoliticaNivel2", "divisionPoliticaNivel1", "nombre")
            this.paisId = validador.jsonNuloSegundoGrado(json, "divisionPoliticaNivel2", "divisionPoliticaNivel1", "paisId")
            this.paisNombre = validador.jsonNuloTercerGrado(json, "divisionPoliticaNivel2", "divisionPoliticaNivel1", "pais", "nombre")
            this.direccion = validador.campoNulo(json.getString("direccion"))
            this.edad = validador.calcularEdad(validador.campoFechaHora(json.getString("fechaNacimiento")))
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