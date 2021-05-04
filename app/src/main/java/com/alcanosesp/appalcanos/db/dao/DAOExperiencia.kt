package com.alcanosesp.appalcanos.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.alcanosesp.appalcanos.db.entity.Estudio
import com.alcanosesp.appalcanos.db.entity.Experiencia

@Dao
interface DAOExperiencia {

    @Insert
    suspend fun insertarExperiencia(experiencia: Experiencia)

    @Query("SELECT * FROM Experiencia")
    suspend fun obtenerExperiencias() : List<Experiencia>

    @Query("DELETE FROM Experiencia")
    suspend fun eliminarExperiencias()
}