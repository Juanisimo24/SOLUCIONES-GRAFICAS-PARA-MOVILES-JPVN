package com.example.proyectofragmentos
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyectofragmentos.R
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val calcu = findViewById<Button>(R.id.calcu)
        val imc = findViewById<Button>(R.id.imc)

        calcu.setOnClickListener {
            reemplazarFragmento(Calculadora())
        }

        imc.setOnClickListener {
            reemplazarFragmento(Imc())
        }
    }

    private fun reemplazarFragmento(fragmento: Fragment) {
        val manager = supportFragmentManager
        val transaccion = manager.beginTransaction()
        transaccion.replace(R.id.fragmentContainerView, fragmento)
        transaccion.commit()
    }

    // Función llamable desde cualquier fragment
    fun mensaje(texto: String) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show()
    }
}