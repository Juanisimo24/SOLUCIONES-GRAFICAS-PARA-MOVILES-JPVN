package com.example.proyectofragmentos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class Inicio : Fragment() {

    private lateinit var tvQR: TextView
    private lateinit var tvUbicacion: TextView

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            (activity as MainActivity).mensaje("Cancelado")
        } else {
            tvQR.text = "qr: ${result.contents}"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_inicio, container, false)

        tvQR = view.findViewById(R.id.tvQR)
        tvUbicacion = view.findViewById(R.id.tvUbicacion)

        view.findViewById<Button>(R.id.btnMostrarMensaje).setOnClickListener {
            (activity as MainActivity).mensaje("¡Hola desde Inicio!")
        }

        view.findViewById<Button>(R.id.btnLeerQR).setOnClickListener {
            leerQR()
        }

        view.findViewById<Button>(R.id.btnGPS).setOnClickListener {
            (activity as MainActivity).obtenerUbicacion { ubicacionObtenida ->
                tvUbicacion.text = "latitud y longitud: $ubicacionObtenida"
            }
        }

        return view
    }

    private fun leerQR() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan a QR CODE")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        barcodeLauncher.launch(options)
    }
}