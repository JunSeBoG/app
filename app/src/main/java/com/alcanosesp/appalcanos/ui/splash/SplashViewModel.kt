package com.alcanosesp.appalcanos.ui.splash

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.alcanosesp.appalcanos.db.AppDatabase
import com.alcanosesp.appalcanos.db.entity.AppConfigParameter
import com.alcanosesp.appalcanos.utils.BaseViewModel
import kotlinx.coroutines.launch

class SplashViewModel(application: Application) : BaseViewModel(application) {

    val parametroConfiguracion = MutableLiveData<AppConfigParameter>()
    private val dao = AppDatabase(getApplication()).configDao()

    fun refrescar() {
        obtenerBoardConfig()
    }

    /**
     * Obtengo mi parametro de configuracion para el onBoard, en caso de
     * que no este agregado, lo inserto en la base de datos, si ya existe
     * le asigno ese objeto a la variable parametro
     */
    private fun obtenerBoardConfig() {
        launch {
            var parametro: AppConfigParameter? = dao.obtenerParametroConfiguracion("boardMostrado")

            if (parametro == null) {
                dao.agregarParametroConfiguracion(AppConfigParameter(1, "boardMostrado", "no"))
                parametro = dao.obtenerParametroConfiguracion("boardMostrado")
            }

            Log.i("PARAMETRO AGREGADO", parametro?.nombre!!)
            parametroConfiguracion.value = parametro
        }
    }

    /**
     * Metodo para actualizar el valor del parametro de configuracion del onBoarding
     */
    fun actualizarBoardConfig() {
        launch {
            dao.actualizarParametroConfiguracion(AppConfigParameter(1, "boardMostrado", "si"))
        }
    }
}