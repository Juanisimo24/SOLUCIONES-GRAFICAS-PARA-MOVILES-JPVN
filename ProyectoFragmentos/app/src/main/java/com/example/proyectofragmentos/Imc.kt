package com.example.proyectofragmentos
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.proyectofragmentos.R
class Imc : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_imc, container, false)

        val etPeso = view.findViewById<EditText>(R.id.etPeso)
        val etAltura = view.findViewById<EditText>(R.id.etAltura)
        val btnCalcular = view.findViewById<Button>(R.id.btnCalcularImc)
        val tvResultado = view.findViewById<TextView>(R.id.tvResultadoImc)
        val tvCategoria = view.findViewById<TextView>(R.id.tvCategoriaImc)

        btnCalcular.setOnClickListener {
            val pesoStr = etPeso.text.toString()
            val alturaStr = etAltura.text.toString()

            if (pesoStr.isEmpty() || alturaStr.isEmpty()) {
                (activity as MainActivity).mensaje("Por favor llena todos los campos")
                return@setOnClickListener
            }

            val peso = pesoStr.toDouble()
            val altura = alturaStr.toDouble() / 100  // convierte cm a metros

            if (altura <= 0) {
                (activity as MainActivity).mensaje("Altura no válida")
                return@setOnClickListener
            }

            val imc = peso / (altura * altura)
            val imcFormateado = String.format("%.2f", imc)

            tvResultado.text = "IMC: $imcFormateado"

            val categoria = when {
                imc < 18.5 -> "Bajo peso"
                imc < 25.0 -> "Peso normal ✅"
                imc < 30.0 -> "Sobrepeso"
                else -> "Obesidad"
            }

            tvCategoria.text = categoria
        }

        return view
    }
}