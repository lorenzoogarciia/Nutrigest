package com.example.nutrigest

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody


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
        val apiKey = "bc5fc57f31b0b4843a71caa38f454bb3"
        val apiSecret = "f619471aeee88e1b151ade21f1fe6b6d"

        val client = OkHttpClient()
        val json = """
        {
            "Messages":[
                {
                    "From": {
                        "Email": "nutrigest.nutricionistas@gmail.com",
                        "Name": "Nutrigest"
                    },
                    "To": [
                        {
                            "Email": "$mailNutricionista",
                            "Name": "Nutricionista"
                        }
                    ],
                    "Subject": "Solicitud para ser Nutricionista",
                    "TextPart": "Muy buenas, su solicitud para ser nutricionista ha sido recibida correctamente, en breve nos pondremos en contacto con usted para continuar con el proceso. Gracias por confiar en nosotros.",
                    "CustomID": "AppGettingStartedTest"
                }
            ]
        }
    """.trimIndent()

        val body = json.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("https://api.mailjet.com/v3.1/send")
            .post(body)
            .addHeader("Authorization", Credentials.basic(apiKey, apiSecret))
            .addHeader("Content-Type", "application/json")
            .build()

        Thread {
            try {
                val response = client.newCall(request).execute()
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this, "Mail enviado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error al enviar el mail: ${response.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Error al enviar el mail: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
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