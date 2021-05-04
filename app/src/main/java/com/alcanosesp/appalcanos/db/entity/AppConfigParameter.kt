package com.alcanosesp.appalcanos.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lista_config")
data class AppConfigParameter(@PrimaryKey @ColumnInfo(name = "config_id") var id : Int,
                              @ColumnInfo(name = "config_nombre") var nombre : String?,
                              @ColumnInfo(name = "config_valor") var valor : String?)
