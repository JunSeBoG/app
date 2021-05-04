package com.alcanosesp.appalcanos.utils

import me.abhinay.input.CurrencyEditText
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import kotlin.math.round

fun monedaSindecimales(currencyEditText: CurrencyEditText){
    currencyEditText.apply {
        setSpacing(false)
        setDecimals(false)
        setSeparator(".")
        setCurrency("$ ")
    }
}

fun monedaCondecimales(currencyEditText: CurrencyEditText){
    currencyEditText.apply {
        setSpacing(false)
        setDecimals(true)
        setSeparator(".")

    }
}

fun moneda(s: String?): String{
    val a = s
    return if (s == "0" || s == "" || s == null){
        "0.00"
    }else{
        val symbols = DecimalFormatSymbols().apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }

        val decimalFormat = DecimalFormat("#,###.00", symbols)

        val redondear2deciamles = round(s.toDouble()* 100) /100

        decimalFormat.format(redondear2deciamles)
    }
}

