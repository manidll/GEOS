package com.example.geos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Usuario::class], version = 2) // versión aumentada
abstract class UsuarioDataBase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDAO

    companion object {
        @Volatile
        private var INSTANCE: UsuarioDataBase? = null

        fun getDatabase(context: Context): UsuarioDataBase {
            return INSTANCE ?: synchronized(this) {

                // Migración de la versión 1 a la 2
                val MIGRATION_1_2 = object : Migration(1, 2) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("ALTER TABLE usuarios ADD COLUMN fotoUri TEXT")
                        database.execSQL("ALTER TABLE usuarios ADD COLUMN latitud REAL")
                        database.execSQL("ALTER TABLE usuarios ADD COLUMN longitud REAL")
                    }
                }

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UsuarioDataBase::class.java,
                    "usuarios_db"
                )
                    .addMigrations(MIGRATION_1_2) // agregamos migración
                    //.fallbackToDestructiveMigration() // opcional si quieres borrar la DB vieja
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
