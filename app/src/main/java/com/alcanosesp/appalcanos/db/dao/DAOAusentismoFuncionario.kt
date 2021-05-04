package com.alcanosesp.appalcanos.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.alcanosesp.appalcanos.db.entity.AusentismoFuncionario

@Dao
interface DAOAusentismoFuncionario {
    @Insert
    suspend fun insertarAusentismoFuncionario(ausentismoFuncionario: AusentismoFuncionario)

    @Query("SELECT * FROM Ausentismofuncionario ORDER BY FechaInicio DESC")
    suspend fun obtenerAusentismoFuncionario(): List<AusentismoFuncionario>

    @Query("DELETE FROM AusentismoFuncionario ")//where tipo I
    suspend fun eliminarAusentismoFuncionario()

    @Query("SELECT Id FROM Ausentismofuncionario ORDER BY FechaInicio DESC")
    suspend fun obtenerIdIncaPacidades(): List<Int>

    @Query("UPDATE Ausentismofuncionario SET ProrrogaDe =:prorroga where Id =:ausentimosId")
    suspend fun actualizarProrrogaAusentismoFuncionario(prorroga:String, ausentimosId:Int)
}