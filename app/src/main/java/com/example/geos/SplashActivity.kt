package com.example.geos

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Tiempo de espera del splash (ejemplo: 2 segundos)
        Handler(Looper.getMainLooper()).postDelayed({
            // Abrir MenuActivity
            startActivity(Intent(this, MenuActivity::class.java))
            // Cerrar SplashActivity para que no se regrese al presionar "atrás"
            finish()
        }, 2000) // 2000 ms = 2 segundos
    }
}

