package com.example.practicajuegos_jpvn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.practicajuegos_jpvn.ui.theme.PracticaJuegos_JPVNTheme
import androidx.compose.foundation.layout.statusBarsPadding


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PracticaJuegos_JPVNTheme {

                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val canNavigateBack =
                    navBackStackEntry?.destination?.route != Screen.Home.route

                Scaffold(
                    topBar = {
                        if (canNavigateBack) {
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()) {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Regresar",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    },
                ) { innerPadding ->
                    NavHost(
                        navController    = navController,
                        startDestination = Screen.Home.route,
                        modifier         = Modifier
                    ) {
                        composable(route = Screen.Home.route) {
                            Home(
                                navController  = navController,
                                paddingValues  = innerPadding
                            )
                        }
                        composable(route = Screen.Buscaminas.route) {
                            Buscaminas(navController = navController, paddingValues = innerPadding)
                        }
                        composable(route = Screen.EncuentraTopo.route) {
                            EncuentraTopo(navController = navController, paddingValues = innerPadding)
                        }
                    }
                }
            }
        }
    }
}