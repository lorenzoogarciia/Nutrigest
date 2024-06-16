package com.example.nutrigest.clases

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrigest.R

class DietasUsuarioAdapter(
    private val dietasList: List<Dietas>) :
    RecyclerView.Adapter<DietasUsuarioAdapter.DietasViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietasViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_dietausuario, parent, false)
        return DietasViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DietasViewHolder, position: Int) {
        val dieta = dietasList[position]
        val comidas = listOf(
            "Desayuno" to dieta.Desayuno,
            "Media mañana" to dieta.MediaManana,
            "Almuerzo" to dieta.Almuerzo,
            "Merienda" to dieta.Merienda,
            "Cena" to dieta.Cena
        )

        val mealsDetails = comidas.joinToString("\n") { (nombreComida, comida) ->
            if (comida.isNotEmpty()) {
                "$nombreComida:\n" + comida.joinToString("\n") { alimento ->
                    "${alimento.nombre}: ${formatearAlimento(alimento)}"
                }
            } else {
                "$nombreComida: Sin alimentos"
            }
        }

        // Asigna el texto compilado al TextView
        holder.txtDetallesDietaUsuario.text = mealsDetails
    }

    override fun getItemCount() = dietasList.size

    private fun formatearAlimento(alimento: Alimentos): String {
        return "${alimento.calorias.toInt()} kcal"
    }

    class DietasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtDetallesDietaUsuario: TextView = itemView.findViewById(R.id.txt_detalles_dietausuario)
    }
}