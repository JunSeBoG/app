package com.alcanosesp.appalcanos.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity

fun cerrarTeclado(activity: FragmentActivity?){
    val vista = activity?.currentFocus
    if (vista != null) {
        val input =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        input.hideSoftInputFromWindow(vista.windowToken, 0)
    }
}