package com.example.practicajuegos_jpvn

import androidx.compose.animation.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

const val DURACION_JUEGO = 15
const val TIEMPO_VISIBLE = 500L

@Composable
fun EncuentraTopo(navController: NavController, paddingValues: PaddingValues) {

    var juegoIniciado     by remember { mutableStateOf(false) }
    var juegoTerminado    by remember { mutableStateOf(false) }
    var puntaje           by remember { mutableStateOf(0) }
    var segundosRestantes by remember { mutableStateOf(DURACION_JUEGO) }
    var posX              by remember { mutableStateOf(0.5f) }
    var posY              by remember { mutableStateOf(0.5f) }
    var topoVisible       by remember { mutableStateOf(false) }

    LaunchedEffect(juegoIniciado) {
        if (!juegoIniciado) return@LaunchedEffect

        puntaje = 0
        segundosRestantes = DURACION_JUEGO

        coroutineScope {
            // Corrutina 1: contador regresivo
            launch {
                while (segundosRestantes > 0) {
                    delay(1000L)
                    segundosRestantes--
                }
                topoVisible    = false
                juegoTerminado = true
                juegoIniciado  = false
            }

            // Corrutina 2: movimiento del topo
            launch {
                while (juegoIniciado) {
                    posX = Random.nextFloat() * 0.75f + 0.05f
                    posY = Random.nextFloat() * 0.70f + 0.05f
                    topoVisible = true
                    delay(TIEMPO_VISIBLE)
                    topoVisible = false
                    delay(200L)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1B4332),
                        Color(0xFF1B4332),
                        Color(0xFF2D6A4F),
                        Color(0xFF1B4332)
                    )
                )
            )
    ) {

        // Área del juego donde aparece el topo
        if (juegoIniciado) {
            BoxWithConstraints(modifier = Modifier
                .fillMaxSize()
                .padding(top = 110.dp)) {
                val anchoTotal = this.maxWidth.value
                val altoTotal  = this.maxHeight.value

                AnimatedVisibility(
                    visible  = topoVisible,
                    enter    = scaleIn() + fadeIn(),
                    exit     = scaleOut() + fadeOut(),
                    modifier = Modifier.offset(
                        x = (posX * anchoTotal * 0.85f).dp,
                        y = (posY * altoTotal  * 0.65f).dp
                    )
                ) {
                    Button(
                        onClick = {
                            if (topoVisible) {
                                puntaje++
                                topoVisible = false
                            }
                        },
                        modifier       = Modifier.size(72.dp),
                        shape          = RoundedCornerShape(50),
                        colors         = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF95D5B2)
                        ),
                        contentPadding = PaddingValues(0.dp),
                        elevation      = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text(text = "🐭", fontSize = 36.sp)
                    }
                }
            }
        }

        // Panel superior: puntaje y tiempo
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(
                    containerColor = Color(0xFF1B4332).copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🐭 Atrapes",
                            style = TextStyle(color = Color(0xFF95D5B2), fontSize = 13.sp))
                        Text("$puntaje",
                            style = TextStyle(
                                color      = Color.White,
                                fontSize   = 32.sp,
                                fontWeight = FontWeight.Bold
                            ))
                    }
                    HorizontalDivider(
                        modifier  = Modifier
                            .height(48.dp)
                            .width(1.dp),
                        color     = Color.White.copy(alpha = 0.3f)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⏱ Tiempo",
                            style = TextStyle(color = Color(0xFF95D5B2), fontSize = 13.sp))
                        Text("${segundosRestantes}s",
                            style = TextStyle(
                                color      = if (segundosRestantes <= 5)
                                    Color(0xFFFFB74D) else Color.White,
                                fontSize   = 32.sp,
                                fontWeight = FontWeight.Bold
                            ))
                    }
                }
            }
        }

        // Pantalla de inicio
        if (!juegoIniciado && !juegoTerminado) {
            Column(
                modifier            = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "🐭", fontSize = 80.sp)
                Text(
                    text  = "Encuentra el Topo",
                    style = TextStyle(
                        color      = Color.White,
                        fontSize   = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "El topo aparece por ${TIEMPO_VISIBLE}ms.\n" +
                            "¡Tócalo antes de que se esconda!\n" +
                            "Tienes $DURACION_JUEGO segundos.",
                    style     = TextStyle(
                        color    = Color.White.copy(alpha = 0.7f),
                        fontSize = 15.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick  = { juegoIniciado = true; juegoTerminado = false },
                    colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF52B788)),
                    shape    = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("▶  Iniciar juego", fontSize = 18.sp, color = Color.White)
                }
            }
        }

        // Diálogo de fin de juego
        if (juegoTerminado) {
            AlertDialog(
                onDismissRequest = {},
                title = {
                    Text("⏰ ¡Tiempo!",
                        style = TextStyle(
                            color      = Color(0xFF95D5B2),
                            fontSize   = 22.sp,
                            fontWeight = FontWeight.Bold
                        ))
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Lograste atrapar al topo:",
                            style = TextStyle(color = Color.White, fontSize = 15.sp))
                        Text("$puntaje veces 🐭",
                            style = TextStyle(
                                color      = Color(0xFF95D5B2),
                                fontSize   = 28.sp,
                                fontWeight = FontWeight.Bold
                            ))
                        Text(
                            text = when {
                                puntaje >= 10 -> "🏆 ¡Eres un maestro!"
                                puntaje >= 6  -> "👍 ¡Muy buen reflejo!"
                                puntaje >= 3  -> "😅 Puedes mejorar."
                                else          -> "🐢 El topo fue más rápido..."
                            },
                            style = TextStyle(
                                color    = Color.White.copy(alpha = 0.8f),
                                fontSize = 15.sp
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { juegoTerminado = false },
                        colors  = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF52B788)
                        )
                    ) {
                        Text("🔄 Jugar de nuevo", color = Color.White)
                    }
                },
                containerColor = Color(0xFF1B4332)
            )
        }
    }
}