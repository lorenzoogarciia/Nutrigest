package com.example.nutrigest.clases

import android.widget.Toast

class Usuarios (
    var nombre: String = "",
    var edad: Int = 0,
    var address: String = "",
    var altura: Double = 0.0, // Altura en centímetros
    var mail: String = "",
    var peso: Double = 0.0, // Peso en kilogramos
    var pesoObjetivo: Double = 0.0, // Peso objetivo en kilogramos
    var telefono: String = "",
    var actividad: String = "",
    var genero: String = "Null")
{



    fun calcularIMC(): Double {
        return peso / (altura * altura)
    }

    fun calcularCaloriasDiarias(): Double {
        var calorias = 0.0
        when (actividad) {
            "Sedentario" -> calorias = 1.2
            "Ligera" -> calorias = 1.375
            "Moderada" -> calorias = 1.55
            "Intensa" -> calorias = 1.725
            "Muy intensa" -> calorias = 1.9
        }
        return calorias * calcularTMB()
    }

    fun calcularTMB(): Double {
        return when {
            edad < 18 -> 88.362 + (13.397 * peso) + (4.799 * altura) - (5.677 * edad)
            else -> 447.593 + (9.247 * peso) + (3.098 * altura) - (4.330 * edad)
        }
    }

    fun calcularPesoIdeal(): Double {
        return 22 * (altura * altura)
    }

    fun calcularPesoObjetivo(): Double {
        return pesoObjetivo
    }

    fun calcularIMCObjetivo(): Double {
        return pesoObjetivo / (altura * altura)
    }

    private fun actualizarFirestore(campo: String, valor: Any) {
        //Creamos una instancia de la base de datos
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        //Actualizamos el campo en la base de datos
        db.collection("usuarios").document("mail")
            .update(campo, valor)
            .addOnSuccessListener {
                Toast.makeText(null, "Campo $campo actualizado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(null, "Error al actualizar el campo $campo: $e", Toast.LENGTH_SHORT).show()
            }
    }


    override fun toString(): String {
        return "Usuario(nombre='$nombre', edad=$edad, dirección='$address', altura=$altura, mail='$mail'," +
                " peso=$peso, pesoObjetivo=$pesoObjetivo, teléfono='$telefono', actividad='$actividad', género='$genero')"
    }
}