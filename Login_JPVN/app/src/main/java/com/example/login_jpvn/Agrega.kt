package com.example.login_jpvn

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class Agrega : AppCompatActivity() {

    // Referencia a Firebase apuntando al nodo "peliculas"
    val database = FirebaseDatabase.getInstance("https://login-a778c-default-rtdb.firebaseio.com/")
    val myRef = database.getReference("peliculas")

    override fun onCreate(savedInstanceState: Bundle?) {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agrega)

        // Referencias a los campos del layout
        val editNombre = findViewById<EditText>(R.id.editNombre)
        val editGenero = findViewById<EditText>(R.id.editGenero)
        val editAnio = findViewById<EditText>(R.id.editAnio)
        val btnAgregar = findViewById<Button>(R.id.btnAgregar)

        btnAgregar.setOnClickListener {

            // Validamos que los campos no estén vacíos
            if (editNombre.text.isEmpty() || editGenero.text.isEmpty() || editAnio.text.isEmpty()) {
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Usamos HashMap para controlar exactamente los nombres de los campos
            // así coinciden con los nombres en mayúscula que tenemos en Firebase
            val peliEjemplo = hashMapOf(
                "Nombre" to editNombre.text.toString(),
                "Genero" to editGenero.text.toString(),
                "Anio" to editAnio.text.toString()
            )

            // push() genera una clave única automática en Firebase
            // setValue() guarda el objeto en ese nuevo nodo
            myRef.push().setValue(peliEjemplo)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Película agregada", Toast.LENGTH_SHORT).show()
                        // Regresamos a ListaPrincipal después de agregar
                        startActivity(Intent(this, ListaPrincipal::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Error al agregar", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}