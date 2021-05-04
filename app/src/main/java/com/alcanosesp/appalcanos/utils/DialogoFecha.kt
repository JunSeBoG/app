package com.alcanosesp.appalcanos.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.EditText
import android.widget.TimePicker
import androidx.fragment.app.FragmentManager
import com.alcanosesp.appalcanos.adapter.DatePickerAdapter
import com.alcanosesp.appalcanos.adapter.TimePickerAdapter
import java.util.*


fun mostrarDateDialogFecha(fragmenManager: FragmentManager, et: EditText) {
    val newFragment =
        DatePickerAdapter.newInstance(DatePickerDialog.OnDateSetListener { _, anio, mes, dia ->
            val diaSeleccionado = dia.dosDigitos()
            val mesSeleccionado = (mes + 1).dosDigitos()

            val fechaSeleccionada = "$anio - $mesSeleccionado - $diaSeleccionado"
            et.setText(fechaSeleccionada)
        })

    newFragment.show(fragmenManager, "datePicker")
}




