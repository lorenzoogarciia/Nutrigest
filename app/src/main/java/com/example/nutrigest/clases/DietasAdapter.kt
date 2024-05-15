package com.example.nutrigest.clases

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrigest.R
import com.example.nutrigest.interfaces.OnItemActionListener
import com.google.firebase.firestore.FirebaseFirestore

class DietasAdapter(
    private val dietasList: List<Dietas>,
    private val itemActionListener: OnItemActionListener,
    private val mailUsuario: String,
    private val idDieta: String) :
    RecyclerView.Adapter<DietasAdapter.DietasViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietasViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_dieta, parent, false)
        return DietasViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DietasViewHolder, position: Int) {
        val db = FirebaseFirestore.getInstance()
        val dieta = dietasList[position]
        val allMeals = listOf(
            "Desayuno" to dieta.Desayuno,
            "Media mañana" to dieta.MediaManana,
            "Almuerzo" to dieta.Almuerzo,
            "Merienda" to dieta.Merienda,
            "Cena" to dieta.Cena
        )

        val mealsDetails = allMeals.joinToString("\n") { (mealName, meals) ->
            if (meals.isNotEmpty()) {
                "$mealName:\n" + meals.joinToString("\n") { alimento ->
                    "${alimento.nombre}: ${formatAlimentoDetails(alimento)}"
                }
            } else {
                "$mealName: Sin alimentos"
            }
        }

        // Asigna el texto compilado al TextView
        holder.txtDetallesDieta.text = mealsDetails
        Log.d("DietasAdapter", "mailUsuario: $mailUsuario, idDieta: $idDieta")
        holder.btnEliminarDieta.setOnClickListener {
            // Eliminar dieta
            db.collection("usuarios").document(mailUsuario).collection("dietas").document(idDieta).delete()
                .addOnSuccessListener {
                    Toast.makeText(holder.itemView.context, "Dieta eliminada correctamente", Toast.LENGTH_SHORT).show()
                    itemActionListener.onItemDeleted()
                }
                .addOnFailureListener {
                    Toast.makeText(holder.itemView.context, "Error al eliminar la dieta", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun getItemCount() = dietasList.size

    private fun formatAlimentoDetails(alimento: Alimentos): String {
        return "${alimento.calorias.toInt()} kcal"
    }

    class DietasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtDetallesDieta: TextView = itemView.findViewById(R.id.txt_detalles_dieta)
        val btnEliminarDieta: TextView = itemView.findViewById(R.id.btn_eliminar_dieta)
    }
}