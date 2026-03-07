package com.example.login_jpvn

// Clase modelo que representa una película.
// Cada propiedad corresponde a un campo en Firebase.
// El id guarda la clave del nodo en Firebase (ej: "1", "-Mw2qn...")
class Peliculas(nom: String?, ani: String?, gen: String?, ids: String?) {

    var nombre: String? = nom
    var anio: String? = ani
    var genero: String? = gen
    var id: String? = ids
}