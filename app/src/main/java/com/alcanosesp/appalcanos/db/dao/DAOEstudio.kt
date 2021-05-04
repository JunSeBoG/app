package com.alcanosesp.appalcanos.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.alcanosesp.appalcanos.db.entity.Estudio

@Dao
interface DAOEstudio {

    @Insert
    suspend fun insertarEstudio(estudio: Estudio)

    @Query("SELECT * FROM Estudio")
    suspend fun obtenerEstudios() : List<Estudio>

    @Query("DELETE FROM Estudio")
    suspend fun eliminarEstudios()
}