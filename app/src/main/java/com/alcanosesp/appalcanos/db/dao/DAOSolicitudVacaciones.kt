package com.alcanosesp.appalcanos.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.alcanosesp.appalcanos.db.entity.SolicitudVacaciones

@Dao
interface DAOSolicitudVacaciones {
    @Insert
    suspend fun insertarSolicitudVacaciones(solicitudVacaciones: SolicitudVacaciones)

    @Query("SELECT * FROM SolicitudVacaciones ORDER BY SolicitudVacaciones.FechaInicioDisfrute DESC")
    suspend fun obtenerSolicitudVacaciones(): List<SolicitudVacaciones>

    @Query("DELETE FROM SolicitudVacaciones")
    suspend fun eliminarSolicitudVacaciones()
}