package com.alcanosesp.appalcanos.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alcanosesp.appalcanos.R

fun validarSolicitarPermiso(activity: Activity,permiso: String) {
    Log.i("Permisos","Permisos")
    var  MY_PERMISSIONS_REQUEST_READ_CONTACTS :Int = 1
    // Here, thisActivity is the current activity
    if (ContextCompat.checkSelfPermission(activity,
            permiso)
        != PackageManager.PERMISSION_GRANTED) {
        Log.i("Permisos","no tiene")
        // Permission is not granted
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                permiso
            )) {
                Log.i("Permisos","Expliqu")
                ActivityCompat.requestPermissions(activity,
                    arrayOf(permiso),
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS)
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
        } else {
            Log.i("Permisos","solicito")
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(activity,
                arrayOf(permiso),
                MY_PERMISSIONS_REQUEST_READ_CONTACTS)

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    } else {
        // Permission has already been granted
        Log.i("Permisos","tiene")
    }
}
