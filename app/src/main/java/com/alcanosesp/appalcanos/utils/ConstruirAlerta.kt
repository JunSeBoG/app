package com.alcanosesp.appalcanos.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.alcanosesp.appalcanos.R

fun construirAlerta(context: Context , dialogo: Int, texto: String): AlertDialog {

    val vista = LayoutInflater.from(context).inflate(dialogo, null)
    val textView = vista.findViewById<TextView>(R.id.texto_dialog)
    textView.text = texto

    val botonAceptar = vista.findViewById<Button>(R.id.boton_dialog)

    val builder = AlertDialog.Builder(context)
    builder.apply {
        setView(vista)
        create()
    }

    val dialog = builder.show()

    botonAceptar.setOnClickListener {
        dialog.dismiss()
    }

    return dialog
}