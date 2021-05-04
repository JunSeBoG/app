package com.alcanosesp.appalcanos.adapter

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.alcanosesp.appalcanos.R
import java.util.*

class TimePickerAdapter:DialogFragment() {
    private var listener: TimePickerDialog.OnTimeSetListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return TimePickerDialog(context!!,R.style.datePickerStyle, listener,hour,minute,false)
    }

    companion object {
        fun newInstance(listener: TimePickerDialog.OnTimeSetListener): TimePickerAdapter {
            val fragment = TimePickerAdapter()
            fragment.listener = listener
            return fragment
        }
    }
}