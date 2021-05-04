package com.alcanosesp.appalcanos.utils

import android.app.TimePickerDialog
import android.widget.EditText
import androidx.fragment.app.FragmentManager
import com.alcanosesp.appalcanos.adapter.TimePickerAdapter

fun mostrarDateDialoghora(fragmenManager: FragmentManager, et: EditText) {

    val newFragment = TimePickerAdapter.newInstance(TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        val horaSeleccionada = hourOfDay.dosDigitos()
        val minutoSeleccionado = minute.dosDigitos()

        val horaseleccionada = "$horaSeleccionada : $minutoSeleccionado"
        et.setText(horaseleccionada)
    })


    newFragment.show(fragmenManager, "timePicker")
}