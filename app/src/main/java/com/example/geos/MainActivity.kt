package com.example.geos

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.geos.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.poi.ss.usermodel.ClientAnchor
import org.apache.poi.xssf.usermodel.XSSFClientAnchor
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var currentPhotoUri: Uri
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLat: Double? = null
    private var currentLng: Double? = null
    private val listaRegistros = mutableListOf<Usuario>()
    private lateinit var db: UsuarioDataBase
    private lateinit var tvRutaFoto: TextView
    private lateinit var ivCheckFoto: ImageView

    // Lanzador de permisos para cámara y ubicación
    private val permisosLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        val granted = permisos[Manifest.permission.CAMERA] == true &&
                permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (!granted) {
            Toast.makeText(this, "Debes conceder todos los permisos para usar la app", Toast.LENGTH_LONG).show()
        }
    }



    /**
     * Función principal que se ejecuta al iniciar la Activity.
     * Configura permisos, base de datos, cámara, cliente de ubicación
     * y listeners de botones.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tvRutaFoto = binding.root.findViewById(R.id.tvRutaFoto)
        ivCheckFoto = binding.root.findViewById(R.id.ivCheckFoto)


        permisosLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        db = Room.databaseBuilder(
            applicationContext,
            UsuarioDataBase::class.java,
            "usuarios_db"
        ).build()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Configura el resultado del intent de cámara
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                tvRutaFoto.text = currentPhotoUri.path  // Mostrar ruta
                ivCheckFoto.visibility = ImageView.VISIBLE // Mostrar palomita
                Toast.makeText(this, "Foto tomada correctamente", Toast.LENGTH_SHORT).show()
                obtenerUbicacion()
            } else {
                Toast.makeText(this, "Error al tomar la foto", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnFoto.setOnClickListener { tomarFoto() }
        binding.btnAgregar.setOnClickListener { agregarUsuario() }
        binding.btnEscribir.setOnClickListener {
            if (listaRegistros.isNotEmpty()) exportarExcel(listaRegistros)
            else Toast.makeText(this, "No hay registros para exportar", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Inicia la cámara del dispositivo y crea un archivo temporal
     * donde se almacenará la imagen capturada.
     */
    private fun tomarFoto() {
        val photoFile = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "foto_${System.currentTimeMillis()}.jpg"
        )
        currentPhotoUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", photoFile)
        grantUriPermission(
            "com.android.camera",
            currentPhotoUri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        takePictureLauncher.launch(currentPhotoUri)
    }

    /**
     * Lee los datos ingresados por el usuario en los campos de texto,
     * valida que haya información suficiente, foto y ubicación;
     * luego crea un objeto [Usuario], lo guarda en la lista local
     * y en la base de datos Room.
     */
    private fun agregarUsuario() {
        val inmueble = binding.etInmueble.text.toString().trim()
        val nombre = binding.etNombreUsuario.text.toString().trim()
        val localizacion = binding.etLocalizacion.text.toString().trim()
        val giroD = binding.etGiroD.text.toString().trim()
        val servicioD = binding.etServicioD.text.toString().trim()
        val situacionD = binding.etSituacionD.text.toString().trim()
        val codigoPostal = binding.etCodigoPostal.text.toString().trim()
        val seccion = binding.etSeccion.text.toString().trim()
        val rutaD = binding.etRutaD.text.toString().trim()
        val derivada = binding.etDerivada.text.toString().trim()
        val ubicacionToma = binding.etUbicacionToma.text.toString().trim()
        val numSerieMedidor = binding.etNumSerieMedidor.text.toString().trim()
        val numMedidorC = binding.etNumMedidor.text.toString().trim()
        val modeloDmedidor = binding.etModeloMedidor.text.toString().trim()
        val regimenFis = binding.etRegimenFiscal.text.toString().trim()

        if (nombre.isEmpty() || inmueble.isEmpty()) {
            Toast.makeText(this, "Completa al menos los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        if (!::currentPhotoUri.isInitialized || currentPhotoUri == Uri.EMPTY) {
            Toast.makeText(this, "Debes tomar una foto antes de agregar", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentLat == null || currentLng == null) {
            Toast.makeText(this, "No se pudo obtener ubicación", Toast.LENGTH_SHORT).show()
            return
        }

        val usuario = Usuario(
            inmueble = inmueble,
            nombreUsuario = nombre,
            localizacion = localizacion,
            giroD = giroD,
            servicioD = servicioD,
            situacionD = situacionD,
            codigoPostal = codigoPostal,
            seccion = seccion,
            rutaD = rutaD,
            derivada = derivada,
            ubicacionToma = ubicacionToma,
            numSerieMedidor = numSerieMedidor,
            numMedidorC = numMedidorC,
            modeloDmedidor = modeloDmedidor,
            regimenFis = regimenFis,
            fotoUri = currentPhotoUri.toString(),
            latitud = currentLat,
            longitud = currentLng
        )

        listaRegistros.add(usuario)

        lifecycleScope.launch(Dispatchers.IO) {
            db.usuarioDao().insertarUsuario(usuario)
        }

        limpiarCampos()
        Toast.makeText(this, "Usuario agregado correctamente", Toast.LENGTH_SHORT).show()
    }


    /**
     * Limpia todos los campos del formulario después de agregar un registro
     * y reinicia los valores de foto y ubicación.
     */
    private fun limpiarCampos() {
        binding.apply {
            etInmueble.text?.clear()
            etNombreUsuario.text?.clear()
            etLocalizacion.text?.clear()
            etGiroD.text?.clear()
            etServicioD.text?.clear()
            etSituacionD.text?.clear()
            etCodigoPostal.text?.clear()
            etSeccion.text?.clear()
            etRutaD.text?.clear()
            etDerivada.text?.clear()
            etUbicacionToma.text?.clear()
            etNumSerieMedidor.text?.clear()
            etNumMedidor.text?.clear()
            etModeloMedidor.text?.clear()
            etRegimenFiscal.text?.clear()

            tvRutaFoto.text = "No hay foto tomada"
            ivCheckFoto.visibility = ImageView.GONE
        }

        currentPhotoUri = Uri.EMPTY
        currentLat = null
        currentLng = null
    }


    /**
     * Obtiene la ubicación actual del dispositivo usando el cliente
     * de ubicación de Google ([FusedLocationProviderClient]).
     * Asigna los valores de latitud y longitud a variables globales.
     */
    private fun obtenerUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                currentLat = location.latitude
                currentLng = location.longitude
            }
        }
    }

    /**
     * Exporta la lista de usuarios a un archivo Excel (.xlsx) usando Apache POI.
     * Crea una hoja con encabezados, escribe los datos y agrega las imágenes
     * correspondientes a cada registro.
     *
     * @param listaUsuarios lista con los registros a exportar.
     */
    private fun exportarExcel(listaUsuarios: MutableList<Usuario>) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Usuarios")

        // Crear encabezados
        val headerRow = sheet.createRow(0)
        val headers = listOf(
            "Inmueble", "Nombre", "Localización", "Giro", "Servicio", "Situación",
            "Código Postal", "Sección", "Ruta", "Derivada", "Ubicación Toma",
            "Num Serie Medidor", "Num Medidor", "Modelo Medidor", "Régimen Fiscal",
            "Latitud", "Longitud", "Foto"
        )
        headers.forEachIndexed { i, h -> headerRow.createCell(i).setCellValue(h) }

        // Llenar filas con datos
        listaUsuarios.forEachIndexed { index, usuario ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(usuario.inmueble.uppercase())
            row.createCell(1).setCellValue(usuario.nombreUsuario.uppercase())
            row.createCell(2).setCellValue(usuario.localizacion.uppercase())
            row.createCell(3).setCellValue(usuario.giroD.uppercase())
            row.createCell(4).setCellValue(usuario.servicioD.uppercase())
            row.createCell(5).setCellValue(usuario.situacionD.uppercase())
            row.createCell(6).setCellValue(usuario.codigoPostal.uppercase())
            row.createCell(7).setCellValue(usuario.seccion.uppercase())
            row.createCell(8).setCellValue(usuario.rutaD.uppercase())
            row.createCell(9).setCellValue(usuario.derivada.uppercase())
            row.createCell(10).setCellValue(usuario.ubicacionToma.uppercase())
            row.createCell(11).setCellValue(usuario.numSerieMedidor.uppercase())
            row.createCell(12).setCellValue(usuario.numMedidorC.uppercase())
            row.createCell(13).setCellValue(usuario.modeloDmedidor.uppercase())
            row.createCell(14).setCellValue(usuario.regimenFis.uppercase())
            row.createCell(15).setCellValue(usuario.latitud ?: 0.0)
            row.createCell(16).setCellValue(usuario.longitud ?: 0.0)

            // Insertar imagen si existe
            try {
                val fotoFile = File(Uri.parse(usuario.fotoUri).path!!)
                if (fotoFile.exists()) {
                    val bytes = fotoFile.readBytes()
                    val pictureIdx = workbook.addPicture(bytes, XSSFWorkbook.PICTURE_TYPE_JPEG)
                    val drawing = sheet.createDrawingPatriarch()
                    val anchor = XSSFClientAnchor(0, 0, 1023, 255, 17, index + 1, 18, index + 2)
                    anchor.anchorType = ClientAnchor.AnchorType.MOVE_AND_RESIZE
                    drawing.createPicture(anchor, pictureIdx).resize(1.0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Guardar archivo en Descargas
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(path, "padron_2025.xlsx")
        try {
            val out = FileOutputStream(file)
            workbook.write(out)
            out.close()
            workbook.close()
            Toast.makeText(this, "Archivo exportado: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al exportar", Toast.LENGTH_SHORT).show()
        }
    }

}
