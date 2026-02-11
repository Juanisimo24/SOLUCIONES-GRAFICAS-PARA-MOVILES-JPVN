package com.example.calculadorajpvn

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // --- ZONA DE VARIABLES (MEMORIA) ---
    // Las declaro aquí arriba para que TODAS las funciones puedan verlas y usarlas.

    // 1. La Pantalla: 'lateinit' significa "Te prometo que la inicializo más tarde en el onCreate".
    private lateinit var tvResultado: TextView

    // 2. Memoria de números:
    private var primerNumero: Double = 0.0  // Aquí guardo el 1er número (ej: el 5 en "5 + 3")
    private var operacion: String = ""      // Aquí guardo qué botón apreté ("+", "-", etc)

    // 3. Bandera de control:
    // Sirve para saber si debo borrar la pantalla al escribir un nuevo número.
    // True = "La pantalla muestra un resultado viejo, bórralo si escribo algo".
    private var nuevaOperacion: Boolean = true

    // --- ZONA DE INICIO (DONDE ARRANCA LA APP) ---
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // PASO 1: Conectar el cableado
        // Busco la pantalla en el diseño (XML) y la guardo en mi variable.
        tvResultado = findViewById(R.id.tvResultado)

        // PASO 2: Configurar los botones
        // Llamo a esta función que creé abajo para no llenar de código el onCreate.
        inicializarBotones()
    }

    // --- ZONA DE FUNCIONES (MIS HERRAMIENTAS) ---

    // Función para conectar y dar vida a todos los botones
    private fun inicializarBotones() {

        // GRUPO A: Los Números
        // Configuro qué pasa cuando tocas el 0, 1, 2...
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

        // GRUPO B: Las Operaciones
        // Cuando tocas +, -, * o /, guardo la operación en memoria
        findViewById<Button>(R.id.btnMas).setOnClickListener { guardarOperacion("+") }
        findViewById<Button>(R.id.btnMenos).setOnClickListener { guardarOperacion("-") }
        findViewById<Button>(R.id.btnMul).setOnClickListener { guardarOperacion("*") }
        findViewById<Button>(R.id.btnDiv).setOnClickListener { guardarOperacion("/") }

        // GRUPO C: Acciones Especiales

        // Botón IGUAL (=): Aquí ocurre la magia matemática
        findViewById<Button>(R.id.btnIgual).setOnClickListener {
            calcularResultado()
        }

        // Botón C (Clear): Reinicia todo a cero
        findViewById<Button>(R.id.btnC).setOnClickListener {
            tvResultado.text = "0"
            primerNumero = 0.0
            operacion = ""
            nuevaOperacion = true
        }

        // Botón BORRAR (DEL): Borra solo el último numerito
        findViewById<Button>(R.id.btnBorrar).setOnClickListener {
            val textoActual = tvResultado.text.toString()
            if (textoActual.length > 1) {
                // Si hay varios números, quito el último
                tvResultado.text = textoActual.dropLast(1)
            } else {
                // Si solo queda un número, pongo cero (no dejo la pantalla vacía)
                tvResultado.text = "0"
                nuevaOperacion = true
            }
        }
    }

    // LÓGICA 1: Escribir en pantalla
    private fun escribirNumero(numero: String) {
        // Si acabo de hacer una suma (nuevaOperacion = true), borro el resultado anterior.
        if (nuevaOperacion) {
            tvResultado.text = ""
            nuevaOperacion = false
        }

        // Validación anti-error: Si ya hay un punto, no dejo poner otro.
        if (numero == "." && tvResultado.text.contains(".")) {
            return // "Return" significa: detente aquí, no hagas nada más.
        }

        // "Append" significa pegar al final (ej: tengo "1", pego "2" -> "12")
        tvResultado.append(numero)
    }

    // LÓGICA 2: Preparar la operación
    private fun guardarOperacion(op: String) {
        // Guardo el número que está en pantalla en la variable 'primerNumero'
        primerNumero = tvResultado.text.toString().toDouble()
        // Guardo qué operación quiere hacer el usuario (+, -, *, /)
        operacion = op
        // Aviso que lo siguiente que escriban será un número nuevo
        nuevaOperacion = true
    }

    // LÓGICA 3: Calcular
    private fun calcularResultado() {
        // Tomo el número que está AHORA en pantalla (el segundo número)
        val segundoNumero = tvResultado.text.toString().toDouble()
        var resultado = 0.0

        // Reviso qué operación tenía guardada y calculo
        if (operacion == "+") {
            resultado = primerNumero + segundoNumero
        } else if (operacion == "-") {
            resultado = primerNumero - segundoNumero
        } else if (operacion == "*") {
            resultado = primerNumero * segundoNumero
        } else if (operacion == "/") {
            // Validación simple: no se puede dividir entre 0
            if (segundoNumero != 0.0) {
                resultado = primerNumero / segundoNumero
            } else {
                resultado = 0.0 // O podrías poner un mensaje de error
            }
        }

        // Muestro el resultado final
        tvResultado.text = resultado.toString()
        // Preparo todo para empezar de nuevo si el usuario escribe otro número
        nuevaOperacion = true
    }
}