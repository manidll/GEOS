package com.example.geos

import android.os.Bundle
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistorialActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UsuarioAdapter
    private lateinit var db: UsuarioDataBase
    private lateinit var listaUsuariosOriginal: List<Usuario>
    private lateinit var listaUsuarios: MutableList<Usuario>

    /**
     * Método principal de la actividad.
     *
     * Inicializa el layout, configura el RecyclerView y su adapter,
     * carga los usuarios desde la base de datos, configura la búsqueda
     * y el botón de retroceso.
     *
     * @param savedInstanceState Estado previo de la actividad, si existe.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        recyclerView = findViewById(R.id.rvHistorial)
        recyclerView.layoutManager = LinearLayoutManager(this)
        db = UsuarioDataBase.getDatabase(this)

        val searchView = findViewById<SearchView>(R.id.searchView)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Cargar registros desde la base de datos en un hilo de fondo
        lifecycleScope.launch(Dispatchers.IO) {
            listaUsuariosOriginal = db.usuarioDao().obtenerUsuarios()
            listaUsuarios = listaUsuariosOriginal.toMutableList()
            withContext(Dispatchers.Main) {
                adapter = UsuarioAdapter(listaUsuarios) { usuario ->
                    eliminarUsuario(usuario)
                }
                recyclerView.adapter = adapter
            }
        }

        // Configuración de la búsqueda en tiempo real
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val texto = newText ?: ""
                val listaFiltrada = if (texto.isEmpty()) {
                    listaUsuariosOriginal
                } else {
                    listaUsuariosOriginal.filter {
                        it.nombreUsuario.contains(texto, ignoreCase = true)
                    }
                }
                adapter.actualizarLista(listaFiltrada)
                return true
            }
        })
    }

    /**
     * Elimina un usuario de la base de datos después de confirmar con el usuario.
     *
     * Muestra un diálogo de confirmación. Si el usuario confirma, elimina el usuario
     * de la base de datos, actualiza la lista original y mutable, y refresca el RecyclerView.
     *
     * @param usuario Usuario que será eliminado.
     */
    private fun eliminarUsuario(usuario: Usuario) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar registro")
            .setMessage("¿Deseas eliminar a ${usuario.nombreUsuario}?")
            .setPositiveButton("Sí") { _, _ ->
                lifecycleScope.launch(Dispatchers.IO) {
                    db.usuarioDao().eliminarUsuario(usuario.id)
                    withContext(Dispatchers.Main) {
                        listaUsuariosOriginal = listaUsuariosOriginal.filter { it.id != usuario.id }
                        listaUsuarios.remove(usuario)
                        adapter.notifyDataSetChanged()
                        Toast.makeText(this@HistorialActivity, "Registro eliminado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("No", null)
            .show()
    }
}
