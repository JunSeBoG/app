package com.alcanosesp.appalcanos.db.entity

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alcanosesp.appalcanos.utils.JSONValidador
import org.json.JSONObject


@Entity(tableName = "Funcionario")
class Funcionario {

    @PrimaryKey
    @ColumnInfo(name = "Id")
    var id: String = ""

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

    @ColumnInfo(name = "EstadoCivilId")
    var estadoCivilId: String? = ""

    @ColumnInfo(name = "EstadoCivilNombre")
    var estadoCivilNombre: String? = ""

    @ColumnInfo(name = "OcupacionId")
    var ocupacionId: String? = ""

    @ColumnInfo(name = "OcupacionNombre")
    var ocupacionNombre: String? = ""

    @ColumnInfo(name = "Pensionado")
    var pensionado: String? = ""

    @ColumnInfo(name = "Estado")
    var estado: String? = ""

    @ColumnInfo(name = "FechaNacimiento")
    var fechaNacimiento: String? = ""

    @ColumnInfo(name = "MunicipioOrigenId")
    var municipioOrigenId: String? = ""

    @ColumnInfo(name = "MunicipioOrigenNombre")
    var municipioOrigenNombre: String? = ""

    @ColumnInfo(name = "DepartamentoOrigenId")
    var departamentoOrigenId: String? = ""

    @ColumnInfo(name = "DepartamentoOrigenNombre")
    var departamentoOrigenNombre: String? = ""

    @ColumnInfo(name = "PaisOrigenId")
    var paisOrigenId: String? = ""

    @ColumnInfo(name = "PaisOrigenNombre")
    var paisOrigenNombre: String? = ""

    @ColumnInfo(name = "TipoDocumentoId")
    var tipoDocumentoId: String? = ""

    @ColumnInfo(name = "TipoDocumentoNombre")
    var tipoDocumentoNombre: String? = ""

    @ColumnInfo(name = "NumeroDocumento")
    var numeroDocumento: String? = ""

    @ColumnInfo(name = "FechaExpedicionDocumento")
    var fechaExpedicionDocumento: String? = ""

    @ColumnInfo(name = "MunicipioExpedicionDocumentoId")
    var municipioExpedicionDocumentoId: String? = ""

    @ColumnInfo(name = "MunicipioExpedicionDocumentoNombre")
    var municipioExpedicionDocumentoNombre: String? = ""

    @ColumnInfo(name = "DepartamentoExpedicionDocumentoId")
    var departamentoExpedicionDocumentoId: String? = ""

    @ColumnInfo(name = "DepartamentoExpedicionDocumentoNombre")
    var departamentoExpedicionDocumentoNombre: String? = ""

    @ColumnInfo(name = "PaisExpedicionDocumentoId")
    var paisExpedicionDocumentoId: String? = ""

    @ColumnInfo(name = "PaisExpedicionDocumentoNombre")
    var paisExpedicionDocumentoNombre: String? = ""

    @ColumnInfo(name = "Nit")
    var nit: String? = ""

    @ColumnInfo(name = "DigitoVerificacion")
    var digitoVerificacion: String? = ""

    @ColumnInfo(name = "MunicipioResidenciaId")
    var municipioResidenciaId: String? = ""

    @ColumnInfo(name = "MunicipioResidenciaNombre")
    var municipioResidenciaNombre: String? = ""

    @ColumnInfo(name = "DepartamentoResidenciaId")
    var departamentoResidenciaId: String? = ""

    @ColumnInfo(name = "DepartamentoResidenciaNombre")
    var departamentoResidenciaNombre: String? = ""

    @ColumnInfo(name = "PaisResidenciaId")
    var paisResidenciaId: String? = ""

    @ColumnInfo(name = "PaisResidenciaNombre")
    var paisResidenciaNombre: String? = ""

    @ColumnInfo(name = "Celular")
    var celular: String? = ""

    @ColumnInfo(name = "TelefonoFijo")
    var telefonoFijo: String? = ""

