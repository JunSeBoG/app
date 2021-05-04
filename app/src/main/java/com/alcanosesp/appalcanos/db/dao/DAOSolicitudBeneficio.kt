package com.alcanosesp.appalcanos.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.alcanosesp.appalcanos.db.entity.SolicitudBeneficio

@Dao
interface DAOSolicitudBeneficio {

    @Insert
    suspend fun insertarSolicitud(solicitud: SolicitudBeneficio)

    @Query("SELECT * FROM SolicitudBeneficio ORDER BY fechaSolicitud DESC")
    suspend fun obtenerSolicitudes() : List<SolicitudBeneficio>

    @Query("DELETE FROM SolicitudBeneficio")
    suspend fun eliminarSolicitudes()
}