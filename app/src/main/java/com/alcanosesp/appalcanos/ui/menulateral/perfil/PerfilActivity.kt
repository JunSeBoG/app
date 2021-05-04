    package com.alcanosesp.appalcanos.ui.menulateral.perfil

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.api.IRespuestaServidor
import com.alcanosesp.appalcanos.databinding.ActivityPerfilBinding
import com.alcanosesp.appalcanos.ui.login.LoginActivity
import com.alcanosesp.appalcanos.ui.login.LoginViewModel
import com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos.BasicosViewModel
import com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos.EstudiosViewModel
import com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos.ExperienciasViewModel
import com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos.FamiliaresViewModel
import com.alcanosesp.appalcanos.ui.menulateral.incapacidades.IncapacidadViewModel
import com.alcanosesp.appalcanos.ui.menulateral.solicitudes.cesantias.CesantiasViewModel
import com.alcanosesp.appalcanos.ui.menulateral.solicitudes.beneficios.BeneficiosViewModel
import com.alcanosesp.appalcanos.ui.menulateral.solicitudes.permisos.PermisoViewModel
import com.alcanosesp.appalcanos.utils.stringToBitMap
import com.android.volley.VolleyError
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_perfil.*

    var intentos = 0

class PerfilActivity : AppCompatActivity() {
    private lateinit var imagenPrincipal: CircleImageView

    private val vmLogin by lazy {
        ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }

    private val vmFuncionario by lazy {
        ViewModelProviders.of(this).get(BasicosViewModel::class.java)
    }

    private val vmFamiliar by lazy {
        ViewModelProviders.of(this).get(FamiliaresViewModel::class.java)
    }

    private val vmExperiencia by lazy {
        ViewModelProviders.of(this).get(ExperienciasViewModel::class.java)
    }

    private val vmEstudios by lazy {
        ViewModelProviders.of(this).get(EstudiosViewModel::class.java)
    }

    private val vmIncapacidad by lazy {
        ViewModelProviders.of(this).get(IncapacidadViewModel::class.java)
    }

    private val vmSolicitud by lazy {
        ViewModelProviders.of(this).get(BeneficiosViewModel::class.java)
    }

    private val vmPermiso by lazy {
        ViewModelProviders.of(this).get(PermisoViewModel::class.java)
    }

    private val  vmCesantias by lazy {
        ViewModelProviders.of(this).get(CesantiasViewModel::class.java)
    }

    val callbackClima = object : IRespuestaServidor {

        override fun error(error: VolleyError) {

            limpiarBasedeDatos()

            val iLogin = Intent(applicationContext, LoginActivity::class.java)
            iLogin.putExtra("cerrarSesion", "cerrarSesion")
            iLogin.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(iLogin)
            this@PerfilActivity.finish()
        }

        override fun exito(respuesta: Any?) {
            Toast.makeText(this@PerfilActivity, "bien", Toast.LENGTH_LONG).show()
            Log.i("ExitoAvtivity", respuesta.toString())
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val v = DataBindingUtil.inflate<ActivityPerfilBinding>(
            layoutInflater,
            R.layout.activity_perfil,
            null,
            false
        )

        setContentView(v.root)

        funcionario(v)

        val cerrarSesion = findViewById<TextView>(R.id.btnCerrarSesion)
        imagenPrincipal = findViewById(R.id.avatarFuncionario)

        //vmFuncionario.obtenerFuncionario()
        //vmFuncionario.funcionario.observe(this, Observer {
        //})

        /**Cerrar cesion, y vaciar todas las tablas***/
        cerrarSesion.setOnClickListener {

            limpiarBasedeDatos()

            val iLogin = Intent(applicationContext, LoginActivity::class.java)
            iLogin.putExtra("cerrarSesion", "cerrarSesion")
            iLogin.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(iLogin)
            this.finish()
        }

        /**CambiarImagen**/
        rlCambiarImagen.setOnClickListener {
            val iCambiarimagen = Intent(this, CambiarAvatarActivity::class.java)
            startActivity(iCambiarimagen)
        }

        vmLogin.obtenerToken()

    }

    fun funcionario(binding: ActivityPerfilBinding) {
        vmFuncionario.obtenerFuncionario()

        vmFuncionario.funcionario.observe(this, Observer {
        Log.i("PerfilActivity",it.foto.toString())
            binding.funcionario = it
            if (!it?.foto.isNullOrEmpty() ) {
                val imagenBitmap = stringToBitMap(it.foto.toString())
                imagenPrincipal.setImageBitmap(imagenBitmap)
            }else{
                imagenPrincipal.setImageResource(R.drawable.empty_personaje)
            }

        })
    }

    fun limpiarBasedeDatos(){
        vmLogin.eliminarToken()
        vmFuncionario.eliminarFuncionario()
        vmFamiliar.eliminarFamiliares()
        vmEstudios.eliminarEstudios()
        vmExperiencia.eliminarExperiencias()
        vmIncapacidad.eliminarIncapacidad()
        vmSolicitud.eliminarSolicitudes()
        vmPermiso.eliminarSolicitudPermiso()
        vmPermiso.eliminarSoporteSolicitudPermiso()
        vmCesantias.eliminarSolicidCesantias()
    }


}
