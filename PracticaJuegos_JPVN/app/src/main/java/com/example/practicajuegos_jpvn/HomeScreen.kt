package com.example.practicajuegos_jpvn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun Home(navController: NavController, paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))
                )
            )
    ) {
        Column(
            modifier            = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text  = "🎮",
                fontSize = 72.sp
            )
            Text(
                text  = "Selecciona un juego",
                style = TextStyle(
                    color      = Color.White,
                    fontSize   = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            // Botón Buscaminas
            Button(
                onClick  = { navController.navigate(Screen.Buscaminas.route) },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4FC3F7)
                )
            ) {
                Text(
                    text  = "💣  Buscaminas",
                    style = TextStyle(
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color(0xFF1A1A2E)
                    )
                )
            }

            // Botón Encuentra el Topo
            Button(
                onClick  = { navController.navigate(Screen.EncuentraTopo.route) },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF52B788)
                )
            ) {
                Text(
                    text  = "🐭  Encuentra el Topo",
                    style = TextStyle(
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White
                    )
                )
            }
        }
    }
}