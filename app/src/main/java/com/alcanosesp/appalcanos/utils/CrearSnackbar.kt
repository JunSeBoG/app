package com.alcanosesp.appalcanos.utils

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.alcanosesp.appalcanos.R
import com.google.android.material.snackbar.Snackbar

fun crearSnackbar(vista: View, context: Context,exito:Boolean):Snackbar{
    return Snackbar.make(vista, "Snackbar", 2600).apply {

        when (exito) {
            true -> {
                setText("Información actualizada.")
                vista.setBackgroundColor(context.getColor(R.color.verde_pera))
            }
            false -> {
                setText("Ha ocurrido un error al procesar la información.")
                vista.setBackgroundColor(context.getColor(R.color.rojo))
            }
        }

        vista.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).apply {
            typeface = ResourcesCompat.getFont(context, R.font.muli_regular)
        }

        vista.findViewById<TextView>(com.google.android.material.R.id.snackbar_action).apply {
            typeface = ResourcesCompat.getFont(context, R.font.muli_bold)
            isAllCaps = false
        }

        setAction("Aceptar") {
            dismiss()
        }
    }
}