package com.example.nutrigest.clases

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrigest.R
import com.example.nutrigest.interfaces.OnItemActionListener
import com.google.firebase.firestore.FirebaseFirestore

class UsuariosAdapter(private val usuariosList: List<Usuarios>, private val itemActionListener: OnItemActionListener, private val context: Context) : RecyclerView.Adapter<UsuariosAdapter.UsuarioViewHolder>() {

    class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Variables para los textos del RecyclerView
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
            //Variable para el mail del usuario seleccionado
            val usuarioSeleccionado = usuarioActual.mail

            //Eliminamos al usuario de la base de datos
            db.collection("usuarios").document(usuarioSeleccionado).delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show()
                    itemActionListener.onItemDeleted() //Actualizamos el RecyclerView
                }
                .addOnFailureListener {
                    mostrarAlerta("Error al eliminar usuario", context)
                }
        }
    }

    //Función que muestra los mensajes de alerta
    private fun mostrarAlerta(mensaje: String, context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun getItemCount() = usuariosList.size
}