package com.example.nutrigest.clases

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrigest.R

class PerfilNutricionistasAdapter(private var datosNutricionista: List<String>) :
    RecyclerView.Adapter<PerfilNutricionistasAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_nutriperfil, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dato = datosNutricionista[position]
        holder.infoNutricionista.text = dato
    }

    override fun getItemCount() = datosNutricionista.size

    @SuppressLint("NotifyDataSetChanged")
    fun actualizarDatos(nuevosUsuarios: List<String>) {
        datosNutricionista = nuevosUsuarios
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val infoNutricionista: TextView = view.findViewById(R.id.info_nutricionista)
    }
}