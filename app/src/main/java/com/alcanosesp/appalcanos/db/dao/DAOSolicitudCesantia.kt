package com.alcanosesp.appalcanos.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.alcanosesp.appalcanos.db.entity.SolicitudCesantia

@Dao
interface DAOSolicitudCesantia {
    @Insert
    suspend fun insertarSolicitudCesantias(solicitudCesantia: SolicitudCesantia)

    @Query("SELECT * FROM SolicitudCesantia order by SolicitudCesantia.FechaSolicitud DESC")
    suspend fun obtenerSolicitudCesantias(): List<SolicitudCesantia>

    @Query("DELETE FROM SolicitudCesantia")
    suspend fun eliminarSolicitudCesantias()
}