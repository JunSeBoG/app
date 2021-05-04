package com.alcanosesp.appalcanos.ui.login

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.alcanosesp.appalcanos.App
import com.alcanosesp.appalcanos.R
import com.alcanosesp.appalcanos.api.IRespuestaServidor
import com.alcanosesp.appalcanos.api.iniciarSesion
import com.alcanosesp.appalcanos.api.obtenerFuncionario
import com.alcanosesp.appalcanos.api.obtenerImagenServer
import com.alcanosesp.appalcanos.db.entity.Funcionario
import com.alcanosesp.appalcanos.db.entity.Token
import com.alcanosesp.appalcanos.ui.menulateral.MenuLateralActivity
import com.alcanosesp.appalcanos.ui.menulateral.datos_personales.datos_basicos.BasicosViewModel
import com.alcanosesp.appalcanos.utils.bitMapToString
import com.alcanosesp.appalcanos.utils.construirAlerta
import com.android.volley.VolleyError
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }

    private val vmFuncionario by lazy {
        ViewModelProviders.of(this).get(BasicosViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        window.navigationBarColor = getColor(R.color.colorPrimary)
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_login)


        /**Validamos si la app llego a login por un cierre de sesion**/
        val cerroSession: String? = intent.getStringExtra("cerrarSesion")
        if (cerroSession != null) {
            construirAlerta(
                this,
                R.layout.toas_login_cerrar_sesion,
                getString(R.string.mensaje_sesion_cerrada)
            ).show()
        }
    }

    fun btnLoginIngresar(v: View) {
        ocultarFormularioMostarSpiner(true)
        val usuario = etLoginUsuario.text.toString()
        val contrasena = etLoginContrasena.text.toString()

        if (noSonNulos(usuario, contrasena)) {
            val callback = object : IRespuestaServidor {

                override fun exito(respuesta: Any?) {
                    Log.i("TokenDatos", respuesta.toString())
                    val objToken = JSONObject(respuesta.toString())
                    if (usuarioCorrecto(objToken)) {
                        tienePermisos(objToken)
                    }
                }

                override fun error(error: VolleyError) {
                    var codigo: String? = error.networkResponse?.statusCode?.toString()
                    if (codigo.isNullOrEmpty()) codigo = "404"

                    construirAlerta(
                        this@LoginActivity,
                        R.layout.toas_login_warning,
                        getString(R.string.mensaje_eror_404, codigo)
                    )
                    ocultarFormularioMostarSpiner(false)
                }
            }

            iniciarSesion(this, usuario, contrasena, callback)
        }
    }



    private fun noSonNulos(usuario: String, contrasena: String): Boolean {
        return if (usuario.isEmpty() || contrasena.isEmpty()) {
            construirAlerta(
                this,
                R.layout.toas_login_warning,
                getString(R.string.mensaje_error_iniciar_sesion)
            ).show()
            ocultarFormularioMostarSpiner(false)
            false
        } else {
            true
        }
    }

    private fun ocultarFormularioMostarSpiner(visible: Boolean) {
        if (visible) {
            llLoginCampos.visibility = View.INVISIBLE
            pbLoginBarraProgreso.visibility = View.VISIBLE
        } else {
            llLoginCampos.visibility = View.VISIBLE
            pbLoginBarraProgreso.visibility = View.INVISIBLE
        }
    }

    /**Vsi token es nulo, retornamos error de usuarios**/
    private fun usuarioCorrecto(datosToken: JSONObject): Boolean {

        val token = datosToken.getString("token")
        return if (token.isEmpty()) {
            construirAlerta(
                this,
                R.layout.toas_login_warning,
                getString(R.string.mensaje_error_iniciar_sesion)
            ).show()
            ocultarFormularioMostarSpiner(false)
            false
        } else {
            true
        }
    }

    private fun tienePermisos(obj: JSONObject) {
        val nombreApp = obj.getString("aplicaciones").contains(getString(R.string.nombreapp))

        if (nombreApp) {
            viewModel.insertarToken(Token(obj))
            App.TOKEN = obj.getString("token")
            App.REFRESH = obj.getString("refreshToken")

            obtenerUsuario(obj.getString("cedula"))


        } else {
            construirAlerta(
                this,
                R.layout.toas_login_warning,
                getString(R.string.mensaje_no_tiene_permisos_ingreso)
            ).show()
            ocultarFormularioMostarSpiner(false)
        }
    }

    private fun obtenerUsuario(cedula: String) {
        val callback = object : IRespuestaServidor {

            override fun exito(respuesta: Any?) {
                val obj = JSONObject(respuesta.toString())
                val value = obj.get("value")

                if (value.toString() != "[]") {
                    val valueObj = obj.getJSONArray("value")[0]
                    val datosFuncionario = JSONObject(valueObj.toString())

                    val nuevoFuncionario = Funcionario(datosFuncionario)
                    vmFuncionario.insertarFuncionario(nuevoFuncionario)

                    //obtener imagen
                    val adjunto = datosFuncionario.getString("adjunto")
                    if (!adjunto.isNullOrEmpty()) {
                        obtenerFoto(adjunto)
                    } else {
                        abrirMenuLateral()
                    }
                } else {
                    construirAlerta(
                        this@LoginActivity,
                        R.layout.toas_login_warning,
                        getString(R.string.mensaje_no_creado_como_empleado)
                    ).show()
                    ocultarFormularioMostarSpiner(false)
                }
            }

            override fun error(error: VolleyError) {
                Log.i("Error", "obtenerFunconarioLoginActivity ${error.networkResponse.statusCode}")
            }
        }
        obtenerFuncionario(this, cedula, callback)

    }

    fun obtenerFoto(adjunto: String?) {
        val callbackImg = object : IRespuestaServidor {

            override fun exito(respuesta: Any?) {
                if (respuesta != null) {
                    //Guardamos Bitmap de imagen en base Local
                    val imgString = bitMapToString(respuesta as Bitmap)

                    vmFuncionario.atualizarImagenFuncionario(imgString)

                    abrirMenuLateral()
                }
            }

            override fun error(error: VolleyError) {
                Log.i("Error", "ObtenerImagenLoginActivity ${error?.networkResponse?.statusCode}")
                abrirMenuLateral()
            }
        }
        obtenerImagenServer(this, adjunto.toString(), callbackImg)
    }

    private fun abrirMenuLateral() {
        val iLogin = Intent(this@LoginActivity, MenuLateralActivity::class.java)
        this@LoginActivity.startActivity(iLogin)
        this@LoginActivity.finish()
    }


}