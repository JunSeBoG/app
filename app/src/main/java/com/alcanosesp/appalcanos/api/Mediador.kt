package com.alcanosesp.appalcanos.api

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.alcanosesp.appalcanos.App
import com.android.volley.VolleyError
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

val TAG = "Mediador"
val HOST = "http://nompruebas.alcanosesp.com:8081/"



fun headers(): HashMap<String, String> {
    return object : HashMap<String, String>() {
        init {
            put("JwtToken", App.TOKEN.toString())
            put("Aplicacion", "NOMINAP")
            put("Cliente", "Tm9taW5hTW92aWxBbGNhbm9zQ29sb21iaWFFc3A=")
        }
    }
}

fun masterHeaders(): HashMap<String, String> {
    return object : HashMap<String, String>() {
        init {
            put("JwtToken", App.MASTERTOKEN.toString())
        }
    }
}

val c401 = object : I401 {

    override fun call(
        context: Context,
        url: String,
        parametros: JSONObject?,
        iRespuesta: IRespuestaServidor?
    ) {
        try {
            val urlRefresh = HOST.plus("api/autenticacion/refresh")

            val iRespToken = object : IRespuestaServidor {
                override fun error(error: VolleyError) {
                    Log.i("Error desde c401", "Error desde c401")
                    Toast.makeText(context, "pailas desde el c401", Toast.LENGTH_LONG).show()
                    //cerrar Cesion

                }

                override fun exito(respuesta: Any?) {
                    try {
                        Log.i("Exito", "refresco el token")
                        Toast.makeText(
                            context,
                            "refresco el token",
                            Toast.LENGTH_LONG
                        ).show()
                        //actuliza los token del usuario
                        val json = JSONObject(respuesta.toString())
                        App.TOKEN = json.getString("token")
                        App.REFRESH = json.getString("refreshToken")

                        Log.i("RefrecoRespuesra", respuesta.toString())

                        SolicitudVolley.getInstance(context)
                            .jsonRequest(
                                url, parametros,
                                headers(), iRespuesta, null
                            )
                    } catch (e: Exception) {

                    }

                }
            }

            val parametros = JSONObject().apply {
                put("refreshToken", App.REFRESH)
            }

            SolicitudVolley.getInstance(context)
                .jsonRequest(
                    urlRefresh, parametros, headers(), iRespToken, null
                )

        } catch (e: Exception) {
            Log.d(TAG, e.message!!)
        }
    }
}

