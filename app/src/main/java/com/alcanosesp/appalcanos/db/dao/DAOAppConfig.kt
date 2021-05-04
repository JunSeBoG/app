package com.alcanosesp.appalcanos.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.alcanosesp.appalcanos.db.entity.AppConfigParameter

@Dao
interface DAOAppConfig {

    @Insert
    suspend fun agregarParametroConfiguracion(configParameter : AppConfigParameter)

    @Query("SELECT * FROM lista_config WHERE config_nombre = :nombreParametro")
    suspend fun obtenerParametroConfiguracion(nombreParametro : String) : AppConfigParameter?

    @Update
    suspend fun actualizarParametroConfiguracion(configParameter : AppConfigParameter)
}