package com.example.login_jpvn

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListaPrincipal : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    // Referencia a la base de datos en Firebase, nodo "peliculas"
    val database = FirebaseDatabase.getInstance("https://login-a778c-default-rtdb.firebaseio.com/")
    val myRef = database.getReference("peliculas")

    // ArrayList que contendrá todas las películas leídas de Firebase
    lateinit var datos: ArrayList<Peliculas>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_principal)

        auth = FirebaseAuth.getInstance()

        // Conectamos el Toolbar como barra de acción de la Activity
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Botón flotante para ir a la pantalla de agregar película
        val botonAgregar = findViewById<FloatingActionButton>(R.id.botonAgregar)
        botonAgregar.setOnClickListener {
            startActivity(Intent(this, Agrega::class.java))
        }

        // Leemos los datos de Firebase en tiempo real.
        // addValueEventListener se ejecuta cada vez que los datos cambian.
        myRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // Reiniciamos la lista cada vez que hay cambios
                datos = ArrayList()

                // Debug: nos dice cuántas películas hay en Firebase
                println("Total de películas: ${snapshot.childrenCount}")

                // snapshot.children itera sobre cada nodo hijo (cada película)
                snapshot.children.forEach { hijo ->
                    println("Película encontrada: ${hijo.child("Nombre").value}")

                    // Creamos un objeto Peliculas con los datos de Firebase
                    // Los nombres deben coincidir EXACTAMENTE con Firebase (mayúsculas)
                    val pelicula = Peliculas(
                        hijo.child("Nombre").value.toString(),
                        hijo.child("Anio").value.toString(),
                        hijo.child("Genero").value.toString(),
                        hijo.key.toString()
                    )
                    datos.add(pelicula)
                }
                // Llenamos el RecyclerView con los datos obtenidos
                llenaLista()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error Firebase: ${error.message}")
            }
        })
    }

    // Función que asigna el adaptador al RecyclerView
    private fun llenaLista() {
        val lista = findViewById<RecyclerView>(R.id.lista)

        // LinearLayoutManager organiza los items en lista vertical
        lista.layoutManager = LinearLayoutManager(this)

        val adaptador = PeliAdapter(this, datos) { pelicula ->
            // Al tocar un item navegamos a Detalle mandando los datos
            val intent = Intent(this, Detalle::class.java)
            intent.putExtra("key", pelicula.id)
            intent.putExtra("Nombre", pelicula.nombre)
            intent.putExtra("Genero", pelicula.genero)
            intent.putExtra("Anio", pelicula.anio)
            startActivity(intent)
        }
        lista.adapter = adaptador
    }

    // Esta función infla (carga) el menú en el toolbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Esta función detecta qué opción del menú se presionó
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.salir) {
            cerrarSesion()
        } else if (item.itemId == R.id.perfil) {
            Toast.makeText(this, "opción Perfil", Toast.LENGTH_LONG).show()
        }
        return super.onOptionsItemSelected(item)
    }

    // Cierra sesión en Firebase y regresa al Login
    private fun cerrarSesion() {
        auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}