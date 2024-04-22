package com.example.nutrigest.clases

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrigest.R

class AlimentosAdapter(private val alimentosList: List<Alimentos>) : RecyclerView.Adapter<AlimentosAdapter.AlimentoViewHolder>() {

    class AlimentoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(alimento: Alimentos) {
            itemView.findViewById<TextView>(R.id.txt_nombre).text = alimento.nombre
            itemView.findViewById<TextView>(R.id.txt_cantidad).text ="Cantidad: ${alimento.cantidad}"
            itemView.findViewById<TextView>(R.id.txt_calorias).text = "Kcalorías: ${alimento.calorias}"
            itemView.findViewById<TextView>(R.id.txt_kilojulios).text = "KiloJulios: ${alimento.kilojulios}"
            itemView.findViewById<TextView>(R.id.txt_hidratos).text = "Hidratos de Carbono: ${alimento.hidratos}"
            itemView.findViewById<TextView>(R.id.txt_proteinas).text = "Proteínas: ${alimento.proteinas}"
            itemView.findViewById<TextView>(R.id.txt_grasas).text = "Grasas: ${alimento.grasas}"
            itemView.findViewById<TextView>(R.id.txt_azucares).text = "Azúcares: ${alimento.azucar}"
            itemView.findViewById<TextView>(R.id.txt_fibra).text = "Fibra: ${alimento.fibra}"
            itemView.findViewById<TextView>(R.id.txt_sal).text = "Sal: ${alimento.sal}"

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlimentoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alimentos, parent, false)
        return AlimentoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlimentoViewHolder, position: Int) {
        holder.bind(alimentosList[position])
    }

    override fun getItemCount() = alimentosList.size
}