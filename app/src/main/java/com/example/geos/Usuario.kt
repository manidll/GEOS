package com.example.geos
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa a un usuario dentro de la base de datos Room.
 *
 * Esta clase define la estructura de la tabla **"usuarios"**, incluyendo
 * datos personales, información del inmueble, detalles del medidor y
 * geolocalización.
 *
 * Se usa junto con [UsuarioDAO] y [UsuarioDataBase] para el almacenamiento
 * y recuperación de datos locales en la aplicación.
 */
@Entity(tableName = "usuarios")
data class Usuario(
    /** Identificador único del registro (autogenerado por Room). */
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    // ─────────────────────────────────────────────────────────────
    // DATOS DEL USUARIO E INMUEBLE
    // ─────────────────────────────────────────────────────────────

    /** Numero del inmueble asociado al usuario */
    val inmueble: String = "",
    /** Nombre completo del usuario o responsable del inmueble. */
    val nombreUsuario: String = ""
    /** Dirección o ubicación física del inmueble. */,
    val localizacion: String = "",
    /** Giro del negocio o actividad que realiza el usuario. */
    val giroD: String = "",
    /** Tipo de servicio contratado (por ejemplo: domestico, comercial, especial es.). */
    val servicioD: String = "",
    /** Situación del usuario (activo, 2do corte, casa sola no habitado, estacionado.). */
    val situacionD: String = "",
    // val dirFacturacion: String = "0",
    // val dirEnvio: String = "0",
    /** Código postal del domicilio. */
    val codigoPostal: String = "",
    /** Sección o área geográfica asignada al usuario. */
    val seccion: String = "",

    // ─────────────────────────────────────────────────────────────
    // DATOS DE RUTA Y SERVICIO
    // ─────────────────────────────────────────────────────────────

    //val toma: String = "",
    /** Ruta asignada al inmueble. */
    val rutaD: String = "",
    //val regimen: String = "",
    /** Identificador derivado dentro de la ruta principal. */
    val derivada: String = "",
    /** Estado actual del usuario (por ejemplo: activo o inactivo e indefinido). */
    val estado: estadoD = estadoD.ACTIVO,
    //val interior: String = "",
    /** Descripción de la ubicación donde se encuentra la toma de agua. */
    val ubicacionToma: String = "",
    /** Tipo de servicio contratado (agua potable, indefinido, etc.). */
    val servContrato: servicioContrato = servicioContrato.AGUA_POTABLE,

    // ─────────────────────────────────────────────────────────────
    // DATOS DEL MEDIDOR
    // ─────────────────────────────────────────────────────────────

    /** Número de serie del medidor instalado. */
    val numSerieMedidor: String = "",
    /** Número de cuenta o código del medidor. */
    val numMedidorC: String = "",
    /** Modelo o tipo del medidor utilizado. */
    val modeloDmedidor: String = "",
    /** Estado del servicio del medidor (en servicio, toma taponada tt, etc.). */
    val estadoServicioMedidor: estadotMedidorD = estadotMedidorD.EN_SERVICIO_SE,
    /** Estado físico del medidor (funcionando, dañado, etc.). */
    val estadoMedidor: estadoMedidorD = estadoMedidorD.FUNCIONANDO,
    /** Clave de Régimen fiscal del usuario (por ejemplo: persona física o moral). */
    val regimenFis: String = "",

    // ─────────────────────────────────────────────────────────────
    // GEOLOCALIZACIÓN
    // ─────────────────────────────────────────────────────────────

    /** URI de la foto tomada del inmueble o punto de medición. */
    val fotoUri: String? = null,
    /** Coordenada de latitud obtenida al tomar la foto. */
    val latitud: Double? = null,
    /** Coordenada de longitud obtenida al tomar la foto. */
    val longitud: Double? = null
)