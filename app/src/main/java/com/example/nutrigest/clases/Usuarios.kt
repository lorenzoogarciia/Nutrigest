package com.example.nutrigest.clases

import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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



    //Función que calcula el IMC del usuario
    fun calcularIMC(): Double {
        return peso / (altura * altura)
    }

    //Función que calcula las calorías diarias que necesita el usuario
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

    //Función que calcula el TMB del usuario
    fun calcularTMB(): Double {
        return when {
            edad < 18 -> 88.362 + (13.397 * peso) + (4.799 * altura) - (5.677 * edad)
            else -> 447.593 + (9.247 * peso) + (3.098 * altura) - (4.330 * edad)
        }
    }

    //Función que calcula el peso ideal del usuario
    fun calcularPesoIdeal(): Double {
        return 22 * (altura * altura)
    }

    //Función que calcula el peso objetivo del usuario
    fun calcularPesoObjetivo(): Double {
        return pesoObjetivo
    }

    //Función que calcula el IMC objetivo del usuario
    fun calcularIMCObjetivo(): Double {
        return pesoObjetivo / (altura * altura)
    }

    //Función que crea un ID para la dieta del usuario al ser creada
    fun crearIdDieta(nombre: String): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val fechaActual = Date()
        val fechaFormateada = sdf.format(fechaActual)
        return "Dieta ${nombre} ${fechaFormateada}"
    }

    override fun toString(): String {
        return "Usuario(nombre='$nombre', edad=$edad, dirección='$address', altura=$altura, mail='$mail'," +
                " peso=$peso, pesoObjetivo=$pesoObjetivo, teléfono='$telefono', actividad='$actividad', género='$genero')"
    }
}