    @ColumnInfo(name = "TipoViviendaId")
    var tipoViviendaId: String? = ""

    @ColumnInfo(name = "TipoViviendaNombre")
    var tipoViviendaNombre: String? = ""

    @ColumnInfo(name = "Direccion")
    var direccion: String? = ""

    @ColumnInfo(name = "ClaseLibretaMilitarId")
    var claseLibretaMilitarId: String? = ""

    @ColumnInfo(name = "ClaseLibretaMilitarNombre")
    var claseLibretaMilitarNombre: String? = ""

    @ColumnInfo(name = "NumeroLibreta")
    var numeroLibreta: String? = ""

    @ColumnInfo(name = "Distrito")
    var distrito: String? = ""


    @ColumnInfo(name = "LicenciaConduccionAId")
    var licenciaConduccionAId: String? = ""

    @ColumnInfo(name = "LicenciaConduccionANombre")
    var licenciaConduccionANombre: String? = ""

    @ColumnInfo(name = "LicenciaConduccionAFechaVencimiento")
    var licenciaConduccionAFechaVencimiento: String? = ""

    @ColumnInfo(name = "LicenciaConduccionBId")
    var licenciaConduccionBId: String? = ""

    @ColumnInfo(name = "LicenciaConduccionBNombre")
    var licenciaConduccionBNombre: String? = ""

    @ColumnInfo(name = "LicenciaConduccionBFechaVencimiento")
    var licenciaConduccionBFechaVencimiento: String? = ""

    @ColumnInfo(name = "LicenciaConduccionCId")
    var licenciaConduccionCId: String? = ""

    @ColumnInfo(name = "LicenciaConduccionCNombre")
    var licenciaConduccionCNombre: String? = ""

    @ColumnInfo(name = "LicenciaConduccionCFechaVencimiento")
    var licenciaConduccionCFechaVencimiento: String? = ""


    @ColumnInfo(name = "TallaCamisa")
    var tallaCamisa: String? = ""

    @ColumnInfo(name = "TallaPantalon")
    var tallaPantalon: String? = ""

    @ColumnInfo(name = "NumeroCalzado")
    var numeroCalzado: String? = ""

    @ColumnInfo(name = "UsaLentes")
    var usaLentes: String? = ""

    @ColumnInfo(name = "TipoSangreId")
    var tipoSangreId: String? = ""

    @ColumnInfo(name = "TipoSangreNombre")
    var tipoSangreNombre: String? = ""

    @ColumnInfo(name = "CorreoElectronicoPersonal")
    var correoElectronicoPersonal: String? = ""

    @ColumnInfo(name = "CorreoElectronicoCorporativo")
    var correoElectronicoCorporativo: String? = ""

    @ColumnInfo(name = "Adjunto")
    var adjunto: String? = ""

    @ColumnInfo(name = "Foto")
    var foto: String? = ""

    @ColumnInfo(name = "Cargo")
    var cargo: String? = ""

    @ColumnInfo(name = "ContratoId")
    var contratoId: String? = ""


