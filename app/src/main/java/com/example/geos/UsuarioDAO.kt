package com.example.geos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


/**
 * Interfaz Data Access Object (DAO) para gestionar las operaciones
 * de base de datos relacionadas con la entidad [Usuario].
 *
 * Define los métodos de inserción y consulta que se usarán con Room.
 */
@Dao
interface UsuarioDAO {

    /**
     * Inserta un nuevo registro de usuario en la base de datos.
     *
     * Si el registro ya existe (según su clave primaria),
     * se reemplaza con la nueva información.
     *
     * @param usuario objeto [Usuario] que se desea guardar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: Usuario)


    /**
     * Obtiene la lista completa de usuarios almacenados en la base de datos.
     *
     * @return lista de todos los objetos [Usuario].
     */
    @Query("SELECT * FROM usuarios")
    suspend fun obtenerUsuarios(): List<Usuario>
}
