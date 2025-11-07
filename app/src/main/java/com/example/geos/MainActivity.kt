package com.example.geos

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
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

        db = UsuarioDataBase.getDatabase(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnBackContainer.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                tvRutaFoto.text = currentPhotoUri.path
                ivCheckFoto.visibility = ImageView.VISIBLE
                Toast.makeText(this, "Foto tomada correctamente", Toast.LENGTH_SHORT).show()
                obtenerUbicacion()
            } else {
                Toast.makeText(this, "Error al tomar la foto", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnFoto.setOnClickListener { tomarFoto() }
        binding.btnAgregar.setOnClickListener { agregarUsuario() }

        // 🔹 Exportar Excel
        binding.btnExportar.setOnClickListener {
            exportarExcel()
        }


        // 🔹 Abrir Excel actualizado
        binding.btnAbrirExcel.setOnClickListener {
            abrirExcel()
        }
    }

    private fun tomarFoto() {
        val photoFile = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "foto_${System.currentTimeMillis()}.jpg"
        )
        currentPhotoUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", photoFile)
        takePictureLauncher.launch(currentPhotoUri)
    }

    private fun validarCampos(vararg campos: EditText): Boolean {
        var valido = true
        for (campo in campos) {
            if (campo.text.toString().trim().isEmpty()) {
                campo.error = "Campo Obligatorio"
                valido = false
            }
        }
        return valido
    }

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

        val validos = validarCampos(
            binding.etInmueble, binding.etNombreUsuario, binding.etLocalizacion,
            binding.etGiroD, binding.etServicioD, binding.etSituacionD, binding.etCodigoPostal,
            binding.etSeccion, binding.etRutaD, binding.etDerivada, binding.etUbicacionToma,
            binding.etNumSerieMedidor, binding.etNumMedidor, binding.etModeloMedidor, binding.etRegimenFiscal
        )

        if (!validos) {
            Toast.makeText(this, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
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

    private fun exportarExcel() {
        lifecycleScope.launch {
            try {
                val db = UsuarioDataBase.getDatabase(this@MainActivity)
                val listaUsuarios = db.usuarioDao().obtenerUsuarios()

                if (listaUsuarios.isEmpty()) {
                    Toast.makeText(this@MainActivity, "No hay usuarios para exportar", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val workbook = XSSFWorkbook()
                val sheet = workbook.createSheet("Usuarios")

                val headerRow = sheet.createRow(0)
                val headers = listOf(
                    "Inmueble", "Nombre", "Localización", "Giro", "Servicio", "Situación",
                    "Código Postal", "Sección", "Ruta", "Derivada", "Ubicación Toma",
                    "Num Serie Medidor", "Num Medidor", "Modelo Medidor", "Régimen Fiscal",
                    "Latitud", "Longitud", "Foto"
                )
                headers.forEachIndexed { i, h -> headerRow.createCell(i).setCellValue(h) }

                listaUsuarios.forEachIndexed { index, usuario ->
                    val row = sheet.createRow(index + 1)
                    row.createCell(0).setCellValue(usuario.inmueble)
                    row.createCell(1).setCellValue(usuario.nombreUsuario)
                    row.createCell(2).setCellValue(usuario.localizacion)
                    row.createCell(3).setCellValue(usuario.giroD)
                    row.createCell(4).setCellValue(usuario.servicioD)
                    row.createCell(5).setCellValue(usuario.situacionD)
                    row.createCell(6).setCellValue(usuario.codigoPostal)
                    row.createCell(7).setCellValue(usuario.seccion)
                    row.createCell(8).setCellValue(usuario.rutaD)
                    row.createCell(9).setCellValue(usuario.derivada)
                    row.createCell(10).setCellValue(usuario.ubicacionToma)
                    row.createCell(11).setCellValue(usuario.numSerieMedidor)
                    row.createCell(12).setCellValue(usuario.numMedidorC)
                    row.createCell(13).setCellValue(usuario.modeloDmedidor)
                    row.createCell(14).setCellValue(usuario.regimenFis)
                    row.createCell(15).setCellValue(usuario.latitud ?: 0.0)
                    row.createCell(16).setCellValue(usuario.longitud ?: 0.0)
                }

                val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(path, "padron_2025.xlsx")
                val out = FileOutputStream(file)
                workbook.write(out)
                out.close()
                workbook.close()

                Toast.makeText(this@MainActivity, "Archivo exportado: ${file.absolutePath}", Toast.LENGTH_LONG).show()

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "Error al exportar", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // 🔹 Abrir el Excel actualizado
    private fun abrirExcel() {
        try {
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "padron_2025.xlsx")
            if (!file.exists()) {
                Toast.makeText(this, "Primero exporta el archivo Excel", Toast.LENGTH_SHORT).show()
                return
            }

            val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK

            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No se pudo abrir el archivo", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}
