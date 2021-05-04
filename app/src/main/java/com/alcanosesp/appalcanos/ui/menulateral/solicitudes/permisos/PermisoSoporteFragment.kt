package com.alcanosesp.appalcanos.ui.menulateral.solicitudes.permisos

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.alcanosesp.appalcanos.App
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.adapter.SpinnerAdapter
import com.alcanosesp.appalcanos.api.IRespuestaServidor
import com.alcanosesp.appalcanos.api.crearArchivoServer
import com.alcanosesp.appalcanos.api.obtenerSoportePermiso
import com.alcanosesp.appalcanos.api.registrarSoporteSolicitudPermiso
import com.alcanosesp.appalcanos.databinding.FragmentPermisoSoporteBinding
import com.alcanosesp.appalcanos.db.entity.SolicitudPermisoSoporte
import com.alcanosesp.appalcanos.model.ItemSpinner
import com.alcanosesp.appalcanos.utils.Validador
import com.alcanosesp.appalcanos.utils.bitMapToString
import com.alcanosesp.appalcanos.utils.construirAlerta
import com.alcanosesp.appalcanos.utils.uriToBitmap
import com.android.volley.VolleyError
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.io.IOException


class PermisoSoporteFragment : Fragment() {
    private val vmPermiso by lazy {
        ViewModelProviders.of(this).get(PermisoViewModel::class.java)
    }

    private lateinit var binding: FragmentPermisoSoporteBinding
    private lateinit var adaptadorTipoSoporte: SpinnerAdapter
    private val FOTO_GALERIA = 1
    private val TOMO_FOTO = 2
    private val DOCUMENTO = 3
    private var soporteImg: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adaptadorTipoSoporte = SpinnerAdapter(context!!)