    constructor(json: JSONObject) {
        val validador = JSONValidador()

        try {
            this.id = json.getString("id")
            this.primerNombre = validador.campoNulo(json.getString("primerNombre"))
            this.segundoNombre = validador.campoNulo(json.getString("segundoNombre"))
            this.primerApellido = validador.campoNulo(json.getString("primerApellido"))
            this.segundoApellido = validador.campoNulo(json.getString("segundoApellido"))

            this.sexoId = validador.campoNulo(json.getString("sexoId"))
            this.sexoNombre = validador.jsonNuloPrimerGrado(json, "sexo", "nombre")

            this.estadoCivilId = validador.campoNulo(json.getString("estadoCivilId"))
            this.estadoCivilNombre = validador.jsonNuloPrimerGrado(json, "estadoCivil", "nombre")

            this.ocupacionId = validador.campoNulo(json.getString("ocupacionId"))
            this.ocupacionNombre = validador.jsonNuloPrimerGrado(json, "ocupacion", "nombre")

            this.pensionado = validador.campoBoolean(json.getString("pensionado"))
            this.estado = validador.campoNulo(json.getString("estado"))
            this.fechaNacimiento = validador.campoFechaHora(json.getString("fechaNacimiento"))

            this.municipioOrigenId =
                validador.campoNulo(json.getString("divisionPoliticaNivel2OrigenId"))
            this.municipioOrigenNombre =
                validador.jsonNuloPrimerGrado(json, "divisionPoliticaNivel2Origen", "nombre")
            this.departamentoOrigenId = validador.jsonNuloPrimerGrado(
                json,
                "divisionPoliticaNivel2Origen",
                "divisionPoliticaNivel1Id"
            )
            this.departamentoOrigenNombre = validador.jsonNuloSegundoGrado(
                json,
                "divisionPoliticaNivel2Origen",
                "divisionPoliticaNivel1",
                "nombre"
            )
            this.paisOrigenId =
                validador.campoNulo(json.getString("divisionPoliticaNivel2OrigenId"))//validador.jsonNuloSegundoGrado(json, "divisionPoliticaNivel2Origen", "divisionPoliticaNivel1", "paisId")
            this.paisOrigenNombre = validador.jsonNuloTercerGrado(
                json,
                "divisionPoliticaNivel2Origen",
                "divisionPoliticaNivel1",
                "pais",
                "nombre"
            )

            this.tipoDocumentoId = validador.campoNulo(json.getString("tipoDocumentoId"))
            this.tipoDocumentoNombre =
                validador.jsonNuloPrimerGrado(json, "tipoDocumento", "codigoPila")

            this.numeroDocumento = validador.campoNulo(json.getString("numeroDocumento"))
            this.fechaExpedicionDocumento =
                validador.campoFechaHora(json.getString("fechaExpedicionDocumento"))

            this.municipioExpedicionDocumentoId =
                validador.campoNulo(json.getString("divisionPoliticaNivel2ExpedicionDocumentoId"))
            this.municipioExpedicionDocumentoNombre = validador.jsonNuloPrimerGrado(
                json,
                "divisionPoliticaNivel2ExpedicionDocumento",
                "nombre"
            )
            this.departamentoExpedicionDocumentoId = validador.jsonNuloPrimerGrado(
                json,
                "divisionPoliticaNivel2ExpedicionDocumento",
                "divisionPoliticaNivel1Id"
            )
            this.departamentoExpedicionDocumentoNombre = validador.jsonNuloSegundoGrado(
                json,
                "divisionPoliticaNivel2ExpedicionDocumento",
                "divisionPoliticaNivel1",
                "nombre"
            )
            this.paisExpedicionDocumentoId = validador.jsonNuloSegundoGrado(
                json,
                "divisionPoliticaNivel2ExpedicionDocumento",
                "divisionPoliticaNivel1",
                "paisId"
            )
            this.paisExpedicionDocumentoNombre = validador.jsonNuloTercerGrado(
                json,
                "divisionPoliticaNivel2ExpedicionDocumento",
                "divisionPoliticaNivel1",
                "pais",
                "nombre"
            )

            this.nit = validador.campoNulo(json.getString("nit"))
            this.digitoVerificacion = validador.campoNulo(json.getString("digitoVerificacion"))

            this.municipioResidenciaId =
                validador.campoNulo(json.getString("divisionPoliticaNivel2ResidenciaId"))
            this.municipioResidenciaNombre =
                validador.jsonNuloPrimerGrado(json, "divisionPoliticaNivel2Residencia", "nombre")
            this.departamentoResidenciaId = validador.jsonNuloPrimerGrado(
                json,
                "divisionPoliticaNivel2Residencia",
                "divisionPoliticaNivel1Id"
            )
            this.departamentoResidenciaNombre = validador.jsonNuloSegundoGrado(
                json,
                "divisionPoliticaNivel2Residencia",
                "divisionPoliticaNivel1",
                "nombre"
            )
            this.paisResidenciaId = validador.jsonNuloSegundoGrado(
                json,
                "divisionPoliticaNivel2Residencia",
                "divisionPoliticaNivel1",
                "paisId"
            )
            this.paisResidenciaNombre = validador.jsonNuloTercerGrado(
                json,
                "divisionPoliticaNivel2Residencia",
                "divisionPoliticaNivel1",
                "pais",
                "nombre"
            )

            this.celular = validador.campoNulo(json.getString("celular"))
            this.telefonoFijo = validador.campoNulo(json.getString("telefonoFijo"))

            this.tipoViviendaId = validador.campoNulo(json.getString("tipoViviendaId"))
            this.tipoViviendaNombre = validador.jsonNuloPrimerGrado(json, "tipoVivienda", "nombre")

            this.direccion = validador.campoNulo(json.getString("direccion"))

            this.claseLibretaMilitarId =
                validador.campoNulo(json.getString("claseLibretaMilitarId"))
            this.claseLibretaMilitarNombre =
                validador.jsonNuloPrimerGrado(json, "claseLibretaMilitar", "nombre")
            this.numeroLibreta = validador.campoNulo(json.getString("numeroLibreta"))
            this.distrito = validador.campoNulo(json.getString("distrito"))

            this.licenciaConduccionAId =
                validador.campoNulo(json.getString("licenciaConduccionAId"))
            this.licenciaConduccionANombre =
                validador.jsonNuloPrimerGrado(json, "licenciaConduccionA", "nombre")
            this.licenciaConduccionAFechaVencimiento =
                validador.campoFechaHora(json.getString("licenciaConduccionAFechaVencimiento"))
            this.licenciaConduccionBId =
                validador.campoNulo(json.getString("licenciaConduccionBId"))
            this.licenciaConduccionBNombre =
                validador.jsonNuloPrimerGrado(json, "licenciaConduccionB", "nombre")
            this.licenciaConduccionBFechaVencimiento =
                validador.campoFechaHora(json.getString("licenciaConduccionBFechaVencimiento"))
            this.licenciaConduccionCId =
                validador.campoNulo(json.getString("licenciaConduccionCId"))
            this.licenciaConduccionCNombre =
                validador.jsonNuloPrimerGrado(json, "licenciaConduccionC", "nombre")
            this.licenciaConduccionCFechaVencimiento =
                validador.campoFechaHora(json.getString("licenciaConduccionCFechaVencimiento"))

            this.tallaCamisa = validador.campoNulo(json.getString("tallaCamisa"))
            this.tallaPantalon = validador.campoNulo(json.getString("tallaPantalon"))
            this.numeroCalzado = validador.campoNulo(json.getString("numeroCalzado"))
            this.usaLentes = validador.campoBoolean(json.getString("usaLentes"))

            this.tipoSangreId = validador.campoNulo(json.getString("tipoSangreId"))
            this.tipoSangreNombre = validador.jsonNuloPrimerGrado(json, "tipoSangre", "nombre")

            this.correoElectronicoPersonal =
                validador.campoNulo(json.getString("correoElectronicoPersonal"))
            this.correoElectronicoCorporativo =
                validador.campoNulo(json.getString("correoElectronicoCorporativo"))
            this.adjunto = validador.campoNulo(json.getString("adjunto"))
            this.foto = ""
            this.cargo = validador.obtenerCargo(json.getString("contrato"),json.getString("contratoOtroSi")).toLowerCase()
            this.contratoId = validador.campoNulo(json.getString("contratoId"))


        } catch (e: Exception) {
            Log.e("ErrorFincionarioModel", e.message!!)
        }
    }


    constructor()

    fun nombreCorto(): String {
        return "${this.primerNombre} ${this.primerApellido}"
    }

    fun campoVacio(s: String): String{
        return if (s.isEmpty()){
            "N/A"
        }else{
            s
        }
    }
}