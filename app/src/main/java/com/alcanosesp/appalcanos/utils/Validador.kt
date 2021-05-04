package com.alcanosesp.appalcanos.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.model.ItemSpinner
import java.util.regex.Pattern


class Validador {

    //EXPRESIONES REGULARES
    /*private val EXP_REG_ALFABETICO : String  = "^[a-zA-Z ]+$"
    private val EXP_REG_ALFANUMERICO : String  = "^[0-9A-Za-zäÄëËïÏöÖüÜáéíóúáéíóúÁÉÍÓÚñÑ\\\\s]*\$"
    private val EXP_REG_DIRECCION : String  = "^[-a-zA-Z0-9# ]+$"*/

    private val EXP_REG_ALFABETICO: String = "^[A-Za-zäÄëËïÏöÖüÜáéíóúáéíóúÁÉÍÓÚñÑ ]+$"
    private val EXP_REG_NUMERICO: String = "^[0-9]+$"
    private val EXP_REG_ALFANUMERICO: String = "^[0-9A-Za-zäÄëËïÏöÖüÜáéíóúáéíóúÁÉÍÓÚñÑ \\\\s]*\$"
    private val EXP_REG_DIRECCION: String = "^[-A-Za-zäÄëËïÏöÖüÜáéíóúáéíóúÁÉÍÓÚñÑ0-9# ]+$"
    private val EXP_REG_CELULAR: String = "^\\w{10}$"
    private val EXP_REG_TELEFONO: String = "^\\w{7}$"
    private val EXP_REG_CORREO: String =
        "[\\w\\._-]{1,30}\\+?[\\w]{0,10}@[\\w\\.\\-]{3,}\\.\\w{2,5}"
    private val EXP_REG_TARJETAPROFESIONAL: String = "^[A-Z0-9\\s-_Ñ]+$"

    //MENSAJES DE ERROR
    private val MSG_REQUERIDO: String = "Requerido"
    private val MSG_ALFABETICO: String = "El campo debe ser alfabético."
    private val MSG_NUMERICO: String = "El campo debe ser numérico."
    private val MSG_ALFANUMERICO: String = "El campo debe contener números y letras."
    private val MSG_DIRECCION: String = "No es un formato de dirección válido."
    private val MSG_CORREO: String = "El correo eletrónico ingresado no es válido."
    private val MSG_CELULAR: String = "Rango permitido de 1000000000 - 9999999999."
    private val MSG_TELEFONO: String = "Rango permitido de 1000000 - 9999999."
    private val MSG_TARJETAPROFESIONAL: String =
        "El campo debe contener números, letras en mayúscula y guión."

    //archivos e imagenes
    var PESO_MAXIMO_IMAGEN = 5228304 //5Mb
    var PESO_MAXIMO_PDF = 5228304 //5Mb


    fun campoRequerido(editText: EditText, textView: TextView): Boolean {
        return validarTextoRequerido(editText, textView, MSG_REQUERIDO)
    }

    fun spinnerRequerido(spinner: Spinner, textView: TextView): Boolean {
        return validarSpinnerRequerido(spinner, textView, MSG_REQUERIDO)
    }

    fun campoAlfabetico(editText: EditText, textView: TextView, esRequerido: Boolean): Boolean {
        return validarCampo(editText, textView, EXP_REG_ALFABETICO, MSG_ALFABETICO, esRequerido)
    }

    fun campoNumerico(editText: EditText, textView: TextView, esRequerido: Boolean): Boolean {
        return validarCampo(editText, textView, EXP_REG_NUMERICO, MSG_NUMERICO, esRequerido)
    }

    fun campoAlfanumerico(editText: EditText, textView: TextView, esRequerido: Boolean): Boolean {
        return validarCampo(editText, textView, EXP_REG_ALFANUMERICO, MSG_ALFANUMERICO, esRequerido)
    }

    fun campoDireccion(editText: EditText, textView: TextView, esRequerido: Boolean): Boolean {
        return validarCampo(editText, textView, EXP_REG_DIRECCION, MSG_DIRECCION, esRequerido)
    }

    fun campoCelular(editText: EditText, textView: TextView, esRequerido: Boolean): Boolean {
        return validarCampo(editText, textView, EXP_REG_CELULAR, MSG_CELULAR, esRequerido)
    }

