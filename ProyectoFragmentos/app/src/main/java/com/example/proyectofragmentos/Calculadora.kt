package com.example.proyectofragmentos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlin.math.abs
import kotlin.math.floor

class Calculadora : Fragment() {

    private lateinit var tvResultado: TextView
    private lateinit var tvOperacion: TextView

    private var primerNumero: Double = 0.0
    private var operacion: String = ""
    private var nuevaOperacion: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_calculadora, container, false)

        tvResultado = view.findViewById(R.id.tvResultado)
        tvOperacion = view.findViewById(R.id.tvOperacion)

        inicializarBotones(view)

        return view
    }

    private fun inicializarBotones(view: View) {

        // Numeros
        view.findViewById<Button>(R.id.btn0).setOnClickListener { escribirNumero("0") }
        view.findViewById<Button>(R.id.btn1).setOnClickListener { escribirNumero("1") }
        view.findViewById<Button>(R.id.btn2).setOnClickListener { escribirNumero("2") }
        view.findViewById<Button>(R.id.btn3).setOnClickListener { escribirNumero("3") }
        view.findViewById<Button>(R.id.btn4).setOnClickListener { escribirNumero("4") }
        view.findViewById<Button>(R.id.btn5).setOnClickListener { escribirNumero("5") }
        view.findViewById<Button>(R.id.btn6).setOnClickListener { escribirNumero("6") }
        view.findViewById<Button>(R.id.btn7).setOnClickListener { escribirNumero("7") }
        view.findViewById<Button>(R.id.btn8).setOnClickListener { escribirNumero("8") }
        view.findViewById<Button>(R.id.btn9).setOnClickListener { escribirNumero("9") }
        view.findViewById<Button>(R.id.btnPunto).setOnClickListener { escribirNumero(".") }

        // Operaciones
        view.findViewById<Button>(R.id.btnMas).setOnClickListener { guardarOperacion("+") }
        view.findViewById<Button>(R.id.btnMenos).setOnClickListener { guardarOperacion("-") }
        view.findViewById<Button>(R.id.btnMul).setOnClickListener { guardarOperacion("*") }
        view.findViewById<Button>(R.id.btnDiv).setOnClickListener { guardarOperacion("/") }

        // Igual
        view.findViewById<Button>(R.id.btnIgual).setOnClickListener { calcularResultado() }

        // C - Limpiar todo
        view.findViewById<Button>(R.id.btnC).setOnClickListener {
            tvResultado.text = "0"
            tvOperacion.text = ""
            primerNumero = 0.0
            operacion = ""
            nuevaOperacion = true
        }

        // DEL - Borrar ultimo digito
        view.findViewById<Button>(R.id.btnBorrar).setOnClickListener {
            val textoActual = tvResultado.text.toString()
            if (textoActual.length > 1) {
                tvResultado.text = textoActual.dropLast(1)
            } else {
                tvResultado.text = "0"
                nuevaOperacion = true
            }
        }

        // +/- Cambiar signo
        view.findViewById<Button>(R.id.btnPlusMinus).setOnClickListener {
            val actual = tvResultado.text.toString().toDoubleOrNull() ?: return@setOnClickListener
            tvResultado.text = formatearNumero(actual * -1)
        }

        // % Porcentaje
        view.findViewById<Button>(R.id.btnPorcentaje).setOnClickListener {
            val actual = tvResultado.text.toString().toDoubleOrNull() ?: return@setOnClickListener
            tvResultado.text = formatearNumero(actual / 100)
        }
    }

    private fun escribirNumero(num: String) {
        if (nuevaOperacion) {
            tvResultado.text = ""
            nuevaOperacion = false
        }

        if (num == "." && tvResultado.text.contains(".")) return
        if (num == "0" && tvResultado.text.toString() == "0") return

        if (tvResultado.text.toString() == "0" && num != ".") {
            tvResultado.text = num
        } else {
            tvResultado.append(num)
        }

        ajustarTamanoTexto(tvResultado.text.length)
    }

    private fun ajustarTamanoTexto(longitud: Int) {
        tvResultado.textSize = when {
            longitud <= 6  -> 60f
            longitud <= 9  -> 44f
            longitud <= 12 -> 32f
            else           -> 24f
        }
    }

    private fun guardarOperacion(op: String) {
        primerNumero = tvResultado.text.toString().toDoubleOrNull() ?: 0.0
        operacion = op
        nuevaOperacion = true

        val simbolo = when (op) {
            "+" -> "+"; "-" -> "-"; "*" -> "x"; "/" -> "/"; else -> op
        }
        tvOperacion.text = formatearNumero(primerNumero) + " " + simbolo
    }

    private fun calcularResultado() {
        val segundoNumero = tvResultado.text.toString().toDoubleOrNull() ?: return

        val resultado: Double = when (operacion) {
            "+" -> primerNumero + segundoNumero
            "-" -> primerNumero - segundoNumero
            "*" -> primerNumero * segundoNumero
            "/" -> {
                if (segundoNumero != 0.0) primerNumero / segundoNumero
                else {
                    tvResultado.text = "Error"
                    tvOperacion.text = ""
                    nuevaOperacion = true
                    return
                }
            }
            else -> return
        }

        val simbolo = when (operacion) {
            "+" -> "+"; "-" -> "-"; "*" -> "x"; "/" -> "/"; else -> operacion
        }
        tvOperacion.text = "${formatearNumero(primerNumero)} $simbolo ${formatearNumero(segundoNumero)} ="
        tvResultado.text = formatearNumero(resultado)
        ajustarTamanoTexto(tvResultado.text.length)
        nuevaOperacion = true
    }

    private fun formatearNumero(numero: Double): String {
        return if (numero == floor(numero) && !numero.isInfinite() && abs(numero) < 1_000_000_000) {
            numero.toLong().toString()
        } else {
            numero.toBigDecimal().stripTrailingZeros().toPlainString()
        }
    }
}