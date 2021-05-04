package com.alcanosesp.appalcanos.adapter

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.alcanosesp.appalcanos.R
import java.util.*

class DatePickerAdapter : DialogFragment(){

    private var listener: DatePickerDialog.OnDateSetListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(context!!, R.style.datePickerStyle, listener, year, month, day)
    }

    companion object {
        fun newInstance(listener: DatePickerDialog.OnDateSetListener): DatePickerAdapter {
            val fragment = DatePickerAdapter()
            fragment.listener = listener
            return fragment
        }
    }
}