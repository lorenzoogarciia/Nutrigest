package com.example.nutrigest.clases

import com.google.firebase.firestore.PropertyName

data class Dietas(
    val Almuerzo: List<Alimentos> = listOf(),
    val Cena: List<Alimentos> = listOf(),
    val Desayuno: List<Alimentos> = listOf(),
    @get:PropertyName("Media mañana") @set:PropertyName("Media mañana") var MediaManana: List<Alimentos> = listOf(),
    val Merienda: List<Alimentos> = listOf()
)