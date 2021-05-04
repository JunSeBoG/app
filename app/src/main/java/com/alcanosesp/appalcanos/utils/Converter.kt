package com.alcanosesp.appalcanos.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.core.graphics.scale
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun bitMapToString(bitmap: Bitmap): String {
    var baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos)
    var b = baos.toByteArray()
    var temp: String = ""
    try {
        System.gc()
        temp = Base64.encodeToString(b, Base64.DEFAULT).replace("\n", "")

    } catch (e: Exception) {
        e.printStackTrace()
    } catch (e: OutOfMemoryError) {
        baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 10, baos)
        b = baos.toByteArray()
        temp = Base64.encodeToString(b, Base64.DEFAULT)
        Log.e("EWN", "Out of memory error catched")
    }
    return temp
}

fun stringToBitMap(encodedString: String): Bitmap? {
    try {
        var encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
        var bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size);
        return bitmap;
    } catch (e: java.lang.Exception) {
        e.printStackTrace();
    }
    return null;
}

fun pathToBitmap(context: Context, path: String): Bitmap? {
    val uri = Uri.fromFile(File(path));
    return uriToBitmap(context, uri);
}

fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    var TAG: String = "venta";
    // var inputStream :InputStreamReader?
    try {
        val IMAGE_MAX_SIZE: Int = 300000; // 0.3MP
        //final int IMAGE_MAX_SIZE = 1200000; // 1.2MP

        var inputStream = context.getContentResolver().openInputStream(uri)

        // Decode image size
        var options = BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);
        inputStream?.close();

        var scale: Double = 1.0;
        while ((options.outWidth * options.outHeight) * (1 / Math.pow(scale, 2.0)) >
            IMAGE_MAX_SIZE
        ) {
            scale++;
        }
        Log.d(
            TAG,
            "scale = " + scale + ", orig-width: " + options.outWidth + ", orig - height:" + options.outHeight
        );

        var resultBitmap: Bitmap?;
        inputStream = context.getContentResolver().openInputStream(uri);
        if (scale > 1) {
            scale--;
            // scale to max possible inSampleSize that still yields an image
            // larger than target
            options = BitmapFactory.Options();
            options.inSampleSize = scale.toInt();
            resultBitmap = BitmapFactory.decodeStream(inputStream, null, options);

            // resize to desired dimensions
            var height = resultBitmap?.getHeight();
            var width = resultBitmap?.getWidth();
            Log.d(TAG, "1th scale operation dimenions - width: " + width + ",height:" + height);

            var y = Math.sqrt(IMAGE_MAX_SIZE / ((width!!.toDouble()) / height!!));
            var x = (y / height) * width;

            var scaledBitmap =
                Bitmap.createScaledBitmap(resultBitmap!!, x.toInt(), y.toInt(), true);
            resultBitmap.recycle();
            resultBitmap = scaledBitmap;
            System.gc();
        } else {
            resultBitmap = BitmapFactory.decodeStream(inputStream);
        }
        inputStream?.close();

        Log.d(
            TAG,
            "bitmap size - width: " + resultBitmap.getWidth() + ", height: " + resultBitmap.getHeight()
        );


        var bitmapConfig = resultBitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }

        var bitmap = resultBitmap.copy(bitmapConfig, true);

        return bitmap;


    } catch (e: java.lang.Exception) {
        Log.e(TAG, e.message, e);
        return null;
    }
}

fun Int.dosDigitos() = if (this <= 9) "0$this" else this.toString()

fun crearArchivoImagen(context: Context): File {
    // Create an image file name
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}_", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    ).apply {
        // Save a file: path for use with ACTION_VIEW intents
        // currentPhotoPath = absolutePath
        //lateinit var currentPhotoPath: String
    }
}

fun obtenerRatacionIMagen(filePath: String): Int {
    val exif = ExifInterface(filePath)
    return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
}

fun ajustarRotacionimagen(rotacion: Int, imageBitmap: Bitmap): Bitmap {
    var rotatedBitmap: Bitmap? = null
    rotatedBitmap = when (rotacion) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(imageBitmap, 90f)

        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(imageBitmap, 180f);

        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(imageBitmap, 270f);

        else -> imageBitmap;
    }
    return rotatedBitmap
}

fun rotateImage(source: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle);
    return Bitmap.createBitmap(
        source, 0, 0, source.getWidth(), source.getHeight(),
        matrix, true
    );
}

fun cambiarTamanioImagen(nuevoWidth: Int, nuevoHeight: Int, bitmap: Bitmap): Bitmap {
    val heightBitmap = bitmap.height
    val widthBitmap = bitmap.width
    var height = 0
    var width = 0
    var porcentajeHeight = 0 //Porcentaje de reduccion, para el heigth indicado
    var porcentajeWidth = 0 //Porcentaje de reduccion, para el width indicado



    porcentajeHeight = (nuevoHeight * 100) / heightBitmap
    porcentajeWidth = (nuevoWidth * 100) / widthBitmap

    height = (heightBitmap * porcentajeHeight) / 100
    width = (widthBitmap * porcentajeWidth) / 100


    return bitmap.scale(width, height, false)
}

fun convertirBitmapTamanioPermitido(bitmap: Bitmap, nuevoTamanio: Int): Bitmap {
    var width = bitmap.width
    var height = bitmap.height
    var tamanio = width * height
    var porcentajeReduccion = 90

    while (tamanio > nuevoTamanio) {
        width = (bitmap.width * porcentajeReduccion) / 100
        height = (bitmap.height * porcentajeReduccion) / 100

        tamanio = height * width

        porcentajeReduccion -=10
    }


    return bitmap.scale(width, height, false)
}

fun uriToBitmapNoCambiaTamanio(context: Context, uri: Uri): Bitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(
                context.contentResolver,
                uri
            )
        )

    } else {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }
}