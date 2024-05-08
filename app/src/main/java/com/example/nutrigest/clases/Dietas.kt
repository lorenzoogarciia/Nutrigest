package com.example.nutrigest.clases

data class Dietas(
    val Almuerzo: List<Alimentos> = listOf(),
    val Cena: List<Alimentos> = listOf(),
    val Desayuno: List<Alimentos> = listOf(),
    val MediaManana: List<Alimentos> = listOf(),
    val Merienda: List<Alimentos> = listOf()
)