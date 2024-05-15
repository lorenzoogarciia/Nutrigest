package com.example.nutrigest.clases

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrigest.R
import com.example.nutrigest.interfaces.OnItemActionListener
import com.google.firebase.firestore.FirebaseFirestore

class UsuariosAdapter(private val usuariosList: List<Usuarios>, private val itemActionListener: OnItemActionListener) : RecyclerView.Adapter<UsuariosAdapter.UsuarioViewHolder>() {

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
        var btn_eliminar_usuario: TextView = itemView.findViewById(R.id.btn_eliminar_usuario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_usuarios, parent, false)
        return UsuarioViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        //Instancia de la Base de datos
        val db = FirebaseFirestore.getInstance()
        //Textos del RecyclerView
        val usuarioActual = usuariosList[position]
        holder.txtNombre.text = "Nombre: ${usuarioActual.nombre}"
        holder.txtEdad.text = "Edad: ${usuarioActual.edad} años"
        holder.txtAltura.text = "Altura: ${usuarioActual.altura}"
        holder.txtPeso.text = "Peso Actual: ${usuarioActual.peso} kg"
        holder.txtPesoObjetivo.text = "Peso Objetivo: ${usuarioActual.pesoObjetivo} kg"
        holder.txtActividad.text = "Actividad: ${usuarioActual.actividad}"
        holder.txtGenero.text = "Género: ${usuarioActual.genero}"
        holder.txtMail.text = "Mail: ${usuarioActual.mail}"
        holder.txtTelefono.text = "Teléfono: ${usuarioActual.telefono}"
        holder.txtDireccion.text = "Dirección: ${usuarioActual.address}"

        //Botón para eliminar usuario
        holder.btn_eliminar_usuario.setOnClickListener {
            // Eliminar usuario
            val usuarioSeleccionado = usuarioActual.mail

            db.collection("usuarios").document(usuarioSeleccionado).delete()
                .addOnSuccessListener {
                    Toast.makeText(holder.itemView.context, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                    itemActionListener.onItemDeleted()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(holder.itemView.context, "Error al eliminar Usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                }

        }
    }

    override fun getItemCount() = usuariosList.size
}