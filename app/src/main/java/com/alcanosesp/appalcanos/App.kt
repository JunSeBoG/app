package com.alcanosesp.appalcanos

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.alcanosesp.appalcanos.db.entity.*
import com.alcanosesp.appalcanos.ui.notificacion.NotificacionActivity
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException


class App : Application() {

    companion object {

        // usuario
        var TOKEN: String? = null
        var REFRESH: String? = null

        // aplicalcion
        var MASTERTOKEN: String? = null

        var idFuncionario: Int? = null

        var ccFuncionario: String? = null

        var estudio: Estudio? = null
        var familiar: Familiar? = null
        var experiencia: Experiencia? = null

        var incapacidad: AusentismoFuncionario? = null

        var solicitudBeneficio: SolicitudBeneficio? = null

        var solicitudPermiso: SolicitudPermiso? = null

        var solicitudCesantia: SolicitudCesantia? = null

        var solicitudVacaciones: SolicitudVacaciones? = null

        var adjuntoAdjunto: String? = null

    }

    val CHANNEL_ID = "CHANNEL_ID"
    var NOTIFICACION_ID = 0

    var socket: Socket
    private var pendingIntent: PendingIntent? = null

    init {
        try {
            val opts =
                IO.Options()
            opts.forceNew = true
            opts.reconnection = false
            socket = IO.socket("http://nompruebas.alcanosesp.com:3010", opts)

        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
    }

    override fun onCreate() {
        super.onCreate()

        socket.on(
            "mobile:notification"
        ) { args ->
            Log.i("socketEvent", "notification")
            try {
                val obj = args[0] as JSONObject
                notification(obj.getString("title"), obj.getString("text"))
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        socket.on("connect") {
            Log.i("socketEvent", "connect")
        }
        socket.on("disconnect") {
            Log.i("socketEvent", "disconnect")

        }
        socket.connect()
    }

    override fun onTerminate() {
        super.onTerminate()
        socket.disconnect()
    }

    fun notification(title: String, text: String) {
        NOTIFICACION_ID += 1
        Log.i("notiid", NOTIFICACION_ID.toString() + "ss")
        setPendingIntent(title, text)
        createNotificationChannel()
        createNotification(title, text)

    }

    private fun setPendingIntent(title: String, text: String) {
        val intent = Intent(this, NotificacionActivity::class.java)

        intent.putExtra("notifacionesId", NOTIFICACION_ID.toString())
        intent.putExtra("title$NOTIFICACION_ID", title)
        intent.putExtra("text$NOTIFICACION_ID", text)

        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(NotificacionActivity::class.java)
        stackBuilder.addNextIntent(intent)

        pendingIntent =
            stackBuilder.getPendingIntent(NOTIFICACION_ID, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Noticacion"
            val notificationChannel =
                NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun createNotification(title: String, text: String) {

        val gruponotificacion = "import com.alcanosesp.appalcanos.notificacionpush"

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)

        builder.setSmallIcon(R.drawable.ic_gh)
        builder.setContentTitle(title)
        builder.setContentText(text)
        builder.color = getColor(R.color.azul_cielo)
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(true)
        builder.setGroup(gruponotificacion)


        val notificationManagerCompat =
            NotificationManagerCompat.from(applicationContext)
        notificationManagerCompat.notify(NOTIFICACION_ID, builder.build())
    }
}
