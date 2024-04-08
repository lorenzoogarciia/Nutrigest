package com.example.nutrigest.clases

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrigest.R

class UsuariosAdapter(private val usuariosList: List<Usuarios>) : RecyclerView.Adapter<UsuariosAdapter.UsuarioViewHolder>() {

    class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtNombre: TextView = itemView.findViewById(R.id.txt_nombre)
        var txtEdad: TextView = itemView.findViewById(R.id.txt_edad)
        var txtAltura: TextView = itemView.findViewById(R.id.txt_altura)
        var txtPeso: TextView = itemView.findViewById(R.id.txt_peso)
        var txtPesoObjetivo: TextView = itemView.findViewById(R.id.txt_pesoObjetivo)
        var txtActividad: TextView = itemView.findViewById(R.id.txt_actividad)
        var txtSexo: TextView = itemView.findViewById(R.id.txt_sexo)
        var txtMail: TextView = itemView.findViewById(R.id.txt_mail)
        var txtTelefono: TextView = itemView.findViewById(R.id.txt_telefono)
        var txtDireccion: TextView = itemView.findViewById(R.id.txt_direccion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_usuarios, parent, false)
        return UsuarioViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuarioActual = usuariosList[position]
        holder.txtNombre.text = usuarioActual.nombre.toString()
        holder.txtEdad.text = usuarioActual.edad.toString()
        holder.txtAltura.text = usuarioActual.altura.toString()
        holder.txtPeso.text = usuarioActual.peso.toString()
        holder.txtPesoObjetivo.text = usuarioActual.pesoObjetivo.toString()
        holder.txtActividad.text = usuarioActual.actividad.toString()
        holder.txtSexo.text = usuarioActual.sexo.toString()
        holder.txtMail.text = usuarioActual.mail.toString()
        holder.txtTelefono.text = usuarioActual.telefono.toString()
        holder.txtDireccion.text = usuarioActual.direccion.toString()
    }

    override fun getItemCount() = usuariosList.size
}