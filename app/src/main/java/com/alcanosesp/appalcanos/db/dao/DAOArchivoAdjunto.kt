package com.alcanosesp.appalcanos.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.alcanosesp.appalcanos.db.entity.ArchivoAdjunto
import com.alcanosesp.appalcanos.db.entity.AusentismoFuncionario

@Dao
interface DAOArchivoAdjunto {
    @Insert
    suspend fun insertarArchivoAdjunto(archivoAdjunto:ArchivoAdjunto)

    @Query("SELECT * FROM ArchivoAdjunto ")
    suspend fun obtenerArchivoAdjunto(): List<ArchivoAdjunto>

    @Query("DELETE FROM ArchivoAdjunto ")
    suspend fun eliminarArchivoAdjunto()
}