        vmPermiso.obtenerTipoSoporteApi(context!!)
        vmPermiso.listaTipoSoporte.observe(
            this,
            Observer { this.adaptadorTipoSoporte.setItems(it) })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_permiso_soporte, container, false)

        binding.sPermisoSoporteTipo.adapter = adaptadorTipoSoporte
        binding.sPermisoSoporteTipo.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val seleccio = parent?.getItemAtPosition(position) as ItemSpinner
                    vmPermiso.idTipoSoporte = seleccio.id
                }

            }


        binding.llSeleccionarDocumentoSoportePermiso.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "image/*"
                }

                startActivityForResult(intent, DOCUMENTO)
            } catch (ex: IOException) {
                Toast.makeText(context, "Error al cargar fichero de documento.", Toast.LENGTH_LONG)
                    .show()
            }
        }

        binding.llSeleccionarFotoSoportePermiso.setOnClickListener {
            try {
                val iGaleria =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                iGaleria.type = "image/*"
                startActivityForResult(
                    iGaleria,
                    FOTO_GALERIA
                )
            } catch (ex: IOException) {
                Toast.makeText(context, "Error al cargar fichero de imagen.", Toast.LENGTH_LONG)
                    .show()
            }
        }

        binding.llTomarFotoSoportePermiso.setOnClickListener {
            try {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                // if (takePictureIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(takePictureIntent, TOMO_FOTO)
                // }
            } catch (ex: IOException) {
                Toast.makeText(context, "Error al crear fichero de imagen.", Toast.LENGTH_LONG)
                    .show()
            }
        }

        binding.btnGuardarSoportePermiso.setOnClickListener {
            if (soporteImg == null) {
                construirAlerta(
                    context!!,
                    R.layout.toas_login_warning,
                    getString(R.string.debe_seleccionar_un_soporte)
                )
            } else {
                cerrarTeclado()
                if (validarAntesdeEnviar()) {
                    enviarSolicitud()
                } else {
                    crearSnackbar(false).show()
                }
            }
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == FOTO_GALERIA) || (requestCode == DOCUMENTO)) {
            if (resultCode == Activity.RESULT_OK) {
                val selectedImage = data?.data
                val imageBitmap = uriToBitmap(context!!, selectedImage!!)
                val imgString: String = bitMapToString(imageBitmap!!)

                cargarImagen(imgString)
            } else {
                Toast.makeText(context, "No selecciono una imagen.", Toast.LENGTH_LONG).show()
            }
        } else if (requestCode == TOMO_FOTO) {
            if (resultCode == Activity.RESULT_OK) {
                val extras = data?.extras
                val imageBitmap = extras?.get("data") as Bitmap?
                val imgString: String = bitMapToString(imageBitmap!!)

                cargarImagen(imgString)
            } else {
                Toast.makeText(context, "No tomó una foto.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validarAntesdeEnviar(): Boolean {
        val validador = Validador()
        var retorno = true

        if (!validador.spinnerRequerido(
                binding.sPermisoSoporteTipo,
                binding.msgErrorSoportePermisoTipoSoporte
            )
        ) retorno = false

        return retorno
    }

    private fun enviarSolicitud() {
        binding.btnGuardarSoportePermiso.isEnabled = false


        val parametros = JSONObject().apply {
            put("solicitudPermisoId", App.solicitudPermiso?.id)
            put("tipoSoporteId", vmPermiso.idTipoSoporte)
            put("comentario", binding.edSoportePermisoComentario.text.toString().trim())
            put("adjunto", soporteImg)
        }


        val callbackRegistarSolicitudPermiso = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())

                mostarDialogoCancelar()

                crearSnackbar(true).show()
            }

            override fun error(error: VolleyError) {
                binding.btnGuardarSoportePermiso.isEnabled = true
                val errors = String(error?.networkResponse.data).toString()
                crearSnackbar(false).show()
                obtenerMensajesBacked(JSONObject(errors).getJSONObject("errors"))
            }
        }

        registrarSoporteSolicitudPermiso(context!!, callbackRegistarSolicitudPermiso, parametros)

    }

    private fun cerrarTeclado() {
        val vista = activity?.currentFocus
        if (vista != null) {
            val input =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            input.hideSoftInputFromWindow(vista.windowToken, 0)
        }
    }

    private fun cargarImagen(imgB64: String) {
        val callbackArchivoAdjunto = object : IRespuestaServidor {

            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val objectid = json.getString("object_id")
                binding.tvConfirmaCargaSoportePermiso.isVisible = true
                soporteImg = objectid
            }

            override fun error(error: VolleyError) {
                Log.e("Error", "CargarAdjunto volley ${error?.networkResponse?.statusCode}")
            }
        }

        crearArchivoServer(context!!, imgB64, callbackArchivoAdjunto)
    }

    private fun crearSnackbar(exito: Boolean): Snackbar {
        return Snackbar.make(view!!, "Snackbar", 2600).apply {

            val vista = view

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
                typeface = ResourcesCompat.getFont(context!!, R.font.muli_regular)
            }

            vista.findViewById<TextView>(com.google.android.material.R.id.snackbar_action).apply {
                typeface = ResourcesCompat.getFont(context!!, R.font.muli_bold)
                isAllCaps = false
            }

            setAction("Aceptar") {
                dismiss()
            }
        }
    }

    private fun obtenerMensajesBacked(jsonErrors: JSONObject) {
        if (jsonErrors.has("solicitudPermisoId")) {
            val errores = jsonErrors.getJSONArray("solicitudPermisoId")
            val mensajes = StringBuilder()
            for (i in 0 until errores.length()) {
                mensajes.append(errores[i].toString() + "\n")
            }
            construirAlerta(context!!, R.layout.toas_login_warning, mensajes.toString())
        }
        if (jsonErrors.has("tipoSoporteId")) {
            val errores = jsonErrors.getJSONArray("tipoSoporteId")
            val mensajes = StringBuilder()
            for (i in 0 until errores.length()) {
                mensajes.append(errores[i].toString() + "\n")
            }
            binding.msgErrorSoportePermisoTipoSoporte.text = mensajes.toString()
        }
        if (jsonErrors.has("comentario")) {
            val errores = jsonErrors.getJSONArray("comentario")
            val mensajes = StringBuilder()
            for (i in 0 until errores.length()) {
                mensajes.append(errores[i].toString() + "\n")
            }
            binding.msgErrorSoportePermisoComentario.text = mensajes.toString()
        }
    }

    fun obtenerSoportePermisoApi(redirecionar: Boolean = false) {
        val callbackSoportePermiso = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                vmPermiso.eliminarSoporteSolicitudPermiso()
                val json = JSONObject(respuesta.toString())
                val valueArr = json.getJSONArray("value")


                for (i in 0 until valueArr.length()) {
                    val item = valueArr.getJSONObject(i)
                    val permiso = SolicitudPermisoSoporte(item, (i + 1).toString())

                    vmPermiso.insertarSoporteSolicitudPermiso(permiso)
                }

                if (redirecionar) {
                    val handler = Handler()

                    handler.postDelayed(
                        {
                            findNavController().navigate(R.id.action_permisoSoporteFragment_to_permisoFragment)
                        },
                        1500
                    )
                }
            }

            override fun error(error: VolleyError) {
                val codigo = error.networkResponse.statusCode
                construirAlerta(
                    context!!,
                    R.layout.toas_login_warning,
                    getString(R.string.mensaje_eror_404, codigo.toString())
                )
            }
        }
        obtenerSoportePermiso(context!!, callbackSoportePermiso, App.idFuncionario.toString())
    }

    private fun mostarDialogoCancelar() {
        val vista = LayoutInflater.from(context).inflate(R.layout.dialogo_botones, null)
        val textView = vista.findViewById<TextView>(R.id.texto_dialog)
        textView.text = getString(R.string.desea_cargar_otro_soporte)

        val botonAceptar = vista.findViewById<Button>(R.id.boton_dialog)
        val botonCancelar = vista.findViewById<Button>(R.id.boton_dialog_cancel)

        val builder = AlertDialog.Builder(context)
        builder.apply {
            setView(vista)
            create()
        }
        val dialogo = builder.show()
        dialogo.setCancelable(false)
        botonAceptar.setOnClickListener {
            soporteImg = null
            binding.tvConfirmaCargaSoportePermiso.isVisible = false
            binding.sPermisoSoporteTipo.setSelection(0)
            binding.edSoportePermisoComentario.text = null
            binding.btnGuardarSoportePermiso.isEnabled = true
            obtenerSoportePermisoApi()
            dialogo.dismiss()
        }

        botonCancelar.setOnClickListener {
            dialogo.dismiss()
            obtenerSoportePermisoApi(true)
        }
    }
}
