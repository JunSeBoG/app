package com.alcanosesp.appalcanos.ui.menulateral.perfil

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider.getUriForFile
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alcanosesp.appalcanos.App
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.api.*
import com.alcanosesp.appalcanos.db.entity.Funcionario
import com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos.BasicosViewModel
import com.alcanosesp.appalcanos.utils.*
import com.android.volley.VolleyError
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.io.File

class CambiarAvatarActivity : AppCompatActivity() {
    private val vmFuncionario by lazy {
        ViewModelProviders.of(this).get(BasicosViewModel::class.java)
    }

    private var photoFile: File? = null
    private val GALERIA = 1
    private val TOMAR_FOTO = 2
    private var id: String? = App.idFuncionario.toString()
    private var objectId: String? = null
    private var identificacion: String? = null
    private var imageNueva: Bitmap? = null

    private lateinit var imagenPerfil: CircleImageView
    private lateinit var progressBar: LinearLayout
    private lateinit var conetenido: ConstraintLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_cambiar_avatar)

        imagenPerfil = findViewById(R.id.cambiarAvatarFuncionario)
        progressBar = findViewById(R.id.pbCambairAvatar)
        conetenido = findViewById(R.id.llCambiarAbatar)

        obtenerFuncionario()

        vmFuncionario.funcionario.observe(this, Observer {

            identificacion = it.numeroDocumento
            if (it.adjunto.toString().isNotEmpty()) objectId = it.adjunto
            if (it.foto.isNullOrEmpty()) {
                imagenPerfil.setImageResource(R.drawable.empty_personaje)
            } else {
                imagenPerfil.setImageBitmap(stringToBitMap(it.foto!!))
            }
        })

    }

    fun clickTomarFoto(view: View) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                photoFile = try {
                    crearArchivoImagen(this)
                } catch (ex: Exception) {
                    // Error occurred while creating the File
                    Log.e("errorTomarFoto", "sss")
                    null

                }

                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = getUriForFile(
                        this,
                        "com.alcanosesp.fileprovider",
                        photoFile!!
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, TOMAR_FOTO)
                }
            }
        }
    }

    fun clickSeleccionarFoto(view: View) {
        val iGaleria = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        iGaleria.type = "image/*"
        startActivityForResult(
            iGaleria,
            GALERIA
        )
    }


    fun clickEliminarFoto(view: View) {
        if (objectId.isNullOrEmpty()) {
            Toast.makeText(this, "No existe imagen", Toast.LENGTH_LONG).show()
        } else {
            mostrarProgresbar(true)
            var callback = object : IRespuestaServidor {

                override fun exito(respuesta: Any?) {
                    funcionarioAdjuntonull()
                }

                override fun error(error: VolleyError) {
                    construirAlerta(
                        this@CambiarAvatarActivity,
                        R.layout.toas_login_warning,
                        "Error al eliminar Foto, inetenta de nuevo."
                    )
                    mostrarProgresbar(false)
                }
            }
            eliminarArchivoServer(this, objectId.toString(), callback)

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val validador = Validador()


        if (requestCode == GALERIA) {
            mostrarProgresbar(true)
            if (resultCode == RESULT_OK) {

                val selectedImage = data?.data
                val imageBitmap = uriToBitmapNoCambiaTamanio(this, selectedImage!!)
                imageNueva = validador.ajustarPesoPermitido(this, imageBitmap, 1228304)
                val imgString: String = bitMapToString(imageNueva!!)

                crearImagenApi(imgString)

                mostrarProgresbar(false)

            } else {
                Toast.makeText(this, "No seleccionaste una foto.", Toast.LENGTH_LONG).show()
                mostrarProgresbar(false)
            }
        } else if (requestCode == TOMAR_FOTO) {
            if (resultCode == RESULT_OK && photoFile != null) {
                mostrarProgresbar(true)

                val filePath = photoFile?.path
                val imageBitmap = BitmapFactory.decodeFile(filePath)
                val rotation = obtenerRatacionIMagen(filePath!!)
                val rotatedBitmap = ajustarRotacionimagen(rotation, imageBitmap)
                imageNueva = validador.ajustarPesoPermitido(this, rotatedBitmap, 1228304)
                val imgString: String = bitMapToString(imageNueva!!)

                crearImagenApi(imgString)

                mostrarProgresbar(false)
            } else {
                Toast.makeText(this, "No tomo la Foto", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun descargarDatosFuncionario(optenerImgagen: Boolean) {
        val callback = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val obj = JSONObject(respuesta.toString())
                val value = obj.get("value")

                if (value.toString() != "[]") {
                    val valueObj = obj.getJSONArray("value")[0]
                    val datosFuncionario = JSONObject(valueObj.toString())

                    val nuevoFuncionario = Funcionario(datosFuncionario)
                    vmFuncionario.insertarFuncionario(nuevoFuncionario)

                    if (optenerImgagen) {
                        val imgString: String = bitMapToString(imageNueva!!)

                        vmFuncionario.atualizarImagenFuncionario(imgString)

                        val avatarFuncionario =
                            findViewById<CircleImageView>(R.id.cambiarAvatarFuncionario)
                        avatarFuncionario.setImageBitmap(imageNueva)
                    } else {
                        imagenPerfil.setImageResource(R.drawable.empty_personaje)
                    }

                    mostrarProgresbar(false)
                }
            }

            override fun error(error: VolleyError) {
                Log.i("DescargarDatosError", error.toString())
                mostrarProgresbar(false)
            }

        }

        obtenerFuncionario(
            this@CambiarAvatarActivity,
            identificacion.toString(),
            callback
        )
    }

    private fun obtenerFuncionario() {
        vmFuncionario.obtenerFuncionario()

    }

    private fun funcionarioAdjuntonull() {
        val callback = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                descargarDatosFuncionario(false)
            }

            override fun error(error: VolleyError) {
                mostrarProgresbar(false)

            }

        }

        actualizarFuncionarioAdjunto(this, id.toString(), null, callback)
    }

    /**Creamos la imgen en el servidor de imagenes**/
    private fun crearImagenApi(fotoNueva: String) {
        val callback = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val obj = JSONObject(respuesta.toString())

                actualizarImagenFuncionarioApi(obj.getString("object_id"))
            }

            override fun error(error: VolleyError) {
                Log.i("ErrorImgane", error.networkResponse.allHeaders.toString())
                mostrarProgresbar(false)
            }

        }
        crearArchivoServer(this, fotoNueva, callback)
    }

    private fun actualizarImagenFuncionarioApi(object_id: String) {
        val callback = object : IRespuestaServidor {
            override fun exito(respuesta: Any?) {
                val obj = JSONObject(respuesta.toString())

                descargarDatosFuncionario(true)
            }

            override fun error(error: VolleyError) {
                mostrarProgresbar(false)
            }
        }

        actualizarFuncionarioAdjunto(this, id.toString(), object_id, callback)
    }

    private fun mostrarProgresbar(boolean: Boolean) {
        when (boolean) {
            true -> {
                progressBar.visibility = View.VISIBLE
                conetenido.visibility = View.GONE
            }
            false -> {
                progressBar.visibility = View.GONE
                conetenido.visibility = View.VISIBLE
            }
        }

    }
}
