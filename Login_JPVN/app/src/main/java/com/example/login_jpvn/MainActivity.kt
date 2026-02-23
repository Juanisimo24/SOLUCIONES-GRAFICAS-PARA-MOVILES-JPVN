package com.example.login_jpvn

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.content.Intent

// CREDENCIALES DE PRUEBA PARA EVALUACIÓN:
// Usuario: tiburoncin@gmail.com
// Contraseña: 123456
class MainActivity : AppCompatActivity() {

    // FirebaseAuth es el objeto que maneja toda la autenticación.
    // lateinit significa que lo inicializamos después, no al declarar.
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializamos Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Referencia al botón login del layout
        val login = findViewById<Button>(R.id.login)

        login.setOnClickListener {
            // Leemos lo que el usuario escribió en los campos
            val correo = findViewById<EditText>(R.id.correo).text.toString()
            val password = findViewById<EditText>(R.id.password).text.toString()

            // signInWithEmailAndPassword: le manda el correo y contraseña a Firebase
            // para verificar si ese usuario existe y la contraseña es correcta
            auth.signInWithEmailAndPassword(correo, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Usuario Autenticado", Toast.LENGTH_LONG).show()
                        // Creamos el Intent para ir a home
                        // putExtra("nombre", correo) manda el correo como parámetro
                        // que será recibido en home.kt con intent.extras
                        val intent = Intent(this, home::class.java)
                        intent.putExtra("nombre", correo)
                        startActivity(intent)
                    } else {
                        // Muestra el error de Firebase si algo salió mal
                        Toast.makeText(this, task.exception?.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    // onStart se ejecuta cada vez que la Activity se vuelve visible
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        revisaUsuario(currentUser)
    }

    // Revisa si ya hay una sesión activa o no
    private fun revisaUsuario(usuario: FirebaseUser?) {
        if (usuario == null) {
            // No hay sesión, el usuario debe hacer login
            Toast.makeText(this, "No hay usuarios Autenticados", Toast.LENGTH_SHORT).show()
        } else {
            // Ya hay sesión activa, mandamos directo a Home
            // sin necesidad de volver a escribir usuario y contraseña
            Toast.makeText(this, "Sesión activa", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, home::class.java)
            intent.putExtra("nombre", usuario.email)
            startActivity(intent)
            finish() // Cerramos Login para que no quede en la pila
        }
    }
}