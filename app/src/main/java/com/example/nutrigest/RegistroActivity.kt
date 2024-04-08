package com.example.nutrigest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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
        val cmpNombre = findViewById<EditText>(R.id.cmp_nombre)
        val cmpEdad = findViewById<EditText>(R.id.cmp_edad)
        val cmpPeso = findViewById<EditText>(R.id.cmp_peso)
        val cmpPesoObjetivo = findViewById<EditText>(R.id.cmp_peso_objetivo)
        val cmpAltura = findViewById<EditText>(R.id.cmp_altura)
        val cmpAddress = findViewById<EditText>(R.id.cmp_address)
        val cmpTelefono = findViewById<EditText>(R.id.cmp_telefono)
        val spinnerActividad = findViewById<Spinner>(R.id.cmp_actividad)
        val btnVolver = findViewById<Button>(R.id.btn_volver)
        val btnGuardar = findViewById<Button>(R.id.btn_guardar)

        ArrayAdapter.createFromResource(
            this,
            R.array.opciones_actividad,
            R.layout.spinner_registro
        ).also { adapter ->
            // Especificar el layout a usar cuando aparece la lista de opciones
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Aplicar el adaptador al spinner
            spinnerActividad.adapter = adapter
        }


        btnVolver.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
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
                                        "nombre" to cmpNombre.text.toString(),
                                        "edad" to cmpEdad.text.toString(),
                                        "peso" to cmpPeso.text.toString(),
                                        "pesoObjetivo" to cmpPesoObjetivo.text.toString(),
                                        "altura" to cmpAltura.text.toString(),
                                        "address" to cmpAddress.text.toString(),
                                        "telefono" to cmpTelefono.text.toString(),
                                        "actividad" to spinnerActividad.selectedItem.toString()
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

