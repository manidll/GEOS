package com.example.geos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import com.google.android.material.button.MaterialButton

class HistorialActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UsuarioAdapter
    private lateinit var db: UsuarioDataBase
    private lateinit var listaUsuariosOriginal: List<Usuario>
    private lateinit var listaUsuarios: MutableList<Usuario>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        recyclerView = findViewById(R.id.rvHistorial)
        recyclerView.layoutManager = LinearLayoutManager(this)
        db = UsuarioDataBase.getDatabase(this)

        val searchView = findViewById<SearchView>(R.id.searchView)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnAbrir = findViewById<MaterialButton>(R.id.btnAbrirExcelHis)


        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Botón para abrir el Excel
        btnAbrir.setOnClickListener {
            abrirExcel()
        }

        // Cargar registros desde la base de datos
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

        // Búsqueda en tiempo real
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
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
     * Abre el archivo Excel exportado si existe.
     * Usa FileProvider para obtener una URI segura y lo abre con una app compatible (Excel, WPS, Sheets, etc.).
     */
    private fun abrirExcel() {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "padron_2025.xlsx"
        )

        if (!file.exists()) {
            Toast.makeText(this, "Primero exporta el archivo Excel.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(
                uri,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this,
                "No se pudo abrir el archivo. Asegúrate de tener una app compatible.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Elimina un usuario de la base de datos después de confirmación.
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
                        Toast.makeText(
                            this@HistorialActivity,
                            "Registro eliminado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            .setNegativeButton("No", null)
            .show()
    }
}
