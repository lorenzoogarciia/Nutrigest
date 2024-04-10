package com.example.nutrigest.clases

class Nutricionistas (
    var nombre: String = "",
    var mail: String = ""
) {
    override fun toString(): String {
        return "Usuario(nombre='$nombre', mail='$mail')"
    }
}