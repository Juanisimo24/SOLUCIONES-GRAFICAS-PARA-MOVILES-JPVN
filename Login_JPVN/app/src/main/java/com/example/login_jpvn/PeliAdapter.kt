package com.example.login_jpvn

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// PeliAdapter conecta la lista de películas con el RecyclerView.
// RecyclerView.Adapter recicla las vistas para mejor rendimiento.
class PeliAdapter(
    private val context: Activity,
    private val arraylist: ArrayList<Peliculas>,
    // Lambda que se ejecuta cuando el usuario toca un item de la lista
    private val onItemClick: (Peliculas) -> Unit
) : RecyclerView.Adapter<PeliAdapter.ViewHolder>() {

    // ViewHolder guarda las referencias a las vistas de cada item
    // para no buscarlas cada vez (mejor rendimiento)
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.itemTitulo)
        val genero: TextView = view.findViewById(R.id.itemGenero)
        val anio: TextView = view.findViewById(R.id.itemAnio)
    }

    // Crea una nueva vista para cada item de la lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item, parent, false)
        return ViewHolder(view)
    }

    // Llena los datos de cada item con la información de la película
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pelicula = arraylist[position]
        holder.titulo.text = pelicula.nombre
        holder.genero.text = pelicula.genero
        holder.anio.text = pelicula.anio

        // Asignamos el click a cada item de la lista
        holder.itemView.setOnClickListener {
            onItemClick(pelicula)
        }
    }

    // Retorna el total de items en la lista
    override fun getItemCount(): Int = arraylist.size
}