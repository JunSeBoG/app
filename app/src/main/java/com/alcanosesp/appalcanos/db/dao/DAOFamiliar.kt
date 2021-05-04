package com.alcanosesp.appalcanos.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.alcanosesp.appalcanos.db.entity.Familiar

@Dao
interface DAOFamiliar {

    @Insert
    suspend fun insertarFamiliar(familiar: Familiar)

    @Query("SELECT * FROM Familiar")
    suspend fun obtenerFamiliares() : List<Familiar>

    @Query("DELETE FROM Familiar")
    suspend fun eliminarFamiliares()

}