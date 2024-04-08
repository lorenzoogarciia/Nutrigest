package com.example.nutrigest.clases

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrigest.R

class UsuariosControlador(private val usuariosList: List<Usuarios>) : RecyclerView.Adapter<UsuariosControlador.UsuarioViewHolder>() {

    class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtNombre: TextView = itemView.findViewById(R.id.txt_nombre)
        var txtEdad: TextView = itemView.findViewById(R.id.txt_edad)
        var txtAltura: TextView = itemView.findViewById(R.id.txt_altura)
        var txtPeso: TextView = itemView.findViewById(R.id.txt_peso)
        var txtPesoObjetivo: TextView = itemView.findViewById(R.id.txt_pesoObjetivo)
        var txtActividad: TextView = itemView.findViewById(R.id.txt_actividad)
        var txtGenero: TextView = itemView.findViewById(R.id.txt_genero)
        var txtMail: TextView = itemView.findViewById(R.id.txt_mail)
        var txtTelefono: TextView = itemView.findViewById(R.id.txt_telefono)
        var txtDireccion: TextView = itemView.findViewById(R.id.txt_address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_usuarios, parent, false)
        return UsuarioViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuarioActual = usuariosList[position]
        holder.txtNombre.text = "Nombre: ${usuarioActual.nombre.toString()}"
        holder.txtEdad.text = "Edad: ${usuarioActual.edad.toString()} años"
        holder.txtAltura.text = "Altura: ${usuarioActual.altura.toString()}"
        holder.txtPeso.text = "Peso Actual: ${usuarioActual.peso.toString()} kg"
        holder.txtPesoObjetivo.text = "Peso Objetivo: ${usuarioActual.pesoObjetivo.toString()} kg"
        holder.txtActividad.text = "Actividad: ${usuarioActual.actividad.toString()}"
        holder.txtGenero.text = "Género: ${usuarioActual.genero.toString()}"
        holder.txtMail.text = "Mail: ${usuarioActual.mail.toString()}"
        holder.txtTelefono.text = "Teléfono: ${usuarioActual.telefono.toString()}"
        holder.txtDireccion.text = "Dirección: ${usuarioActual.address.toString()}"
    }

    override fun getItemCount() = usuariosList.size
}