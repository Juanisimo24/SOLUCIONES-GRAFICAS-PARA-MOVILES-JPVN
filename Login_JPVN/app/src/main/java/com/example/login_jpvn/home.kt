package com.example.login_jpvn

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Referencias a los elementos del layout
        val regresaHome = findViewById<Button>(R.id.regresa)
        val parametros = findViewById<TextView>(R.id.parametros)

        // Recibimos los datos que nos mandó MainActivity con putExtra.
        // intent.extras contiene todos los datos enviados.
        // getCharSequence("nombre") obtiene el valor con clave "nombre".
        val extras = intent.extras
        parametros.text = parametros.text.toString() + " " + extras?.getCharSequence("nombre").toString()

        // Al presionar el botón regresamos al Login
        regresaHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Cerramos Home para que no quede en la pila
        }

        // Inicializamos Firebase Auth para poder cerrar sesión
        val auth = FirebaseAuth.getInstance()
        val cerrarSesion = findViewById<Button>(R.id.cerrarSesion)
        cerrarSesion.setOnClickListener {
            // signOut() cierra la sesión en Firebase
            // Después de esto auth.currentUser será null
            auth.signOut()
            // Estas flags limpian la pila y crean una nueva tarea con el Login
            // así no cierra la app sino que regresa al Login correctamente
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}