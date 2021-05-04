package com.alcanosesp.appalcanos.utils


import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.api.IRespuestaServidor
import com.alcanosesp.appalcanos.api.crearArchivoServer
import com.alcanosesp.appalcanos.db.entity.ArchivoAdjunto
import com.android.volley.VolleyError
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_cargar_adjunto.*
import org.json.JSONObject
import java.io.File
import java.nio.file.Files


class CargarAdjunto : BottomSheetDialogFragment() {
    private val FOTO_GALERIA = 1
    private val TOMAR_FOTO = 2
    private val DOCUMENTO = 3
    private var photoFile: File? = null


    private val viewModel by lazy {
        ViewModelProviders.of(this).get(ArchivoAdjuntoViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cargar_adjunto, container, false)

        val seleccionarDocumento =
            view.findViewById<LinearLayout>(R.id.llSeleccionarDocumentoDialogo)
        val seleccionarFoto = view.findViewById<LinearLayout>(R.id.llSeleccionarFotoDialogo)
        val tomarFoto = view.findViewById<LinearLayout>(R.id.llTomarFotoDialogo)


        seleccionarDocumento.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "application/pdf"
            //intent.addCategory(Intent.CATEGORY_OPENABLE)


            startActivityForResult(intent, DOCUMENTO)
        }


        seleccionarFoto.setOnClickListener {
            val iGaleria = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            iGaleria.type = "image/*"
            startActivityForResult(
                iGaleria,
                FOTO_GALERIA
            )
        }

        tomarFoto.setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(context!!.packageManager)?.also {
                    photoFile = try {
                        crearArchivoImagen(context!!)
                    } catch (ex: Exception) {
                        // Error occurred while creating the File
                        Log.e("errorTomarFoto", "sss")
                        null

                    }

                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            context!!,
                            "com.alcanosesp.fileprovider",
                            photoFile!!
                        )
                        pb_cargar_adjunto.isVisible=true
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, TOMAR_FOTO)
                    }
                }
            }
        }

        viewModel.archivoAdjunto.observe(this, Observer {
            Log.i("Obserac", " cerrar")
            dismiss()
        })

        return view
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val validador = Validador()

        when (requestCode) {
            FOTO_GALERIA -> {
                if (resultCode == RESULT_OK) {
                    pb_cargar_adjunto.isVisible=true
                    ll_item_cargar_adjunto.isVisible=false

                    val selectedImage = data?.data
                    Log.i("Foto", selectedImage.toString())
                    val imageBitmap = uriToBitmapNoCambiaTamanio(context!!, selectedImage!!)
                    val tamanioPermitido = validador.ajustarPesoPermitido(context!!, imageBitmap)
                    val imgString: String = bitMapToString(tamanioPermitido)

                    cargarImagen(imgString)

                } else {
                    Toast.makeText(context, "No selecciono una imagen.", Toast.LENGTH_LONG).show()
                }

            }
            TOMAR_FOTO -> {
                if (resultCode == RESULT_OK) {
                    pb_cargar_adjunto.isVisible=true
                    ll_item_cargar_adjunto.isVisible=false
                    val filePath = photoFile?.path
                    Log.i("filePath", filePath)
                    val imageBitmap = BitmapFactory.decodeFile(filePath)
                    val rotation = obtenerRatacionIMagen(filePath!!)
                    val rotatedBitmap = ajustarRotacionimagen(rotation, imageBitmap)
                    val tamaniPermitido = validador.ajustarPesoPermitido(context!!, rotatedBitmap)
                    val imgString: String = bitMapToString(tamaniPermitido)

                    cargarImagen(imgString)

                } else {
                    Toast.makeText(context, "No tomó una foto.", Toast.LENGTH_LONG).show()
                }
            }
            DOCUMENTO -> {
                if (resultCode == RESULT_OK) {
                    pb_cargar_adjunto.isVisible=true
                    ll_item_cargar_adjunto.isVisible=false

                    var uri = data?.data

                    val returnUri = data?.data
                    val returnCursor =
                        context!!.contentResolver.query(returnUri!!, null, null, null, null)!!
                    val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
                    returnCursor.moveToFirst()
                    val filename = returnCursor.getString(nameIndex)

                    val fileSave = context!!.getExternalFilesDir(null)
                    val sourcePath = context!!.getExternalFilesDir(null).toString()
                    var file = UrlPdf().copyFileStream(File("$sourcePath/$filename"), uri!!, context!!)

                    DocumentsContract.copyDocument(context!!.contentResolver,uri,"$sourcePath/12$filename".toUri())

                    Log.i("absolutePath", file.absolutePath)
                    var fileContent = Files.readAllBytes(file.toPath())
                    Log.i("fileContent", fileContent.toString())
                    var porfin = Base64.encodeToString(fileContent, Base64.NO_PADDING)
                    Log.i("porfinBase64", porfin)




                } else {
                    Toast.makeText(context, "No selecciono una imagen.", Toast.LENGTH_LONG).show()
                }

            }
        }
    }


    fun dumpImageMetaData(uri: Uri, context: Context) {
        val cursor = context.contentResolver.query(
            uri, null, null, null, null, null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                val displayName: String =
                    it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                val sizeIndex: Int = it.getColumnIndex(OpenableColumns.SIZE)
                val size: String = if (!it.isNull(sizeIndex)) {
                    it.getString(sizeIndex)
                } else {
                    "Unknown"
                }
                val validador = Validador()
                Log.i("Size", " $size")
                Log.i("Display Name:", "$displayName")

                if (validador.validarPesoPermitidoPdf(context, size.toInt())) {

                    Log.i("isDocumentUri", DocumentsContract.isDocumentUri(context, uri).toString())
                    Log.i("getDocumentId", DocumentsContract.getDocumentId(uri))
                    //DocumentsContract.


                    //cargarImagen(imgString)
                }
            }
        }

    }


    fun cargarImagen(imgB64: String) {
        val callbackArchivoAdjunto = object : IRespuestaServidor {

            override fun exito(respuesta: Any?) {
                val json = JSONObject(respuesta.toString())
                val objectid = json.getString("object_id")

                val jsonIncapacoidad = JSONObject().apply {
                    put("archivo", imgB64)
                    put("objectId", objectid)
                    put("etiqueta", "incapacidad")///Validar
                }
                viewModel.insertarArchivoAdjunto(ArchivoAdjunto(jsonIncapacoidad))
                viewModel.obtenerArchivoAdjunto()
            }

            override fun error(error: VolleyError) {
                Log.e("Error", "CargarAdjunto volley ${error.networkResponse.statusCode}")
                viewModel.obtenerArchivoAdjunto()
            }
        }

        crearArchivoServer(context!!, imgB64, callbackArchivoAdjunto)
    }


}

