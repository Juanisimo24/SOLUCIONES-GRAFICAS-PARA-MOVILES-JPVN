package com.example.login_jpvn

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class Detalle : AppCompatActivity() {

    // Referencia a Firebase apuntando al nodo "peliculas"
    val database = FirebaseDatabase.getInstance("https://login-a778c-default-rtdb.firebaseio.com/")
    val myRef = database.getReference("peliculas")

    override fun onCreate(savedInstanceState: Bundle?) {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle)

        // Referencias a los campos del layout
        val editNombre = findViewById<EditText>(R.id.editNombre)
        val editGenero = findViewById<EditText>(R.id.editGenero)
        val editAnio = findViewById<EditText>(R.id.editAnio)
        val btnEditar = findViewById<Button>(R.id.btnEditar)
        val btnEliminar = findViewById<Button>(R.id.btnEliminar)

        // Recibimos los datos de la película seleccionada en ListaPrincipal
        // con intent.extras obtenemos todos los extras enviados con putExtra
        val key = intent.extras?.getString("key")
        val nombre = intent.extras?.getString("Nombre")
        val genero = intent.extras?.getString("Genero")
        val anio = intent.extras?.getString("Anio")

        // Llenamos los campos con los datos actuales de la película
        editNombre.setText(nombre)
        editGenero.setText(genero)
        editAnio.setText(anio)

        // Botón para editar: actualiza todos los campos del nodo en Firebase
        btnEditar.setOnClickListener {
            // Usamos HashMap para controlar exactamente los nombres de los campos
            // así coinciden con los nombres en mayúscula que tenemos en Firebase
            val peliEjemplo = hashMapOf(
                "Nombre" to editNombre.text.toString(),
                "Genero" to editGenero.text.toString(),
                "Anio" to editAnio.text.toString()
            )

            myRef.child(key!!).setValue(peliEjemplo)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Película actualizada", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, ListaPrincipal::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Botón para eliminar: borra el nodo completo de Firebase
        btnEliminar.setOnClickListener {
            // removeValue() elimina toda la información del nodo seleccionado
            myRef.child(key!!).removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Película eliminada", Toast.LENGTH_SHORT).show()
                        // Regresamos a ListaPrincipal después de eliminar
                        startActivity(Intent(this, ListaPrincipal::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}