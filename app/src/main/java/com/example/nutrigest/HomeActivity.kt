package com.example.nutrigest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {
    //Instancia de la Base de Datos
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //Setup
        val bundle = intent.extras
        val email: String?  = bundle?.getString("email")

        setup(email.toString())

    }

    private fun setup(email: String?){
        //Inicializamos los objetos en pantalla
        val txtHome = findViewById<TextView>(R.id.txt_home)
        val btnRegresar = findViewById<Button>(R.id.btn_regresar)

        btnRegresar.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }

    }

}