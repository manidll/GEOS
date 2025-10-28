package com.example.geos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        findViewById<MaterialButton>(R.id.btnRegistro).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java)) // tu layout de registro
        }

        findViewById<MaterialButton>(R.id.btnHistorial).setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btnConfiguracion).setOnClickListener {
            startActivity(Intent(this, ConfiguracionActivity::class.java))
        }
    }
}