    fun campoTelefono(editText: EditText, textView: TextView, esRequerido: Boolean): Boolean {
        return validarCampo(editText, textView, EXP_REG_TELEFONO, MSG_TELEFONO, esRequerido)
    }

    fun campoCorreo(editText: EditText, textView: TextView, esRequerido: Boolean): Boolean {
        return validarCampo(editText, textView, EXP_REG_CORREO, MSG_CORREO, esRequerido)
    }

    fun campoTarjetaProfesional(
        editText: EditText,
        textView: TextView,
        esRequerido: Boolean
    ): Boolean {
        return validarCampo(
            editText,
            textView,
            EXP_REG_TARJETAPROFESIONAL,
            MSG_TARJETAPROFESIONAL,
            esRequerido
        )
    }

    fun textViewRequerido(text: TextView, textView: TextView): Boolean {
        return validarTextViewRequerido(text, textView, MSG_REQUERIDO)
    }

    fun valorRequerido(string: String?, textView: TextView): Boolean {
        return validarValorRequerido(string, textView, MSG_REQUERIDO)
    }

    private fun validarValorRequerido(
        string: String?,
        textView: TextView,
        msgError: String
    ): Boolean {

        val texto: String? = string.toString()
        textView.text = ""

        if (texto.isNullOrEmpty()) {
            textView.text = msgError
            return false
        }

        return true
    }

    private fun validarCampo(
        editText: EditText,
        textView: TextView,
        regex: String,
        msgError: String,
        esRequerido: Boolean
    ): Boolean {
        val texto = editText.text.toString().trim()
        textView.text = ""

        if (esRequerido) {
            if (campoRequerido(editText, textView)) {
                if (!Pattern.matches(regex, texto)) {
                    textView.text = msgError
                    Log.i("CAMPO", "error")
                    return false
                }
            } else {
                return false
            }
        }

        if (!esRequerido && texto.isNotEmpty()) {
            if (!Pattern.matches(regex, texto)) {
                textView.text = msgError
                Log.i("CAMPO", "formato")
                return false
            }
        }

        return true
    }

    private fun validarTextoRequerido(
        editText: EditText,
        textView: TextView,
        msgError: String
    ): Boolean {

        val texto = editText.text.toString().trim()
        textView.text = ""

        if (texto.isEmpty()) {
            textView.text = msgError
            return false
        }

        return true
    }

    private fun validarTextViewRequerido(
        text: TextView,
        textView: TextView,
        msgError: String
    ): Boolean {

        val texto = text.text.toString().trim()
        textView.text = ""

        if (texto.isEmpty()) {
            textView.text = msgError
            return false
        }

        return true
    }

    private fun validarSpinnerRequerido(
        spinner: Spinner,
        textView: TextView,
        msgError: String
    ): Boolean {

        val item = spinner.selectedItem as ItemSpinner
        textView.text = ""

        if (item.nombre?.isEmpty()!!) {
            textView.text = msgError
            return false
        }

        return true
    }

    fun pesoPermitidoImagen(
        context: Context,
        bitmap: Bitmap,
        tamanioPermitido: Int = PESO_MAXIMO_IMAGEN
    ): Boolean {
        return if (bitmap.width * bitmap.height < tamanioPermitido) {
            true
        } else {
            construirAlerta(
                context,
                R.layout.toas_login_warning,
                context.getString(R.string.error_tamanio_archivo)
            )
            false
        }
    }

    fun ajustarPesoPermitido(context: Context, bitmap: Bitmap, tamanioPermitido :Int = PESO_MAXIMO_IMAGEN): Bitmap {

        return if (bitmap.width * bitmap.height < tamanioPermitido) {
            bitmap
        } else {
            convertirBitmapTamanioPermitido(bitmap, tamanioPermitido)
        }
    }

    fun validarPesoPermitidoPdf(context: Context, peso: Int, tamanioPermitido :Int = PESO_MAXIMO_PDF): Boolean {
        return if (peso < tamanioPermitido) {
            true
        } else {
            construirAlerta(
                context,
                R.layout.toas_login_warning,
                context.getString(R.string.error_tamanio_archivo)
            )
            false
        }
    }
}