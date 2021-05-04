package com.alcanosesp.appalcanos.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.alcanosesp.appalcanos.db.dao.*
import com.alcanosesp.appalcanos.db.entity.*

@Database(
    entities = [
        AppConfigParameter::class,
        Token::class,
        Funcionario::class,
        Familiar::class,
        Estudio::class,
        Experiencia::class,
        AusentismoFuncionario::class,
        SolicitudBeneficio::class,
        ArchivoAdjunto::class,
        SolicitudPermiso::class,
        SolicitudPermisoSoporte::class,
        SolicitudCesantia::class,
        SolicitudVacaciones::class
    ],
    exportSchema = false,
    version = 7
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun configDao(): DAOAppConfig
    abstract fun tokenDao(): DAOToken
    abstract fun funcionarioDao(): DAOFuncionario
    abstract fun familiarDao(): DAOFamiliar
    abstract fun estudioDao(): DAOEstudio
    abstract fun experienciaDao(): DAOExperiencia
    abstract fun ausentismoFuncionarioDao(): DAOAusentismoFuncionario
    abstract fun solicitudBeneficioDao(): DAOSolicitudBeneficio
    abstract fun archivoAdjuntoDao(): DAOArchivoAdjunto
    abstract fun solicitudPermisoDao(): DAOSolicitudPermiso
    abstract fun solicitudPermisoSoporteDao(): DAOSolicitudPermisoSoporte
    abstract fun solicitudCesantiasDao(): DAOSolicitudCesantia
    abstract fun solicitudVacacionesDao(): DAOSolicitudVacaciones


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private val BLOCK = Any()

        operator fun invoke(context: Context) = INSTANCE ?: synchronized(BLOCK) {
            INSTANCE ?: buildDatabase(context).also {
                INSTANCE = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "appDatabase"
        ).fallbackToDestructiveMigration()
            .build()
    }
}