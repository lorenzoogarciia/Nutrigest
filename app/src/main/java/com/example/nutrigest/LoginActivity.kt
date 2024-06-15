package com.example.nutrigest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    //Instancia de la BBDD
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        //Enseñamos la SplashScreen de la APP para mejorar la UX
        Thread.sleep(1000)
        setTheme(R.style.Base_Theme_NutriGest)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /*Llamamos a la función setup para inicializar todos los elementos
        * de la Activity y su backend*/
        setup()
    }

    //Función setup
    private fun setup(){

        //Inicializamos los elementos visuales del Activity
        val btnSignup = findViewById<Button>(R.id.btn_signup)
        val btnSignin = findViewById<Button>(R.id.btn_signin)
        val btnSerNutricionista = findViewById<Button>(R.id.btn_sernutricionista)
        val txtMail = findViewById<EditText>(R.id.cmp_mail)
        val txtPasswd = findViewById<EditText>(R.id.cmp_passwd)

        //Funcionamiento del botón de registro
        btnSignup.setOnClickListener {
            val signupIntent = Intent(this, RegistroActivity::class.java)
            startActivity(signupIntent)
            Toast.makeText(this, "Registro", Toast.LENGTH_SHORT).show()
        }

        btnSerNutricionista.setOnClickListener {
            val serNutricionistaIntent = Intent(this, SerNutricionistaActivity::class.java)
            startActivity(serNutricionistaIntent)
            Toast.makeText(this, "Ser Nutricionista", Toast.LENGTH_SHORT).show()
        }

        //Funcionamiento del botón de Inicio de Sesión
        btnSignin.setOnClickListener {
            //Creamos el bloque try-catch para asegurar el funcionamiento
            try {
                //Verificamos que los campos contraseña y mail no estén vacíos
                if (txtMail.text.isNotEmpty() && txtPasswd.text.isNotEmpty()){
                    //Instanciamos la autenticación de FireBase
                    FirebaseAuth.getInstance()
                        //Iniciamos sesión con el usuario y la contraseña de los campos
                        .signInWithEmailAndPassword(txtMail.text.toString(),
                            txtPasswd.text.toString()).addOnCompleteListener {
                                //Verificamos que el usuario esté creado
                            if(it.isSuccessful){
                                val mail = it.result?.user?.email ?: ""
                                Log.d("LoginActivity", "Usuario autenticado: $mail")
                                validarRolUsuario(mail)
                            }else{
                                showAlert("Por favor, revise sus credenciales")
                            }
                        }
                }else{
                    //Si los campos están vacíos lanzamos un mensaje de alerta
                    showAlert("Por favor, rellene todos los campos")
                }
            }catch (e: FirebaseAuthException){
                //Capturamos la excepción y la mostramos por pantalla en caso de error
                showAlert("Error de autenticación: ${e.stackTraceToString()}")
            }
        }
    }

    //Función que muestra el home de los "usuarios"
    private fun showHome(mail: String) {
        val homeIntent = Intent(this,HomeActivity::class.java).apply {
            putExtra("mail", mail)
        }
        startActivity(homeIntent)
    }

    //Función que muestra los mensajes de alerta
    private fun showAlert(mensaje: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    //Función que muestra el home de los "nutricionistas"
    private fun showNutriHome(mail: String){
        val nutriHomeIntent = Intent(this,NutriHomeActivity::class.java).apply {
            putExtra("mail", mail)
        }
        startActivity(nutriHomeIntent)
    }

    //Función que valida si el usuario es "usuario" o nutricionista
    private fun validarRolUsuario(mail: String?){
        db.collection("nutricionistas").whereEqualTo("mail",mail.toString()).get().addOnSuccessListener { documents ->
            if (documents != null && !documents.isEmpty){
                //El usuario es nutricionista
                showNutriHome(mail.toString())
            }else{
                //Verificar si el usuario es común
                db.collection("usuarios").whereEqualTo("mail",mail.toString()).get().addOnSuccessListener { userDocuments ->
                    if (userDocuments != null && !userDocuments.isEmpty){
                        //El usuario es común
                        showHome(mail.toString())
                    }else{
                        //Si el usuario no se encuentra en ninguna de las dos colecciones
                        showAlert("El usuario no se encuentra en la Base de Datos")
                    }
                }.addOnFailureListener { exception ->
                    showAlert("Error al verificar el tipo de usuario: ${exception.message}")
                }
            }
        }

    }

}