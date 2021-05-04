package com.alcanosesp.appalcanos.ui.menulateral.perfil

import android.app.Application

import androidx.lifecycle.MutableLiveData
import com.alcanosesp.appalcanos.db.AppDatabase
import com.alcanosesp.appalcanos.db.entity.Token
import com.alcanosesp.appalcanos.utils.BaseViewModel

class PerfilViewModel(application: Application) : BaseViewModel(application) {
    val token = MutableLiveData<Token>()
    private val dao = AppDatabase(getApplication()).tokenDao()




}

