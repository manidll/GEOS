package com.example.geos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

/**
 * MenuActivity
 *
 * Actividad que muestra el menú principal de la aplicación.
 * Permite navegar a:
 *  - Registro de usuarios (MainActivity)
 *  - Historial de usuarios (HistorialActivity)
 *  - Configuración de la aplicación (ConfiguracionActivity)
 */
class MenuActivity : AppCompatActivity() {

    /**
     * Método principal de la actividad.
     *
     * Inicializa el layout y configura los botones del menú para navegar
     * a las distintas secciones de la aplicación.
     *
     * @param savedInstanceState Estado previo de la actividad, si existe.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Botón para ir a la pantalla de registro de usuarios
        findViewById<MaterialButton>(R.id.btnRegistro).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // Botón para ir a la pantalla de historial de usuarios
        findViewById<MaterialButton>(R.id.btnHistorial).setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
        }

        // Botón para ir a la pantalla de configuración
        findViewById<MaterialButton>(R.id.btnConfiguracion).setOnClickListener {
            startActivity(Intent(this, ConfiguracionActivity::class.java))
        }
    }
}
