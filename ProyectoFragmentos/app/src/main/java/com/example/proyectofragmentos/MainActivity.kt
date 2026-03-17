package com.example.proyectofragmentos

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Permiso preciso concedido
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Permiso aproximado concedido
            }
            else -> {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragmentActual = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
                if (fragmentActual !is Inicio) {
                    reemplazarFragmento(Inicio())
                } else {
                    finish()
                }
            }
        })

        val calcu = findViewById<Button>(R.id.calcu)
        val imc = findViewById<Button>(R.id.imc)

        calcu.setOnClickListener {
            reemplazarFragmento(Calculadora())
        }

        imc.setOnClickListener {
            reemplazarFragmento(Imc())
        }

        // Cargar Inicio por defecto
        if (savedInstanceState == null) {
            reemplazarFragmento(Inicio())
        }
    }

    private fun reemplazarFragmento(fragmento: Fragment) {
        val manager = supportFragmentManager
        val transaccion = manager.beginTransaction()
        transaccion.replace(R.id.fragmentContainerView, fragmento)
        transaccion.commit()
    }

    fun mensaje(texto: String) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show()
    }

    fun requestPermissions() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    fun obtenerUbicacion(callback: (String) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions()
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                val ubicacion = location?.let {
                    "Lat: ${it.latitude}, Long: ${it.longitude}"
                } ?: "Ubicación no disponible"
                callback(ubicacion)
            }
    }
}