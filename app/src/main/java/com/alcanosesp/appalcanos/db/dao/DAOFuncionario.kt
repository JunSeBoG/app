package com.alcanosesp.appalcanos.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.alcanosesp.appalcanos.db.entity.Funcionario

@Dao
interface DAOFuncionario{
    @Insert
    suspend fun agregarFuncionario(funcionario : Funcionario)

    @Query("SELECT * FROM Funcionario")
    suspend fun obtenerFuncionario() : Funcionario?

    @Update
    suspend fun actualizarFuncionario(funcionario : Funcionario)

    @Query("DELETE FROM Funcionario")
    suspend fun eliminarFuncionarios()

    @Query("UPDATE Funcionario SET Foto = :imagenB64")
    suspend fun actualizarImagenFuncionarios(imagenB64:String)

    @Query("UPDATE Funcionario SET Foto =null and Adjunto = null")
    suspend fun actualizaNullAdjuntoFoto()

    @Query("UPDATE Funcionario SET Adjunto = :objectId")
    suspend fun atualizarAdjuntoFuncionario(objectId:String)
}