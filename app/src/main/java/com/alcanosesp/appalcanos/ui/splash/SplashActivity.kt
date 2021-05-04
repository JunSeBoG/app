package com.alcanosesp.appalcanos.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alcanosesp.appalcanos.App
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.ui.login.LoginActivity
import com.alcanosesp.appalcanos.ui.login.LoginViewModel
import com.alcanosesp.appalcanos.ui.menulateral.MenuLateralActivity
import com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos.BasicosViewModel
import com.alcanosesp.appalcanos.ui.onboard.OnBoardActivity

class SplashActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(SplashViewModel::class.java)
    }

    private val vmToken by lazy {
        ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //Pantalla completa y ocultar el ActionBar
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        viewModel.refrescar()

        mostrarSplash()
    }

    /**
     * Metodo que muestra el splash
     */
    private fun mostrarSplash(){
        val handler = Handler()
        val run = Runnable { siguienteActivity() }

        handler.postDelayed(run, 2000)
    }

    /**
     * Metodo para verificar si la actividad de onBoarding ya fue mostrada
     */
    private fun siguienteActivity(){
        viewModel.parametroConfiguracion.observe(this, Observer {
            when(it.valor){
                "si" -> validarSiEstaLogeado()
                "no" -> { viewModel.actualizarBoardConfig()
                            irOnBoard() }
            }
        })
    }

    /*** Metódo que inicia la actividad de Token***/
    private fun irLogin(){
        val iLogin = Intent(this, LoginActivity::class.java)
        this.startActivity(iLogin)
        this.finish()
    }

    /*** Metódo que inicia actividad principal***/
    private fun irMenuLateral(){
        val iMenu = Intent(this, MenuLateralActivity::class.java)
        this.startActivity(iMenu)
        this.finish()
    }

    /*** Metódo que inicia la actividad del OnBoard ***/
    private fun irOnBoard(){
        val iOnBoard = Intent(this, OnBoardActivity::class.java)
        this.startActivity(iOnBoard)
        this.finish()
    }

    private fun validarSiEstaLogeado(){
      vmToken.obtenerToken()
        vmToken.tokenSesion .observe(this, Observer {
            val token = it
            if(token ==null){
                irLogin()
            }else{
                App.TOKEN = it.token
                App.REFRESH = it.refreshToken
                irMenuLateral()
            }
        })
    }
}
