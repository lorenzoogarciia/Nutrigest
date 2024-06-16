package com.example.nutrigest.clases

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

class DietasAdapter(
    private val dietasList: List<Dietas>,
    private val itemActionListener: OnItemActionListener,
    private val mailUsuario: String,
    private val idDieta: String,
    private val context: Context) :
    RecyclerView.Adapter<DietasAdapter.DietasViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietasViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_dieta, parent, false)
        return DietasViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DietasViewHolder, position: Int) {
        val db = FirebaseFirestore.getInstance()
        val dieta = dietasList[position]
        val comidas = listOf(
            "Desayuno" to dieta.Desayuno,
            "Media mañana" to dieta.MediaManana,
            "Almuerzo" to dieta.Almuerzo,
            "Merienda" to dieta.Merienda,
            "Cena" to dieta.Cena
        )

        val detallesComida = comidas.joinToString("\n") { (nombreComida, comida) ->
            if (comida.isNotEmpty()) {
                "$nombreComida:\n" + comida.joinToString("\n") { alimento ->
                    "${alimento.nombre}: ${formatearAlimento(alimento)}"
                }
            } else {
                "$nombreComida: Sin alimentos"
            }
        }

        holder.txtDetallesDieta.text = detallesComida
        holder.btnEliminarDieta.setOnClickListener {
            // Eliminamos la dieta
            db.collection("usuarios").document(mailUsuario).collection("dietas").document(idDieta).delete()
                .addOnSuccessListener {
                    Toast.makeText(holder.itemView.context, "Dieta eliminada correctamente", Toast.LENGTH_SHORT).show()
                    itemActionListener.onItemDeleted()
                }
                .addOnFailureListener {
                    mostrarAlerta("Error al eliminar la dieta", holder.itemView.context)
                }
        }
    }

    override fun getItemCount() = dietasList.size

    private fun formatearAlimento(alimento: Alimentos): String {
        return "${alimento.calorias.toInt()} kcal"
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

    class DietasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtDetallesDieta: TextView = itemView.findViewById(R.id.txt_detalles_dieta)
        val btnEliminarDieta: TextView = itemView.findViewById(R.id.btn_eliminar_dieta)
    }
}