package com.alcanosesp.appalcanos.ui.notificacion

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.alcanosesp.appalcanos.App
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.databinding.ActivityNotificacionBinding
import com.alcanosesp.appalcanos.ui.menulateral.MenuLateralActivity

class NotificacionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificacionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.navigationBarColor = getColor(R.color.colorPrimary)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_notificacion)


        val id = intent.getStringExtra("notifacionesId")

        binding.textTitle.text =  intent.getStringExtra("title$id")
        binding.textDescrip.text =intent.getStringExtra("text$id")

        binding.bAceptarNotificacion.setOnClickListener {

            val notificationId = intent.getIntExtra("notificationId", 0)
            val manager =this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(notificationId)
            val intent = Intent(this,MenuLateralActivity::class.java)
            this.startActivity(intent)
            this.finish()
        }
    }
}
