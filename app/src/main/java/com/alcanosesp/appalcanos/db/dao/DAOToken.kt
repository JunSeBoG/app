package com.alcanosesp.appalcanos.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.alcanosesp.appalcanos.db.entity.Token

@Dao
interface DAOToken {
    @Insert
    suspend fun agregarToken(token: Token)

    @Query("SELECT * FROM Token")
    suspend fun obtenerToken(): Token?

    @Query("UPDATE Token SET  Token=:token , RefreshToken=:refreshToken ")
    suspend fun actualizarToken(token: String, refreshToken: String)

    @Query("DELETE FROM Token")
    suspend fun eliminarToken()


}