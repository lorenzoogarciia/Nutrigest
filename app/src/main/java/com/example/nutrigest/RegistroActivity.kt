package com.example.nutrigest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore

class RegistroActivity : AppCompatActivity() {
    //Instancia de la Base de Datos
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        //Setup
        setup()
    }

    private fun setup() {
        title = "Registro"

        //Inicialización de Elementos en Pantalla
        val cmpMail = findViewById<EditText>(R.id.cmp_email)
        val cmpContrasenia = findViewById<EditText>(R.id.cmp_contrasenia)
        val cmpAddress = findViewById<EditText>(R.id.cmp_address)
        val cmpTelefono = findViewById<EditText>(R.id.cmp_telefono)
        val btnVolver = findViewById<Button>(R.id.btn_volver)
        val btnGuardar = findViewById<Button>(R.id.btn_guardar)


        btnVolver.setOnClickListener {
            onBackPressed()
        }

        btnGuardar.setOnClickListener {
            try {
                //Comprobamos que los campos Mail y Contraseña no estén vacíos
                if (cmpMail.text.toString().isNotEmpty() && cmpContrasenia.text.toString().isNotEmpty()){

                    //Recogemos la instancia de la BBDD y creamos el usuario para la autenticación
                    FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(cmpMail.text.toString(),cmpContrasenia.text.toString())
                        .addOnCompleteListener(this) {task ->

                            //Usuario creado correctamente, procedemos con el almacenamiento en la BBDD
                            if (task.isSuccessful){
                                db.collection("usuarios").document(cmpMail.text.toString()).set(
                                    hashMapOf(
                                        "mail" to cmpMail.text.toString(),
                                        "address" to cmpAddress.text.toString(),
                                        "telefono" to cmpTelefono.text.toString()
                                    )
                                )
                            }else{
                                showAlert("No se ha podido registrar al usuario para la autenticación")
                            }
                        }

                }else{
                    showAlert("Por favor, rellene todos los campos")
                }
            }catch (e: FirebaseAuthException){
                e.printStackTrace()
            }
        }
    }

    private fun showAlert(mensaje: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}

