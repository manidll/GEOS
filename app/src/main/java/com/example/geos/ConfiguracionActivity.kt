package com.example.geos

import android.app.AlertDialog
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

/**
 * Actividad de configuración de la aplicación GEOS.
 *
 * Permite al usuario acceder a opciones de configuración como:
 * - Volver al menú principal
 * - Exportación de datos (placeholder)
 * - Visualizar la política de privacidad
 * - Consultar permisos requeridos
 * - Información de la aplicación (Acerca de)
 * - Información de contacto
 *
 * Todos los botones muestran diálogos informativos para guiar al usuario.
 */
class ConfiguracionActivity : AppCompatActivity() {

    /**
     * Método principal que se ejecuta al crear la actividad.
     * Inicializa la interfaz y define los eventos de los botones.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion)

        // 🔹 Botón de volver al menú principal
        // Permite regresar a la pantalla anterior usando el dispatcher de back
        findViewById<LinearLayout>(R.id.btnBackConfiguracion).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 🔹 Botón de Exportación (funcionalidad aún no implementada)
        findViewById<Button>(R.id.btnElegirCarpeta).setOnClickListener {
            mostrarDialogo(
                "Exportación",
                "Funcionalidad de exportación próximamente disponible."
            )
        }

        // 🔹 Botón de Política de Privacidad
        // Muestra información legal sobre cómo se manejan los datos de los usuarios
        findViewById<Button>(R.id.btnPolitica).setOnClickListener {
            mostrarDialogo(
                "Política de Privacidad",
                "GEOS recopila y almacena datos proporcionados por los usuarios durante el levantamiento de padrón de la Comisión Estatal del Agua de Sonora.\n" +
                        "La información recolectada (como nombre, dirección, coordenadas GPS y fotografías) se utiliza únicamente con fines administrativos y de registro interno.\n" +
                        "Los datos son manejados con confidencialidad y no se comparten con terceros ajenos a la Comisión.\n" +
                        "El acceso a la información está restringido al personal autorizado y se emplean medidas para resguardar su seguridad.\n" +
                        "Para cualquier duda o solicitud relacionada con la privacidad de los datos, puede contactarse al área de sistemas de la Comisión Estatal del Agua de Sonora."
            )
        }

        // 🔹 Botón de Permisos
        // Informa al usuario sobre los permisos que requiere la aplicación
        findViewById<Button>(R.id.btnPermisosApp).setOnClickListener {
            mostrarDialogo(
                "Permisos de la aplicación",
                "Esta app requiere permisos de ubicación y almacenamiento para funcionar correctamente."
            )
        }

        // 🔹 Botón Acerca de
        // Proporciona información general de la app y su objetivo
        findViewById<Button>(R.id.btnAcercaDe).setOnClickListener {
            mostrarDialogo(
                "Acerca de",
                "Versión 1.0\n\nAplicación desarrollada para CEA.\n\n" +
                        "Esta aplicación fue desarrollada para apoyar el levantamiento de padrón de la Comisión Estatal del Agua de Sonora en el municipio de Guaymas.\n" +
                        "Su objetivo es facilitar la captura y organización de información de los usuarios en campo, contribuyendo a la mejora del sistema de gestión dentro de las instalaciones."
            )
        }

        // 🔹 Botón de Contacto
        // Muestra el correo electrónico de soporte
        findViewById<Button>(R.id.btnContacto).setOnClickListener {
            mostrarDialogo(
                "Contacto",
                "Correo: soporte@padronapp.com"
            )
        }
    }

    /**
     * Método auxiliar para mostrar un diálogo tipo aviso.
     *
     * @param titulo Título del diálogo
     * @param mensaje Mensaje que se mostrará al usuario
     */
    private fun mostrarDialogo(titulo: String, mensaje: String) {
        val dialog = AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton("Aceptar", null) // Solo botón de aceptación
            .create()

        dialog.show()
    }
}
