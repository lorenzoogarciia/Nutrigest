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

class DietasUsuarioAdapter(
    private val dietasList: List<Dietas>) :
    RecyclerView.Adapter<DietasUsuarioAdapter.DietasViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietasViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_dieta, parent, false)
        return DietasViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DietasViewHolder, position: Int) {
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
    }

    override fun getItemCount() = dietasList.size

    private fun formatAlimentoDetails(alimento: Alimentos): String {
        return "${alimento.calorias.toInt()} kcal"
    }

    class DietasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtDetallesDieta: TextView = itemView.findViewById(R.id.txt_detalles_dieta)
    }
}