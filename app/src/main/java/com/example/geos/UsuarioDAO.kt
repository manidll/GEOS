package com.example.geos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UsuarioDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: Usuario)

    @Query("SELECT * FROM usuarios")
    suspend fun obtenerUsuarios(): List<Usuario>

    @Query("DELETE FROM usuarios WHERE id = :id")
    suspend fun eliminarUsuario(id: Int)

    @Query("DELETE FROM usuarios")
    suspend fun eliminarTodos()
}

