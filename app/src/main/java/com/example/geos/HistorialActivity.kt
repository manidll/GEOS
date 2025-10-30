package com.example.geos

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistorialActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var db: UsuarioDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        recyclerView = findViewById(R.id.rvHistorial)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = UsuarioDataBase.getDatabase(this)

        // 🔹 Lógica del botón de volver
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack?.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 🔹 Cargar registros desde Room
        lifecycleScope.launch(Dispatchers.IO) {
            val listaUsuarios = db.usuarioDao().obtenerUsuarios()
            withContext(Dispatchers.Main) {
                recyclerView.adapter = UsuarioAdapter(listaUsuarios)
            }
        }
    }
}
