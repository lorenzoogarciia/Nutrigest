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

        //Función setup para inicializar los elementos de la Activity y su backend
        setup()
    }

    @SuppressLint("WrongViewCast")
    private fun setup() {

        //Inicialización de los elementos visuales de la Activity
        val cmpMailNutricionista = findViewById<EditText>(R.id.cmp_email_nutricionista)
        val btnEnviarMail = findViewById<Button>(R.id.btn_enviar_mail)
        val btnVolver = findViewById<Button>(R.id.btn_volver)

        //Funcionamiento del botón de volver
        btnVolver.setOnClickListener {
            val volverIntent = Intent(this, LoginActivity::class.java)
            startActivity(volverIntent)
            Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show()
        }

        //Funcionamiento del botón de enviar mail
        btnEnviarMail.setOnClickListener {
            val mailNutricionista = cmpMailNutricionista.text.toString()

            if (mailNutricionista.isEmpty()) {
                mostrarAlerta("Por favor, ingrese un correo electrónico")
            } else {
                Thread {
                    try {
                        //Si hay un email introducido, se envía el mail
                        enviarMail(mailNutricionista)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                            Log.e("ERROR", e.toString())
                            mostrarAlerta("Error al enviar el mail: ${e.message}")
                        }
                    }
                }.start()
            }
        }
    }

    //Función para enviar correo
    private fun enviarMail(mailNutricionista: String) {
        //Apis de nuestra cuenta de MailJet
        val apiKey = "bc5fc57f31b0b4843a71caa38f454bb3"
        val apiSecret = "f619471aeee88e1b151ade21f1fe6b6d"

        //Cliente de OkHttp
        val cliente = OkHttpClient()

        //JSON con los datos del correo
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

        //Petición de OkHttp a la API de MailJet
        val body = json.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("https://api.mailjet.com/v3.1/send")
            .post(body)
            .addHeader("Authorization", Credentials.basic(apiKey, apiSecret))
            .addHeader("Content-Type", "application/json")
            .build()

        //Envío de la petición en segundo plano
        Thread {
            try {
                val response = cliente.newCall(request).execute()
                runOnUiThread {
                    if (response.isSuccessful) {
                        //Si la petición es exitosa, se muestra un mensaje de éxito
                        Toast.makeText(this, "Mail enviado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        //Si la petición falla, se muestra un mensaje de error
                        mostrarAlerta("Error al enviar el mail: ${response.message}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    mostrarAlerta("Error al enviar el mail: ${e.message}")
                }
            }
        }.start()
    }

        //Función que muestra los mensajes de alerta
        fun mostrarAlerta(message: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}