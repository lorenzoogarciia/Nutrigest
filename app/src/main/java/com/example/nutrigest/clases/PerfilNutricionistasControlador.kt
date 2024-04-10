package com.example.nutrigest.clases

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.nutrigest.R

class PerfilNutricionistasControlador(private var datosNutricionista: List<String>) :
    RecyclerView.Adapter<PerfilNutricionistasControlador.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_nutriperfil, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dato = datosNutricionista[position]
        holder.infoNutricionista.text = dato
    }

    override fun getItemCount() = datosNutricionista.size

    fun actualizarDatos(nuevosUsuarios: List<String>) {
        datosNutricionista = nuevosUsuarios
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val infoNutricionista: TextView = view.findViewById(R.id.info_nutricionista)
    }
}