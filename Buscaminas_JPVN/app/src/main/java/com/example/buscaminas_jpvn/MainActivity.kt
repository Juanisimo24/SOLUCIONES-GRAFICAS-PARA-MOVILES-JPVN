package com.example.buscaminas_jpvn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buscaminas_jpvn.ui.theme.Buscaminas_JPVNTheme
import java.util.Random

// ─────────────────────────────────────────────────────────────────────────────
// ACTIVIDAD PRINCIPAL
// ComponentActivity es la clase base para actividades que usan Jetpack Compose
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hace que la pantalla se extienda hasta los bordes (bajo la barra de estado)
        enableEdgeToEdge()

        // setContent define la UI completa usando Jetpack Compose
        setContent {
            Buscaminas_JPVNTheme {
                // Scaffold es la estructura base de Material Design:
                // nos da topBar, contenido y más de forma organizada
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        // Barra superior con título del juego y estilo personalizado
                        TopAppBar(
                            title = {
                                Text(
                                    text = "💣 Buscaminas",
                                    style = TextStyle(
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                // Color de fondo oscuro para la barra
                                containerColor = Color(0xFF1A1A2E)
                            )
                        )
                    }
                ) { innerPadding ->
                    // innerPadding lo da el Scaffold para que el contenido
                    // no quede tapado por la topBar
                    Tablero(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// FUNCIÓN HELPER: ASIGNA MINA
// Esta función decide si una celda tendrá mina de forma aleatoria.
// Se llama FUERA del composable porque no necesita estado de la UI.
// Genera un número del 0 al 9; si es mayor que 7, hay mina (30% de probabilidad)
// ─────────────────────────────────────────────────────────────────────────────
fun asignaMina(): Boolean {
    val numeroRandom = Random().nextInt(10)
    return (numeroRandom > 7)
}

// ─────────────────────────────────────────────────────────────────────────────
// FUNCIÓN HELPER: CUENTA MINAS CONTIGUAS  ← PUNTO EXTRA
// Recibe la posición (fila, columna) de una celda y la lista de minas,
// y cuenta cuántas de las 8 celdas vecinas tienen mina.
// Esto le dice al jugador cuántas minas hay alrededor de la celda destapada.
// ─────────────────────────────────────────────────────────────────────────────
fun contarMinasContiguas(
    fila: Int,
    columna: Int,
    filas: Int,
    columnas: Int,
    minas: List<MutableState<Boolean>>
): Int {
    var contador = 0

    // Revisamos las 8 posiciones alrededor de la celda (arriba, abajo, izq, der y diagonales)
    for (df in -1..1) {          // df = delta fila: -1 (arriba), 0 (misma), 1 (abajo)
        for (dc in -1..1) {      // dc = delta col:  -1 (izq),   0 (misma), 1 (der)

            // Saltamos la celda central (ella misma)
            if (df == 0 && dc == 0) continue

            val filaVecina = fila + df
            val colVecina  = columna + dc

            // Verificamos que la celda vecina esté dentro del tablero
            // (evitamos índices fuera de rango)
            if (filaVecina in 0 until filas && colVecina in 0 until columnas) {
                val indexVecino = filaVecina * columnas + colVecina

                // Si la celda vecina tiene mina, sumamos 1 al contador
                if (minas[indexVecino].value) {
                    contador++
                }
            }
        }
    }
    return contador
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPOSABLE PRINCIPAL: TABLERO
// @Composable indica que esta función describe parte de la UI de Jetpack Compose.
// Recibe un modifier para que el padre controle su tamaño/posición.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun Tablero(modifier: Modifier = Modifier) {

    // ── DIMENSIONES DEL TABLERO ───────────────────────────────────────────────
    val columnas = 6
    val filas    = 10

    // ── ESTADOS REACTIVOS ─────────────────────────────────────────────────────
    // "remember" conserva el valor entre recomposiciones (re-renders de la UI).
    // "mutableStateOf" hace que cuando el valor cambie, la UI se actualice sola.

    // estadoBotones: true = botón activo (no presionado), false = ya presionado
    var estadoBotones = remember {
        List(filas * columnas) { mutableStateOf(true) }
    }

    // minas: true = esa celda tiene mina, false = celda segura
    var minas = remember {
        List(filas * columnas) { mutableStateOf(asignaMina()) }
    }

    // verAlertaDerrota: controla si mostramos el diálogo de "Perdiste"
    var verAlertaDerrota = remember { mutableStateOf(false) }

    // verAlertaVictoria: controla si mostramos el diálogo de "Ganaste" ← NUEVO
    var verAlertaVictoria = remember { mutableStateOf(false) }

    // segurosConsecutivos: cuenta cuántas celdas seguras lleva el jugador
    // destapando SIN tocar una mina. Se reinicia si toca una mina. ← NUEVO
    var segurosConsecutivos = remember { mutableStateOf(0) }

    // Número de botones ganadores necesarios para ganar
    val botonesParaGanar = 10

    // ── FUNCIÓN DE REINICIO ────────────────────────────────────────────────────
    // La definimos aquí para reutilizarla en ambos diálogos (derrota y victoria)
    fun reiniciarJuego() {
        // Reactivamos todos los botones
        estadoBotones.forEach { it.value = true }
        // Reasignamos minas aleatoriamente
        minas.forEach       { it.value = asignaMina() }
        // Ocultamos ambas alertas
        verAlertaDerrota.value  = false
        verAlertaVictoria.value = false
        // Reiniciamos el contador de seguros
        segurosConsecutivos.value = 0
    }

    // ── DISEÑO DEL TABLERO ────────────────────────────────────────────────────
    // Box nos permite apilar elementos (fondo + contenido encima)
    Box(
        modifier = modifier
            .background(
                // Fondo degradado de azul oscuro a morado oscuro (diseño mejorado)
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalArrangement   = Arrangement.Center,
            horizontalAlignment   = Alignment.CenterHorizontally
        ) {

            // ── MARCADOR: muestra los seguros consecutivos ─────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0F3460).copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text  = "✅ Seguros: ${segurosConsecutivos.value} / $botonesParaGanar",
                        style = TextStyle(
                            color      = Color(0xFF4ECDC4),
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text  = "💣 Destapa $botonesParaGanar para ganar",
                        style = TextStyle(
                            color    = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── CUADRÍCULA DEL TABLERO ─────────────────────────────────────────
            // Iteramos por cada FILA
            for (i in 0 until filas) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)         // Cada fila ocupa el mismo espacio vertical
                        .padding(vertical = 2.dp)
                ) {
                    // Iteramos por cada COLUMNA dentro de la fila
                    for (j in 0 until columnas) {

                        // Calculamos el índice lineal: fila * núm_columnas + columna
                        // Esto convierte (fila, columna) en un índice de la lista plana
                        val index = i * columnas + j

                        // ── PUNTO EXTRA: Minas contiguas ─────────────────────────
                        // Calculamos cuántas minas hay alrededor de ESTA celda
                        val minasAlrededor = contarMinasContiguas(
                            fila     = i,
                            columna  = j,
                            filas    = filas,
                            columnas = columnas,
                            minas    = minas
                        )

                        // Determinamos qué texto y color mostrar en el botón
                        // al ser presionado (destapado)
                        val textoCelda: String
                        val colorTexto: Color

                        if (!estadoBotones[index].value) {
                            // Botón ya fue presionado: mostrar info
                            if (minas[index].value) {
                                // Era mina → mostramos bomba
                                textoCelda = "💣"
                                colorTexto = Color.Red
                            } else if (minasAlrededor > 0) {
                                // No tiene mina pero hay minas vecinas → mostramos el número
                                textoCelda = minasAlrededor.toString()
                                // Colores por cantidad de minas (como en el juego clásico)
                                colorTexto = when (minasAlrededor) {
                                    1    -> Color(0xFF4FC3F7) // azul claro
                                    2    -> Color(0xFF81C784) // verde
                                    3    -> Color(0xFFFFB74D) // naranja
                                    else -> Color(0xFFE57373) // rojo claro
                                }
                            } else {
                                // Sin mina y sin vecinos con mina → zona despejada
                                textoCelda = " "
                                colorTexto = Color.White
                            }
                        } else {
                            // Botón aún no presionado → mostramos icono neutral
                            textoCelda = "🟦"
                            colorTexto = Color.White
                        }

                        // ── BOTÓN DE CELDA ────────────────────────────────────────
                        Button(
                            onClick = {
                                // Solo procesamos si el botón está activo (no presionado aún)
                                if (estadoBotones[index].value) {

                                    // Desactivamos el botón (lo marcamos como destapado)
                                    estadoBotones[index].value = false

                                    if (minas[index].value) {
                                        // ── DERROTA: encontró una mina ────────────────
                                        // Reiniciamos el contador de seguros consecutivos
                                        segurosConsecutivos.value = 0
                                        // Mostramos el diálogo de derrota
                                        verAlertaDerrota.value = true

                                    } else {
                                        // ── CELDA SEGURA: incrementamos el contador ───
                                        segurosConsecutivos.value++

                                        // ── VICTORIA: 10 seguros consecutivos ← NUEVO ─
                                        if (segurosConsecutivos.value >= botonesParaGanar) {
                                            verAlertaVictoria.value = true
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)      // Cada botón ocupa el mismo ancho
                                .fillMaxSize()
                                .padding(2.dp),  // Pequeño espacio entre botones
                            shape = RoundedCornerShape(6.dp),
                            // Color del botón según su estado
                            colors = ButtonDefaults.buttonColors(
                                containerColor  = if (estadoBotones[index].value)
                                    Color(0xFF16213E)   // Azul oscuro = sin destapar
                                else
                                    Color(0xFF0D0D1A),  // Negro = ya destapado
                                disabledContainerColor = Color(0xFF0D0D1A)
                            ),
                            // El botón sigue "enabled" siempre; controlamos el click manualmente
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = if (estadoBotones[index].value) 4.dp else 0.dp
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text  = textoCelda,
                                style = TextStyle(
                                    fontSize   = if (textoCelda.length == 1 && textoCelda != " ") 18.sp else 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = colorTexto
                                )
                            )
                        }
                    }
                }
            }
        }

        // ── DIÁLOGO DE DERROTA ─────────────────────────────────────────────────
        // AlertDialog es un componente de Material Design para mostrar alertas modales
        if (verAlertaDerrota.value) {
            AlertDialog(
                // Se llama cuando el usuario toca fuera del diálogo para cerrarlo
                onDismissRequest = { verAlertaDerrota.value = false },
                title = {
                    Text(
                        text  = "💥 ¡Perdiste!",
                        style = TextStyle(
                            color      = Color(0xFFE57373),
                            fontSize   = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                text = {
                    Text(
                        text  = "Encontraste una mina. Llevabas ${segurosConsecutivos.value} celdas seguras seguidas.",
                        style = TextStyle(color = Color.White)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { reiniciarJuego() },
                        colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))
                    ) {
                        Text(text = "🔄 Reiniciar", color = Color.White)
                    }
                },
                containerColor = Color(0xFF1A1A2E)
            )
        }

        // ── DIÁLOGO DE VICTORIA ← PUNTO PRINCIPAL ─────────────────────────────
        // Se muestra cuando el jugador destapa 10 celdas seguras consecutivas
        if (verAlertaVictoria.value) {
            AlertDialog(
                onDismissRequest = { verAlertaVictoria.value = false },
                title = {
                    Text(
                        text  = "🏆 ¡Ganaste!",
                        style = TextStyle(
                            color      = Color(0xFF4ECDC4),
                            fontSize   = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                text = {
                    Text(
                        text = "¡Increíble! Destapaste $botonesParaGanar celdas seguras " +
                                "consecutivas sin tocar ninguna mina. ¡Eres un experto!",
                        style = TextStyle(color = Color.White)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { reiniciarJuego() },
                        colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFF4ECDC4))
                    ) {
                        Text(text = "🎮 Jugar de nuevo", color = Color(0xFF1A1A2E))
                    }
                },
                dismissButton = {
                    // Botón secundario para seguir jugando sin reiniciar
                    TextButton(onClick = { verAlertaVictoria.value = false }) {
                        Text(text = "Seguir jugando", color = Color.White.copy(alpha = 0.7f))
                    }
                },
                containerColor = Color(0xFF1A1A2E)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PREVIEW
// @Preview permite ver la UI en el panel de diseño de Android Studio
// sin necesidad de ejecutar la app en un dispositivo.
// showBackground = true muestra el fondo para mejor visualización.
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun TableroPreview() {
    Buscaminas_JPVNTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text  = "💣 Buscaminas",
                            style = TextStyle(
                                fontSize   = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color      = Color.White
                            )
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1A1A2E)
                    )
                )
            }
        ) { innerPadding ->
            Tablero(modifier = Modifier.padding(innerPadding).fillMaxSize())
        }
    }
}