fun consultarClima(
    context: Context,
    callback: IRespuestaServidor
) {
    try {
        val url = HOST.plus("weatherforecast")
        SolicitudVolley.getInstance(context)
            .jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerPaises(
    context: Context,
    callback: IRespuestaServidor
) {
    try {
        val url =
            HOST.plus("odata/paises?\$orderby=nombre&\$filter=estadoRegistro eq 'Activo'")

        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerDepartamentos(
    context: Context,
    callback: IRespuestaServidor,
    pais: Int?
) {
    try {
        val url =
            HOST.plus("odata/divisionpoliticaniveles1?\$orderby=nombre&\$filter=paisId eq $pais and estadoRegistro eq 'Activo'")
        Log.i("JUICIO", url)
        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerMunicipios(
    context: Context,
    callback: IRespuestaServidor,
    dpto: Int?
) {
    try {
        val url =
            HOST.plus("odata/divisionpoliticaniveles2?\$orderby=nombre&\$filter=divisionPoliticaNivel1Id eq $dpto and estadoRegistro eq 'Activo'")

        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerSexos(
    context: Context,
    callback: IRespuestaServidor
) {
    try {
        val url = HOST.plus("odata/sexos?\$orderby=nombre&\$filter=estadoRegistro eq 'Activo'")

        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerTiposDocumento(
    context: Context,
    callback: IRespuestaServidor
) {
    try {
        val url =
            HOST.plus("odata/tipoDocumentos?\$orderby=nombre&\$filter=estadoRegistro eq 'Activo'")

        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerTiposVivienda(
    context: Context,
    callback: IRespuestaServidor
) {
    try {
        val url =
            HOST.plus("odata/tipoviviendas?\$orderby=nombre&\$filter=estadoRegistro eq 'Activo'")

        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerParentescos(
    context: Context,
    callback: IRespuestaServidor
) {
    try {
        val url =
            HOST.plus("odata/parentescos?\$orderby=nombre&\$filter=estadoRegistro eq 'Activo'")

        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerProfesiones(
    context: Context,
    callback: IRespuestaServidor
) {
    try {
        val url =
            HOST.plus("odata/profesiones?\$orderby=nombre&\$filter=estadoRegistro eq 'Activo'")
        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerNivelesEducativos(
    context: Context,
    callback: IRespuestaServidor
) {
    try {
        val url =
            HOST.plus("odata/nivelEducativos?\$orderby=nombre&\$filter=estadoRegistro eq 'Activo'")
        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerFamiliares(
    context: Context,
    callback: IRespuestaServidor,
    idFuncionario: String
) {
    try {
        //val url = HOST.plus("odata/funcionarioestudios?\$filter=funcionarioId eq $idFuncionario &\$expand=nivelEducativo, pais, profesion")
        val url =
            HOST.plus("odata/InformacionFamiliares?\$filter=funcionarioId eq $idFuncionario and estadoRegistro eq 'Activo' &\$expand=tipoDocumento, parentesco, sexo, nivelEducativo, divisionPoliticaNivel2(\$expand=divisionPoliticaNivel1(\$expand=pais))")

        Log.i("JUICIO", url)
        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun registrarFamiliar(
    context: Context,
    callback: IRespuestaServidor,
    parametros: JSONObject
) {
    try {
        val url = HOST.plus("api/InformacionFamiliares")
        SolicitudVolley.getInstance(context).jsonRequest(url, parametros, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun editarFamiliar(
    context: Context,
    callback: IRespuestaServidor,
    parametros: JSONObject,
    id: Int
) {
    try {
        val url = HOST.plus("api/InformacionFamiliares/$id")
        SolicitudVolley.getInstance(context)
            .jsonPutRequest(url, parametros, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerEstudios(
    context: Context,
    callback: IRespuestaServidor,
    idFuncionario: String
) {
    try {
        val url =
            HOST.plus("odata/funcionarioestudios?\$filter=funcionarioId eq $idFuncionario and estadoRegistro eq 'Activo' &\$expand=nivelEducativo, pais, profesion")
        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun registrarEstudio(
    context: Context,
    callback: IRespuestaServidor,
    parametros: JSONObject
) {
    try {
        val url = HOST.plus("api/funcionarioestudios")
        SolicitudVolley.getInstance(context).jsonRequest(url, parametros, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun editarEstudio(
    context: Context,
    callback: IRespuestaServidor,
    parametros: JSONObject,
    id: Int
) {
    try {
        val url = HOST.plus("api/funcionarioestudios/$id")
        SolicitudVolley.getInstance(context)
            .jsonPutRequest(url, parametros, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerExperiencias(
    context: Context,
    callback: IRespuestaServidor,
    idFuncionario: String
) {
    try {
        val url =
            HOST.plus("odata/ExperienciaLaborales?\$filter=funcionarioId eq $idFuncionario and estadoRegistro eq 'Activo'")

        Log.i("JUICIO", url)
        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun registrarExperiencia(
    context: Context,
    callback: IRespuestaServidor,
    parametros: JSONObject
) {
    try {
        val url = HOST.plus("api/ExperienciaLaborales")
        SolicitudVolley.getInstance(context).jsonRequest(url, parametros, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun editarExperiencia(
    context: Context,
    callback: IRespuestaServidor,
    parametros: JSONObject,
    id: Int
) {
    try {
        val url = HOST.plus("api/ExperienciaLaborales/$id")
        SolicitudVolley.getInstance(context).jsonPutRequest(url, parametros, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerSolicitudesBeneficios(
    context: Context,
    callback: IRespuestaServidor,
    idFuncionario: String
) {
    try {
        val url =
            HOST.plus("odata/beneficios?\$orderBy=fechaSolicitud desc &\$filter=funcionarioId eq $idFuncionario and estadoRegistro eq 'Activo' &\$expand=tipobeneficio, tipoPeriodo")

        Log.i("JUICIO", url)
        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}
fun obtenerTiposBeneficio(
    context: Context,
    callback: IRespuestaServidor
) {
    try {
        val url =
            HOST.plus("odata/tipobeneficios?\$orderby=nombre&\$filter=estadoRegistro eq 'Activo'")

        Log.i("JUICIO", url)
        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}
fun obtenerTipoBeneficio(
    context: Context,
    callback: IRespuestaServidor,
    id: Int
) {
    try {
        val url =
            HOST.plus("odata/tipobeneficios?\$filter=id eq $id")

        Log.i("JUICIO", url)
        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}
fun obtenerPeriodosPago(
    context: Context,
    callback: IRespuestaServidor,
    id: Int
) {
    try {
        val url =
            HOST.plus("odata/tipobeneficios?\$filter=id eq $id")

        Log.i("JUICIO", url)
        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}
fun obtenerRequisitosTipoBeneficio(
    context: Context,
    callback: IRespuestaServidor,
    id: Int
) {
    try {
        val url =
            HOST.plus("odata/tipobeneficiorequisitos?\$filter=tipoBeneficioId eq $id &\$expand=tipoSoporte")

        Log.i("JUICIO", url)
        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}
fun obtenerAdjuntosBeneficio(
    context: Context,
    callback: IRespuestaServidor,
    id: Int
) {
    try {
        val url =
            HOST.plus("odata/BeneficioAdjuntos?\$filter=beneficioId eq $id &\$expand=tipoBeneficioRequisito(\$expand=tipoSoporte)")

        Log.i("JUICIO", url)
        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}
fun registrarSolicitudBeneficio(
    context: Context,
    callback: IRespuestaServidor,
    parametros: JSONObject
) {
    try {
        val url =
            HOST.plus("api/beneficios")

        Log.i("JUICIO", url)
        SolicitudVolley.getInstance(context).jsonRequest(url, parametros, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}fun editarSolicitudBeneficio(
    context: Context,
    callback: IRespuestaServidor,
    parametros: JSONObject,
    id: Int
) {
    try {
        val url =
            HOST.plus("api/beneficios/$id")

        Log.i("JUICIO", url)
        SolicitudVolley.getInstance(context).jsonPutRequest(url, parametros, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}
fun cancelarSolicitudBeneficio(
    context: Context,
    callback: IRespuestaServidor,
    parametros: JSONObject,
    id: Int
) {
    try {
        val url =
            HOST.plus("api/beneficios/$id")

        Log.i("JUICIO", url)
        SolicitudVolley.getInstance(context).jsonPatchRequest(url, parametros, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun iniciarSesion(
    context: Context,
    usuario: String,
    contrasena: String,
    callback: IRespuestaServidor
) {
    try {
        val url = HOST.plus("api/Autenticacion/Login")
        val parametros = JSONObject()
        parametros.put("cedula", usuario)
        parametros.put("clave", contrasena)

        SolicitudVolley.getInstance(context)
            .jsonRequest(url, parametros, headers(), callback, null)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerFuncionario(context: Context, cedula: String, callback: IRespuestaServidor) {
    try {
        val filtro = "odata/funcionarioDatoActuales/?" +
                "\$top=1&\$filter=numeroDocumento eq '${cedula}'&" +
                "&\$expand= sexo(\$select=id,nombre), " +
                "EstadoCivil(\$select=id,nombre), " +
                "Ocupacion(\$select=id,codigo,nombre), " +
                "tipoDocumento(\$select=id,codigoPila,codigoDian,nombre,formato), " +
                "DivisionPoliticaNivel2Origen(\$select=id,codigo,nombre,divisionPoliticaNivel1Id; " +
                "\$expand=divisionPoliticaNivel1(\$select=id,codigo,nombre,paisId; " +
                "\$expand=pais(\$select=id,codigo,nombre))), " +
                "DivisionPoliticaNivel2Residencia(\$select=id,codigo,nombre, divisionPoliticaNivel1Id;" +
                "\$expand=divisionPoliticaNivel1(\$select=id,codigo,nombre,paisId; " +
                "\$expand=pais(\$select=id,codigo,nombre))), " +
                "TipoVivienda(\$select=id,nombre), " +
                "ClaseLibretaMilitar(\$select=id,nombre), " +
                "TipoSangre(\$select=id,nombre), " +
                "licenciaConduccionA(\$select=id,nombre), " +
                "licenciaConduccionB(\$select=id,nombre), " +
                "licenciaConduccionC(\$select=id,nombre), " +
                "contrato(\$select=id; \$expand= cargoDependencia(\$select=id; \$expand= cargo(\$select=nombre))), " +
                "contratoOtroSi (\$select=id; \$expand= cargoDependencia(\$select=id; \$expand= cargo(\$select=nombre)))," +
                "DivisionPoliticaNivel2ExpedicionDocumento(\$select=id,codigo,nombre,divisionPoliticaNivel1Id; \$expand=divisionPoliticaNivel1(\$select=id,codigo,nombre,paisId; \$expand=pais(\$select=id,codigo,nombre)))"
        val url = HOST.plus(filtro)

        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun editarFuncionario(
    context: Context,
    callback: IRespuestaServidor,
    parametros: JSONObject,
    id: Int
) {
    try {
        val url = HOST.plus("api/Funcionarios/${id}")
        SolicitudVolley.getInstance(context).jsonPatchRequest(url, parametros, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerImagenServer(context: Context, objectId: String, callback: IRespuestaServidor) {
    try {

        val url = HOST.plus("api/archivos/$objectId/Archivo")

        SolicitudVolley.getInstance(context).imgRequest(
            url,
            1000,
            1000,
            ImageView.ScaleType.CENTER,
            Bitmap.Config.ARGB_8888,
            null,
            callback,
            c401
        )
    } catch (e: Exception) {
        Log.i(TAG, e.message!!)
    }
}

fun eliminarArchivoServer(context: Context, objectId: String, callback: IRespuestaServidor) {
    try {

        val url = HOST.plus("api/archivos/$objectId")
        SolicitudVolley.getInstance(context).jsonRequestDelete(
            url,
            null,
            headers(),
            callback,
            c401
        )
    } catch (e: Exception) {
        Log.i(TAG, e.message!!)
    }
}

fun actualizarFuncionarioAdjunto(
    context: Context,
    FuncionarioId: String,
    AdjuntoId: String?,
    callback: IRespuestaServidor
) {
    try {

        val url = HOST.plus("api/Funcionarios/$FuncionarioId")
        val parametros = JSONObject()
        parametros.put("id", FuncionarioId)
        parametros.put("adjunto", AdjuntoId)

        SolicitudVolley.getInstance(context).jsonPatchRequest(
            url,
            parametros,
            headers(),
            callback,
            c401
        )
    } catch (e: Exception) {
        Log.i(TAG, e.message!!)
    }
}


fun crearArchivoServer(context: Context, archivoB64: String, callback: IRespuestaServidor) {
    try {
        val url = HOST.plus("api/archivos")
        val parametros = JSONObject()
        parametros.put("archivo", archivoB64.replace("\\n", ""))

        SolicitudVolley.getInstance(context).jsonRequest(
            url,
            parametros,
            headers(),
            callback,
            c401
        )
    } catch (e: Exception) {
        Log.i(TAG, e.message!!)
    }
}

//cambiar por ausentismo
fun obtenerIncapacidad(
    context: Context,
    callback: IRespuestaServidor,
    idFuncionario: String
) {
    try {
        val url = HOST.plus(
                    "odata/AusentismoFuncionarios?\$orderby=fechaInicio desc & " +
                            "\$filter=TipoAusentismo/ClaseAusentismo/Codigo eq 'I' and " +
                            "funcionarioId eq $idFuncionario  and estadoRegistro ne 'Eliminado' " +
                            "&\$expand=tipoAusentismo(\$Expand=claseAusentismo),diagnosticoCie"
        )

        Log.i("Ausentismo", url)
        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun registrarIncapacidad(
    context: Context,
    callback: IRespuestaServidor,
    parametros: JSONObject
) {
    try {
        val url = HOST.plus("api/AusentismoFuncionarios")
        SolicitudVolley.getInstance(context).jsonRequest(url, parametros, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun editarIncapacidad(
    context: Context,
    callback: IRespuestaServidor,
    parametros: JSONObject,
    id: Int
) {
    try {
        val url = HOST.plus("api/AusentismoFuncionarios/$id")
        SolicitudVolley.getInstance(context)
            .jsonPutRequest(url, parametros, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerDiagnosticoCie(
    context: Context,
    callback: IRespuestaServidor,
    like: CharSequence
) {
    try {
        val url =
            HOST.plus("odata/Diagnosticocies?\$top=20&\$filter=contains(codigo,'${like}') and estadoRegistro eq 'Activo'")

        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerTipoIncapaciad(
    context: Context,
    callback: IRespuestaServidor
) {
    try {
        val url =
            HOST.plus(
                "odata/TipoAusentismos?\$filter=ClaseAusentismo/Codigo eq 'I' " +
                        "and estadoRegistro  eq 'Activo' &\$Expand=claseAusentismo"
            )
        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerIncapaciadProrrogaPorFecha(
    context: Context,
    fechaInicio: String,
    funcionarioId: Int,
    callback: IRespuestaServidor
) {
    try {

        val calendar = Calendar.getInstance();
        val date = SimpleDateFormat("yyyy-MM-dd").parse(fechaInicio.replace(" ", ""))
        calendar.time = date!!;
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        val diaAnterior = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)

        val url =
            HOST.plus(
                "odata/AusentismoFuncionarios?\$orderby=fechaInicio &" +
                        "\$filter=TipoAusentismo/ClaseAusentismo/Codigo eq 'I' and " +
                        "funcionarioId eq $funcionarioId and estado ne 'Anulado' and fechaFin eq $diaAnterior and estadoRegistro eq 'Activo'  &" +
                        "\$select=Id, fechaInicio,fechaFin &\$expand=tipoAusentismo(\$select=nombre &\$Expand=claseAusentismo)," +
                        "diagnosticoCie(\$select=Codigo)"
            )
        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerIncapaciadProrrogaPorId(
    context: Context,
    filtroId: String,
    callback: IRespuestaServidor
) {
    try {
        val url =
            HOST.plus(
                "odata/ProrrogaAusentismos?\$filter= ($filtroId) and estado ne 'Anulado' " +
                        "&\$expand=prorroga(\$expand=diagnosticoCie(\$select= codigo)) "
            )

        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun cancelarIncapacidad(
    context: Context,
    ausentimsoId: Int,
    parametros: JSONObject,
    callback: IRespuestaServidor
) {
    try {
        val url = HOST.plus("api/AusentismoFuncionarios/estado/$ausentimsoId")

        SolicitudVolley.getInstance(context).jsonPatchRequest (url, parametros, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerSolicitudPermiso(
    context: Context,
    callback: IRespuestaServidor,
    idFuncionario: String
) {
    try {
        val url = HOST.plus(
            "odata/SolicitudPermisos?\$filter=funcionarioId eq $idFuncionario and estadoRegistro eq 'Activo' " +
                    "&\$orderby=fechaInicio desc &\$Select=id,fechaInicio,fechaFin,horaSalida,horaLlegada,estado,justificacion,observaciones " +
                    "&\$expand=tipoAusentismo(\$select=id,codigo,nombre; \$expand=claseAusentismo (\$select=id,codigo,nombre))"
        )

        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun cancelarSolicitudPermiso(
    context: Context,
    solicitudPermisoId: Int,
    parametros: JSONObject,
    callback: IRespuestaServidor
) {
    try {
        val url = HOST.plus("api/SolicitudPermisos/Estado/$solicitudPermisoId")

        SolicitudVolley.getInstance(context).jsonPatchRequest (url, parametros, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerClasePermiso(
    context: Context,
    filtro :String,
    callback: IRespuestaServidor
) {
    try {
        val url =
            HOST.plus(
                "odata/claseAusentismos?\$filter=$filtro and estadoRegistro eq 'Activo' " +
                        "&\$orderby=nombre &\$select=id, codigo,nombre"
            )

        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerTipoPermiso(
    context: Context,
    filtro :String,
    callback: IRespuestaServidor
) {
    try {
        val url =
            HOST.plus(
                "odata/tipoAusentismos?\$filter=estadoRegistro eq 'Activo' $filtro  " +
                        "&\$orderby=nombre  &\$select=id, codigo,nombre,claseAusentismoId, unidadTiempo"
            )

        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun registrarSolicitudPermiso(
    context: Context,
    callback: IRespuestaServidor,
    parametros: JSONObject
) {
    try {
        val url = HOST.plus("api/SolicitudPermisos")
        SolicitudVolley.getInstance(context).jsonRequest(url, parametros, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun editarSolicitudPermiso(
    context: Context,
    callback: IRespuestaServidor,
    parametros: JSONObject,
    id: Int
) {
    try {
        val url = HOST.plus("api/SolicitudPermisos/$id")
        SolicitudVolley.getInstance(context)
            .jsonPutRequest(url, parametros, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerTipoSoporte(
    context: Context,
    callback: IRespuestaServidor
) {
    try {
        val url =
            HOST.plus("odata/tipoSoportes?\$orderby=nombre")

        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun registrarSoporteSolicitudPermiso(
    context: Context,
    callback: IRespuestaServidor,
    parametros: JSONObject
) {
    try {
        val url = HOST.plus("api/SoporteSolicitudPermisos")
        SolicitudVolley.getInstance(context).jsonRequest(url, parametros, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerSoportePermiso(
    context: Context,
    callback: IRespuestaServidor,
    funcionarioId :String,
    solicitudPermisoId :String? = null
) {
    try {
        var filtrosolicitudPermisoId = "  "
        if (solicitudPermisoId!=null){
            filtrosolicitudPermisoId= "  "//""and   solicitudPermisoId eq $solicitudPermisoId "
        }
        val url =
            HOST.plus("odata/SoporteSolicitudPermisos?\$filter=solicitudPermiso/funcionarioId eq $funcionarioId $filtrosolicitudPermisoId and " +
                    "estadoRegistro eq 'Activo' &\$orderby=id &\$Select=id,solicitudPermisoId,comentario,adjunto " +
                    "&\$expand=tipoSoporte(\$select=id,nombre),solicitudPermiso(\$select=funcionarioId)")

        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun elimianrSoporteSolicitudPermiso(
    context: Context,
    Id: Int,
    callback: IRespuestaServidor
) {
    try {
        val url = HOST.plus("api/SoporteSolicitudPermisos/$Id")

        SolicitudVolley.getInstance(context).jsonRequestDelete (url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun optenerDatosGraficasInicio(
    context: Context,
    callback: IRespuestaServidor,
    funcionarioId :String
) {
    try {

        val url = HOST.plus("api/Dashboards/GraficasMovil/$funcionarioId")
        val parametros = JSONObject().apply {
            put("funcionarioId","$funcionarioId")
        }


        SolicitudVolley.getInstance(context).jsonRequest(url, parametros, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun optenerSolicitudCesantias(
    context: Context,
    callback: IRespuestaServidor,
    funcionarioId :String
) {
    try {

        val url = HOST.plus("odata/SolicitudCesantias?\$orderby=fechaSolicitud desc & " +
                "\$filter=funcionarioId eq $funcionarioId  and estadoRegistro eq 'Activo' & " +
                "\$select=id,funcionarioId,fechaSolicitud,baseCalculoCesantia,valorSolicitado,soporte,observacion,estado& " +
                "\$expand=MotivoSolicitudCesantia(\$select=id, nombre)")

        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun cancelarSolicitudCesantias(
    context: Context,
    solicitudPermisoId: Int,
    parametros: JSONObject,
    callback: IRespuestaServidor
) {
    try {
        val url = HOST.plus("api/SolicitudCesantias/Estado/$solicitudPermisoId")

        SolicitudVolley.getInstance(context).jsonPatchRequest (url, parametros, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun optenerMotivoSolicitudCesantias(
    context: Context,
    callback: IRespuestaServidor
) {
    try {

        val url = HOST.plus("odata/motivoSolicitudCesantias?\$filter=estadoRegistro  eq 'Activo'")

        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

    fun obtenerDatosActualesSolicitiud(
    context: Context,
    callback: IRespuestaServidor,
    funcionarioId: String
) {
    try {

       val url = HOST.plus("api/SolicitudCesantias/DatosCesantias/$funcionarioId?")

        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}


fun registrarSolicitudCesantias(
    context: Context,
    callback: IRespuestaServidor,
    parametros: JSONObject
) {
    try {
        val url = HOST.plus("api/SolicitudCesantias")
        SolicitudVolley.getInstance(context).jsonRequest(url, parametros, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun editarSolicitudCesantias(
    context: Context,
    callback: IRespuestaServidor,
    parametros: JSONObject,
    id: Int
) {
    try {
        val url = HOST.plus("api/SolicitudCesantias/$id")
        SolicitudVolley.getInstance(context)
            .jsonPutRequest(url, parametros, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun optenerSolicitudVacaciones(
    context: Context,
    callback: IRespuestaServidor,
    funcionarioId :String
) {
    try {

        val url = HOST.plus("odata/SolicitudVacaciones?\$orderby=fechaInicioDisfrute  desc " +
                "&\$filter= funcionarioId eq $funcionarioId &\$select=id,fechaInicioDisfrute,diasDisfrute," +
                "fechaFinDisfrute,diasDinero,observacion,estado,justificacion &\$expand=libroVacaciones(" +
                "\$select=id,inicioCausacion,finCausacion,diasDisponibles,tipo,diasTrabajados)")

        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun cancelarSolicitudVacaciones(
    context: Context,
    solicitudVacacionesId: Int,
    parametros: JSONObject,
    callback: IRespuestaServidor
) {
    try {
        val url = HOST.plus("api/SolicitudVacaciones/estado/$solicitudVacacionesId")

        SolicitudVolley.getInstance(context).jsonPatchRequest (url, parametros, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun optenerInterrupcionesVacaciones(
    context: Context,
    callback: IRespuestaServidor,
    solicitudVacacionesId  :String
) {
    try {

        val url = HOST.plus("odata/SolicitudVacacionesInterrupciones?\$orderby=ausentismoFuncionario/fechaInicio desc " +
                "&\$filter=solicitudVacacionesId eq $solicitudVacacionesId " +
                "&\$select= id &\$expand=AusentismoFuncionario(\$select=fechaInicio,fechaFin,justificacion;" +
                "\$expand=tipoAusentismo(\$select=nombre))")

        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerPeriodoMasAntiguo(
    context: Context,
    callback: IRespuestaServidor,
    contratoId  :String
) {
    try {

        val url = HOST.plus("odata/LibroVacaciones?\$top=1 &\$orderby=inicioCausacion& \$filter=contratoId eq $contratoId")

        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)
    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun registrarSolicitudVacaciones(
    context: Context,
    callback: IRespuestaServidor,
    parametros: JSONObject
) {
    try {
        val url = HOST.plus("api/SolicitudVacaciones")
        SolicitudVolley.getInstance(context).jsonRequest(url, parametros, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun editarSolicitudVacaciones(
    context: Context,
    callback: IRespuestaServidor,
    parametros: JSONObject,
    id: Int
) {
    try {
        val url = HOST.plus("api/SolicitudVacaciones/$id")
        SolicitudVolley.getInstance(context)
            .jsonPutRequest(url, parametros, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}


fun obtenerDesprendibles(
    context: Context,
    callback: IRespuestaServidor,
    idFuncionario: String
) {
    try {
        val url = HOST.plus("api/desprendiblepagos/$idFuncionario")


        //val url = "http://nomintegra.alcanosesp.com:9001/api/desprendiblepagos/5647"
        Log.i("JUICIO", url)
        SolicitudVolley.getInstance(context).jsonRequest(url, null, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}

fun obtenerDesprendible(
    context: Context,
    callback: IRespuestaServidor,
    parametros: JSONObject
) {
    try {
        val url = HOST.plus("reporte/desprendiblepagos")


        //val url = "http://nomintegra.alcanosesp.com:9001/reporte/desprendiblepagos"
        Log.i("JUICIO", url)
        SolicitudVolley.getInstance(context).jsonRequest(url, parametros, headers(), callback, c401)

    } catch (e: Exception) {
        Log.d(TAG, e.message!!)
    }
}