package com.example.practicajuegos_jpvn

sealed class Screen(val route: String) {
    object Home : Screen(route = "home")
    object Buscaminas : Screen(route = "buscaminas")
    object EncuentraTopo : Screen(route = "encuentraTopo")
}