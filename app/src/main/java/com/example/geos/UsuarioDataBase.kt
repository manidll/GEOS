package com.example.geos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Clase que representa la base de datos local de la aplicación utilizando Room.
 *
 * Esta base de datos almacena objetos del tipo [Usuario], definidos en la entidad correspondiente.
 * Proporciona un acceso estructurado y persistente a los datos del usuario sin necesidad de manejar
 * directamente SQL, aprovechando las ventajas de Room (ORM de Android).
 */
@Database(entities = [Usuario::class], version = 1)
abstract class UsuarioDataBase : RoomDatabase() {

    /**
     * Devuelve una instancia del DAO (Data Access Object) que permite interactuar
     * con la tabla de usuarios. A través del DAO se realizan operaciones como:
     * - Insertar registros
     * - Consultar usuarios
     * - Actualizar o eliminar datos (si se definen métodos adicionales)
     */
    abstract fun usuarioDao(): UsuarioDAO

    companion object {
        // Mantiene una sola instancia de la base de datos en toda la aplicación (Singleton)
        @Volatile
        private var INSTANCE: UsuarioDataBase? = null


        /**
         * Devuelve la instancia única de la base de datos.
         *
         * Si no existe, la crea de forma segura utilizando un bloque synchronized
         * para evitar que se creen múltiples instancias simultáneamente en hilos diferentes.
         *
         * @param context Contexto de la aplicación, necesario para construir la base de datos.
         * @return Instancia única de [UsuarioDataBase].
         */

        fun getDatabase(context: Context): UsuarioDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UsuarioDataBase::class.java,
                    "usuarios_db" // Nombre del archivo de base de datos generado
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
