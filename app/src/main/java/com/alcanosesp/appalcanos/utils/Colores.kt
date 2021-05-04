package com.alcanosesp.appalcanos.utils

import com.alcanosesp.appalcanos.R
import java.util.*
import kotlin.collections.HashMap

val listaColores = ArrayList<Int>().apply {
    add(R.color.magenta)
    add(R.color.fuego)
    add(R.color.azul_verdoso)
    add(R.color.ultravioleta)
    add(R.color.azul_rey)
    add(R.color.salmon)
    add(R.color.castano)
    add(R.color.naranja)
    add(R.color.mandarina)
    add(R.color.purpura)
    add(R.color.turquesa)
    add(R.color.nude)
    add(R.color.carmesi)
    add(R.color.verde_menta)
    add(R.color.coral)
    add(R.color.verde_lima)
}

val estadosInformacion = HashMap<String, Int?>().apply {
    put("", R.color.blanco)
    put("null", R.color.blanco)
    put("Pendiente", R.color.purpura)
    put("Validado", R.color.verde_pera)
    put("Rechazado", R.color.carmesi)
}

val estadosAusentismoFuncionario = HashMap<String, Int?>().apply {
    put("", R.color.blanco)
    put("null", R.color.blanco)
    put("Registrado", R.color.mandarina)
    put("Procesado", R.color.azul_verdoso)
    put("Aprobado", R.color.verde_pera)
    put("Anulado", R.color.carmesi)
    put("Finalizado", R.color.azul_verdoso)

}

val estadosBeneficiosNombres = HashMap<String, String>().apply {
    put("", "")
    put("Aprobada", "Aprobada")
    put("EnTramite", "En trámite")
    put("EnReembolso", "En reembolso")
    put("Autorizada", "Autorizada")
    put("Condonada", "Condonada")
    put("EnCondonacion", "En condonación")
    put("Cancelada", "Cancelada")
    put("Otorgada", "Otorgada")
    put("Rechazada", "Rechazada")
    put("Finalizada", "Finalizada")

    put("Solicitada", "Solicitada")

    //Vacaciones
    put("EnCurso","En curso")
    put("Interrumpida","Interrumpida")
    put("Solicitada","Solicitada")
    put("Terminada","Terminada")
}

val opcionAxulioEducativoNombres = HashMap<String, String>().apply {
    put("", "")
    put("Opcion1Condonacion", "Opción 1: condonación")
    put("Opcion2Condonacionyfinanciacion", "Opción 2: condonación y financiación")
}
val opcionAxulioEducativoNombreC = HashMap<String, String>().apply {
    put("", "")
    put("Opción 1: condonación", "Opcion1Condonacion")
    put("Opción 2: condonación y financiación", "Opcion2Condonacionyfinanciacion")
}

val estadosBeneficiosdash = HashMap<String, Int?>().apply {
    put("EnTramite", R.color.naranja)
    put("Rechazada", R.color.amarillo_solicitudes)
    put("Aprobada", R.color.verde_pera)
    put("Cancelada", R.color.naranja_claro)
    put("Autorizada", R.color.turquesa)
    put("Otorgada", R.color.purpura)
    put("Finalizada", R.color.verde_menta)
    put("EnCondonacion",  R.color.magenta)
    put("EnReembolso", R.color.azul_verdoso)
    put("Condonada", R.color.coral)
    put("null", R.color.blanco)
    put("", R.color.blanco)
}

val estadosBeneficios = HashMap<String, Int?>().apply {
    put("Aprobada", R.color.verde_pera)
    put("Autorizada", R.color.turquesa)
    put("Otorgada", R.color.azul_rey)
    put("EnTramite", R.color.purpura)
    put("Condonada", R.color.coral)
    put("Rechazada", R.color.carmesi)
    put("EnReembolso", R.color.fuego)
    put("Cancelada", R.color.salmon)
    put("Finalizada", R.color.azul_verdoso)
    put("EnCondonacion",  R.color.magenta)
    put("null", R.color.blanco)
    put("", R.color.blanco)
}

val estadosPermisos = HashMap<String, Int?>().apply {
    put("", R.color.blanco)
    put("null", R.color.blanco)
    put("Aprobada", R.color.barra4)
    put("Autorizada", R.color.barra5)
    put("Cancelada", R.color.barra2)
    put("Solicitada", R.color.barra1)
    put("Rechazada", R.color.barra3)
}

val estaditos = HashMap<String, Int?>().apply {
    put("", R.color.blanco)
    put("null", R.color.blanco)
    put("Solicitada", R.color.barra1)
    put("Cancelada", R.color.barra2)
    put("Rechazada", R.color.barra3)
    put("Aprobada", R.color.barra4)
    put("Autorizada", R.color.barra5)
}

val estadosVacaciones = HashMap<String, Int?>().apply {
    put("Aprobada", R.color.verde_pera)
    put("Autorizada", R.color.turquesa)
    put("Cancelada", R.color.salmon)
    put("EnCurso", R.color.azul_rey)
    put("Interrumpida", R.color.fuego)
    put("Rechazada", R.color.carmesi)
    put("Solicitada", R.color.purpura)
    put("Terminada", R.color.azul_verdoso)
    put("null", R.color.blanco)
    put("", R.color.blanco)
}