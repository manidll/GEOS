package com.example.geos

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

/**
 * SplashActivity
 *
 * Actividad que muestra una pantalla de bienvenida (splash) al iniciar la aplicación.
 * Tras un tiempo determinado, redirige automáticamente al menú principal (MenuActivity)
 * y cierra la actividad de splash para que no se pueda volver con el botón atrás.
 */
class SplashActivity : AppCompatActivity() {

    /**
     * Método principal de la actividad.
     *
     * Inicializa el layout del splash y programa un retraso antes de abrir
     * el MenuActivity. La actividad se cierra automáticamente después del retraso.
     *
     * @param savedInstanceState Estado previo de la actividad, si existe.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Tiempo de espera del splash (2 segundos)
        Handler(Looper.getMainLooper()).postDelayed({
            // Abrir MenuActivity
            startActivity(Intent(this, MenuActivity::class.java))
            // Cerrar SplashActivity para que no se regrese al presionar "atrás"
            finish()
        }, 2000) // 2000 ms = 2 segundos
    }
}
