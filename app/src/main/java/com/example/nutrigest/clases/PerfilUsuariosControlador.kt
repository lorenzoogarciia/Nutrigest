package com.example.nutrigest.clases

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrigest.R

class PerfilUsuariosControlador(private var datosUsuario: List<String>) :
    RecyclerView.Adapter<PerfilUsuariosControlador.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_perfil, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dato = datosUsuario[position]
        holder.infoUsuario.text = dato
    }

    override fun getItemCount() = datosUsuario.size

    fun actualizarDatos(nuevosUsuarios: List<String>) {
        datosUsuario = nuevosUsuarios
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val infoUsuario: TextView = view.findViewById(R.id.info_usuario)
    }
}


