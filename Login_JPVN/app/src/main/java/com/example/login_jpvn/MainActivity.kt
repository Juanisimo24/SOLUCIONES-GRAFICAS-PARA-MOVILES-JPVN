package com.example.login_jpvn

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

// CREDENCIALES DE PRUEBA PARA EVALUACIÓN:
// Usuario: tiburoncin@gmail.com
// Contraseña: 123456

class MainActivity : AppCompatActivity() {

    // FirebaseAuth es el objeto principal que maneja toda la autenticación.
    // lateinit significa que se inicializará después, no al momento de declarar.
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtenemos la instancia de Firebase Auth.
        auth = FirebaseAuth.getInstance()

        // Obtenemos referencia al botón login del layout por su id
        val login = findViewById<Button>(R.id.login)

        // Asignamos el evento click al botón login
        login.setOnClickListener {

            // Leemos lo que el usuario escribió en los campos
            val correo = findViewById<EditText>(R.id.correo).text.toString().trim()
            val password = findViewById<EditText>(R.id.password).text.toString().trim()

            // Validación: verificamos que los campos no estén vacíos
            if (correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // signInWithEmailAndPassword verifica el correo y contraseña en Firebase
            auth.signInWithEmailAndPassword(correo, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Usuario Autenticado", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, ListaPrincipal::class.java)
                        intent.putExtra("nombre", correo)
                        startActivity(intent)
                        finish()
                    } else {
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

    // Revisa si ya hay sesión activa, si sí va directo a ListaPrincipal
    private fun revisaUsuario(usuario: FirebaseUser?) {
        if (usuario == null) {
            Toast.makeText(this, "No hay usuarios Autenticados", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Sesión activa", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ListaPrincipal::class.java)
            intent.putExtra("nombre", usuario.email)
            startActivity(intent)
            finish()
        }
    }
}