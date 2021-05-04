package com.alcanosesp.appalcanos.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.alcanosesp.appalcanos.db.entity.SolicitudPermisoSoporte

@Dao
interface DAOSolicitudPermisoSoporte {
    @Insert
    suspend fun insertarPermisoSoporte(permisoSoporte: SolicitudPermisoSoporte)

    @Query("SELECT * FROM permisosoporte")
    suspend fun obtenerPermisoSoporte(): List<SolicitudPermisoSoporte>

    @Query("DELETE FROM PermisoSoporte")
    suspend fun eliminarPermisosSoporte()
}