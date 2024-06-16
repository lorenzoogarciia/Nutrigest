package com.example.nutrigest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
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

        //Función setup para inicializar los elementos de la Activity y su backend
        setup()
    }

    private fun setup() {

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
        val spinnerGenero = findViewById<Spinner>(R.id.cmp_genero)
        val btnVolver = findViewById<Button>(R.id.btn_volver)
        val btnGuardar = findViewById<Button>(R.id.btn_guardar)

        ArrayAdapter.createFromResource(
            this,
            R.array.opciones_actividad,
            R.layout.spinner_registro
        ).also { adapter ->
            //Configuración del Spinner
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerActividad.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.opciones_genero,
            R.layout.spinner_genero
        ).also { adapter ->
            // Configuración del Spinner
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerGenero.adapter = adapter
        }

        //Funcionamiento del botón de volver
        btnVolver.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        //Funcionamiento del botón de guardar usuario
        btnGuardar.setOnClickListener {
            try {
                //Guardamos las variables que son datos numericos
                val edad = cmpEdad.text.toString().toIntOrNull() ?: 0
                val peso = cmpPeso.text.toString().toDoubleOrNull() ?: 0.0
                val pesoObjetivo = cmpPesoObjetivo.text.toString().toDoubleOrNull() ?: 0.0
                val altura = cmpAltura.text.toString().toDoubleOrNull() ?: 0.0

                //Comprobamos que los campos Mail y Contraseña no estén vacíos
                if (cmpMail.text.toString().isNotEmpty() && cmpContrasenia.text.toString().isNotEmpty()){

                    //Recogemos la instancia de la BBDD y creamos el usuario para la autenticación
                    FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(cmpMail.text.toString(),cmpContrasenia.text.toString())
                        .addOnCompleteListener(this) {crearUsario ->

                            //Usuario creado correctamente, procedemos con el almacenamiento en la BBDD
                            if (crearUsario.isSuccessful){
                                db.collection("usuarios").document(cmpMail.text.toString()).set(
                                    hashMapOf(
                                        "mail" to cmpMail.text.toString(),
                                        "nombre" to cmpNombre.text.toString(),
                                        "edad" to edad,
                                        "peso" to peso,
                                        "pesoObjetivo" to pesoObjetivo,
                                        "altura" to altura,
                                        "address" to cmpAddress.text.toString(),
                                        "telefono" to cmpTelefono.text.toString(),
                                        "actividad" to spinnerActividad.selectedItem.toString(),
                                        "genero" to spinnerGenero.selectedItem.toString()
                                    )
                                )

                                //Mostramos un mensaje de éxito
                                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                            }else{
                                mostrarAlerta("No se ha podido registrar al usuario para la autenticación")
                            }
                        }

                }else{
                    mostrarAlerta("Por favor, rellene todos los campos")
                }
            }catch (e: FirebaseAuthException){
                mostrarAlerta("Error al registrar al usuario: ${e.message}")
            }
        }
    }

    //Función que muestra los mensajes de alerta
    private fun mostrarAlerta(mensaje: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}

