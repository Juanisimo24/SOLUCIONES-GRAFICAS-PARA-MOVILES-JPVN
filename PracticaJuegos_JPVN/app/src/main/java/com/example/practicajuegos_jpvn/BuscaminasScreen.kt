package com.example.practicajuegos_jpvn

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.Random

fun asignaMina(): Boolean {
    val numeroRandom = Random().nextInt(10)
    return (numeroRandom > 7)
}

fun contarMinasContiguas(
    fila: Int,
    columna: Int,
    filas: Int,
    columnas: Int,
    minas: List<MutableState<Boolean>>
): Int {
    var contador = 0
    for (df in -1..1) {
        for (dc in -1..1) {
            if (df == 0 && dc == 0) continue
            val filaVecina = fila + df
            val colVecina  = columna + dc
            if (filaVecina in 0 until filas && colVecina in 0 until columnas) {
                val indexVecino = filaVecina * columnas + colVecina
                if (minas[indexVecino].value) contador++
            }
        }
    }
    return contador
}

@Composable
fun Buscaminas(navController: NavController, paddingValues: PaddingValues)  {

    val columnas = 6
    val filas    = 10

    var estadoBotones = remember { List(filas * columnas) { mutableStateOf(true) } }
    var minas         = remember { List(filas * columnas) { mutableStateOf(asignaMina()) } }
    var verAlertaDerrota  = remember { mutableStateOf(false) }
    var verAlertaVictoria = remember { mutableStateOf(false) }
    var segurosConsecutivos = remember { mutableStateOf(0) }
    val botonesParaGanar = 10

    fun reiniciarJuego() {
        estadoBotones.forEach { it.value = true }
        minas.forEach       { it.value = asignaMina() }
        verAlertaDerrota.value  = false
        verAlertaVictoria.value = false
        segurosConsecutivos.value = 0
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F3460)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
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

            for (i in 0 until filas) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 2.dp)
                ) {
                    for (j in 0 until columnas) {
                        val index = i * columnas + j
                        val minasAlrededor = contarMinasContiguas(i, j, filas, columnas, minas)

                        val textoCelda: String
                        val colorTexto: Color

                        if (!estadoBotones[index].value) {
                            if (minas[index].value) {
                                textoCelda = "💣"; colorTexto = Color.Red
                            } else if (minasAlrededor > 0) {
                                textoCelda = minasAlrededor.toString()
                                colorTexto = when (minasAlrededor) {
                                    1    -> Color(0xFF4FC3F7)
                                    2    -> Color(0xFF81C784)
                                    3    -> Color(0xFFFFB74D)
                                    else -> Color(0xFFE57373)
                                }
                            } else {
                                textoCelda = " "; colorTexto = Color.White
                            }
                        } else {
                            textoCelda = "🟦"; colorTexto = Color.White
                        }

                        Button(
                            onClick = {
                                if (estadoBotones[index].value) {
                                    estadoBotones[index].value = false
                                    if (minas[index].value) {
                                        segurosConsecutivos.value = 0
                                        verAlertaDerrota.value = true
                                    } else {
                                        segurosConsecutivos.value++
                                        if (segurosConsecutivos.value >= botonesParaGanar) {
                                            verAlertaVictoria.value = true
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .padding(2.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (estadoBotones[index].value)
                                    Color(0xFF16213E) else Color(0xFF0D0D1A),
                                disabledContainerColor = Color(0xFF0D0D1A)
                            ),
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

        if (verAlertaDerrota.value) {
            AlertDialog(
                onDismissRequest = { verAlertaDerrota.value = false },
                title = {
                    Text("💥 ¡Perdiste!", style = TextStyle(
                        color = Color(0xFFE57373), fontSize = 22.sp, fontWeight = FontWeight.Bold
                    ))
                },
                text = {
                    Text("Encontraste una mina. Llevabas ${segurosConsecutivos.value} celdas seguras.",
                        style = TextStyle(color = Color.White))
                },
                confirmButton = {
                    Button(onClick = { reiniciarJuego() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))) {
                        Text("🔄 Reiniciar", color = Color.White)
                    }
                },
                containerColor = Color(0xFF1A1A2E)
            )
        }

        if (verAlertaVictoria.value) {
            AlertDialog(
                onDismissRequest = { verAlertaVictoria.value = false },
                title = {
                    Text("🏆 ¡Ganaste!", style = TextStyle(
                        color = Color(0xFF4ECDC4), fontSize = 22.sp, fontWeight = FontWeight.Bold
                    ))
                },
                text = {
                    Text("¡Increíble! Destapaste $botonesParaGanar celdas seguras consecutivas.",
                        style = TextStyle(color = Color.White))
                },
                confirmButton = {
                    Button(onClick = { reiniciarJuego() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4ECDC4))) {
                        Text("🎮 Jugar de nuevo", color = Color(0xFF1A1A2E))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { verAlertaVictoria.value = false }) {
                        Text("Seguir jugando", color = Color.White.copy(alpha = 0.7f))
                    }
                },
                containerColor = Color(0xFF1A1A2E)
            )
        }
    }
}