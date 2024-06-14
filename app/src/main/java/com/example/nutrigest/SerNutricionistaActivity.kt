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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.Base64
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.Message
import java.io.ByteArrayOutputStream
import java.util.Properties
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class SerNutricionistaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
        Log.d("enviarMail", "Mail: $mailNutricionista")
        val credential = getServiceAccountCredential()
        val service = Gmail.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory(), credential)
            .setApplicationName("NutriGest")
            .build()

        val props = Properties()
        val session = Session.getDefaultInstance(props, null)

        try {
            val mimeMessage = MimeMessage(session).apply {
                setFrom(InternetAddress("nutrigest@nutrigest.com"))
                addRecipient(javax.mail.Message.RecipientType.TO, InternetAddress(mailNutricionista))
                subject = "Solicitud para ser nutricionista"
                setText("Muy buenas, su solicitud para ser nutricionista ha sido recibida. \n" +
                        "En breve nos pondremos en contacto con usted para continuar con el proceso.")
            }

            val buffer = ByteArrayOutputStream()
            mimeMessage.writeTo(buffer)
            val rawMessage = Base64.encodeBase64URLSafeString(buffer.toByteArray())

            val message = Message().apply {
                raw = rawMessage
            }

            service.users().messages().send("me", message).execute()

            runOnUiThread {
                Toast.makeText(this, "Mail enviado correctamente", Toast.LENGTH_SHORT).show()
            }

        }catch (e: Exception){
            e.printStackTrace()
            runOnUiThread {
                Log.e("ERROR", "Error al enviar mail: ${e.toString()}")
                showAlert("Error al enviar el correo: ${e.message}")
            }
        }
    }

    //Función para obtener las credenciales de la cuenta de Google
    private fun getServiceAccountCredential(): GoogleCredential {
        Log.d("getServiceAccountCredential", "Obteniendo credenciales")
        val inputStream = resources.openRawResource(R.raw.credentials)
        Log.d("getServiceAccountCredential", "InputStream: $inputStream")
        return GoogleCredential.fromStream(inputStream)
            .createScoped(listOf("https://www.googleapis.com/auth/gmail.send"))
            .createDelegated("nutrigest@nutrigest.com")
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