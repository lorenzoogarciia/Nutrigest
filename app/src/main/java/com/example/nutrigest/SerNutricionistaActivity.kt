package com.example.nutrigest

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

import java.io.ByteArrayOutputStream
import java.util.Properties



class SerNutricionistaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ser_nutricionista)

        //Setup
        setup()
    }

    @SuppressLint("WrongViewCast")
    private fun setup() {
        title = "Ser Nutricionista"

        val cmpMailNutricionista = findViewById<EditText>(R.id.cmp_email_nutricionista)
        val btnEnviarMail = findViewById<Button>(R.id.btn_enviar_mail)
        val btnVolver = findViewById<Button>(R.id.btn_volver)

        btnVolver.setOnClickListener {
            val volverIntent = Intent(this, LoginActivity::class.java)
            startActivity(volverIntent)
            Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show()
        }

        btnEnviarMail.setOnClickListener {
            val mailNutricionista = cmpMailNutricionista.text.toString()

            if (mailNutricionista.isEmpty()) {
                showAlert("Por favor, ingrese un correo electrónico")
            } else {
                Thread {
                    try {
                        enviarMail(mailNutricionista)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                            Log.e("ERROR", e.toString())
                            showAlert("Error al enviar el mail: ${e.message}")
                        }
                    }
                }.start()
            }
        }
    }

    //Función para enviar correo
    private fun enviarMail(mailNutricionista: String) {


    }



        fun showAlert(message: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}