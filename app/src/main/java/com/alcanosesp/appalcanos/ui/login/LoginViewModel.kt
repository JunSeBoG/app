package com.alcanosesp.appalcanos.ui.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.alcanosesp.appalcanos.db.AppDatabase
import com.alcanosesp.appalcanos.db.entity.Token
import com.alcanosesp.appalcanos.utils.BaseViewModel
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : BaseViewModel(application) {

    val tokenSesion = MutableLiveData<Token>()
    private val dao = AppDatabase(getApplication()).tokenDao()

    fun obtenerToken() {
        launch {
            tokenSesion.value = dao.obtenerToken()
        }
    }

    fun insertarToken(token: Token) {
        eliminarToken()
        launch {
            dao.agregarToken(token)
        }
    }

    fun eliminarToken() {
        launch {
            dao.eliminarToken()
        }
    }

    fun actualizarToken(token: String, refreskToken:String){
        launch {
            dao.actualizarToken(token, refreskToken)

            tokenSesion.value = dao.obtenerToken()
        }
    }
}