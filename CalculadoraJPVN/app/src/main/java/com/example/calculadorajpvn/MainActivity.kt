package com.example.calculadorajpvn

import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import kotlin.math.abs
import kotlin.math.floor

class MainActivity : AppCompatActivity() {

    private lateinit var tvResultado: TextView
    private lateinit var tvOperacion: TextView

    private var primerNumero: Double = 0.0
    private var operacion: String = ""
    private var nuevaOperacion: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hace que el layout respete los bordes del sistema (status bar, nav bar)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        setContentView(R.layout.activity_main)

        tvResultado = findViewById(R.id.tvResultado)
        tvOperacion = findViewById(R.id.tvOperacion)

        inicializarBotones()
    }

    private fun inicializarBotones() {

        // Numeros
        findViewById<Button>(R.id.btn0).setOnClickListener { escribirNumero("0") }
        findViewById<Button>(R.id.btn1).setOnClickListener { escribirNumero("1") }
        findViewById<Button>(R.id.btn2).setOnClickListener { escribirNumero("2") }
        findViewById<Button>(R.id.btn3).setOnClickListener { escribirNumero("3") }
        findViewById<Button>(R.id.btn4).setOnClickListener { escribirNumero("4") }
        findViewById<Button>(R.id.btn5).setOnClickListener { escribirNumero("5") }
        findViewById<Button>(R.id.btn6).setOnClickListener { escribirNumero("6") }
        findViewById<Button>(R.id.btn7).setOnClickListener { escribirNumero("7") }
        findViewById<Button>(R.id.btn8).setOnClickListener { escribirNumero("8") }
        findViewById<Button>(R.id.btn9).setOnClickListener { escribirNumero("9") }
        findViewById<Button>(R.id.btnPunto).setOnClickListener { escribirNumero(".") }

        // Operaciones
        findViewById<Button>(R.id.btnMas).setOnClickListener { guardarOperacion("+") }
        findViewById<Button>(R.id.btnMenos).setOnClickListener { guardarOperacion("-") }
        findViewById<Button>(R.id.btnMul).setOnClickListener { guardarOperacion("*") }
        findViewById<Button>(R.id.btnDiv).setOnClickListener { guardarOperacion("/") }

        // Igual
        findViewById<Button>(R.id.btnIgual).setOnClickListener { calcularResultado() }

        // C - Limpiar todo
        findViewById<Button>(R.id.btnC).setOnClickListener {
            tvResultado.text = "0"
            tvOperacion.text = ""
            primerNumero = 0.0
            operacion = ""
            nuevaOperacion = true
        }

        // DEL - Borrar ultimo digito
        findViewById<Button>(R.id.btnBorrar).setOnClickListener {
            val textoActual = tvResultado.text.toString()
            if (textoActual.length > 1) {
                tvResultado.text = textoActual.dropLast(1)
            } else {
                tvResultado.text = "0"
                nuevaOperacion = true
            }
        }

        // +/- Cambiar signo
        findViewById<Button>(R.id.btnPlusMinus).setOnClickListener {
            val actual = tvResultado.text.toString().toDoubleOrNull() ?: return@setOnClickListener
            tvResultado.text = formatearNumero(actual * -1)
        }

        // % Porcentaje
        findViewById<Button>(R.id.btnPorcentaje).setOnClickListener {
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
            "+" -> "+"
            "-" -> "-"
            "*" -> "x"
            "/" -> "/"
            else -> op
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
                if (segundoNumero != 0.0) {
                    primerNumero / segundoNumero
                } else {
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
        tvOperacion.text = formatearNumero(primerNumero) + " " + simbolo + " " + formatearNumero(segundoNumero) + " ="
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