package com.alcanosesp.appalcanos.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.alcanosesp.appalcanos.db.entity.SolicitudPermiso
import com.alcanosesp.appalcanos.db.entity.SolicitudPermisoSoporte

@Dao
interface DAOSolicitudPermiso {
    @Insert
    suspend fun insertarPermiso(permiso: SolicitudPermiso)

    @Query("SELECT * FROM Permiso order by Permiso.Posicion")
    suspend fun obtenerPermiso(): List<SolicitudPermiso>

    @Query("DELETE FROM Permiso")
    suspend fun eliminarPermisos()

    @Query("SELECT * FROM PermisoSoporte WHERE PermisoSoporte.SolicitudPermisoId =:permisoId")
    suspend fun obtenerSoportePermiso(permisoId :Int): List<SolicitudPermisoSoporte>

    @Insert
    suspend fun insertarSoportePermiso(soporte: SolicitudPermisoSoporte)

    @Query("DELETE FROM PermisoSoporte")
    suspend fun eliminarSoportePermisos()

    @Query("DELETE FROM PermisoSoporte WHERE PermisoSoporte.SolicitudPermisoId =:id")
    suspend fun eliminarSoportePermisosById(id:Int)

}