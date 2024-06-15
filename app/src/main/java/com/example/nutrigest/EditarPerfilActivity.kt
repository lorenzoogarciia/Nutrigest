package com.example.nutrigest

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("NAME_SHADOWING")
class EditarPerfilActivity : AppCompatActivity(),  NavigationView.OnNavigationItemSelectedListener {
    //Variables para el Navigation Drawer
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    //Instancia de la base de datos
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        // Configuración del Navigation Drawer
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_editarperfil)
        setSupportActionBar(toolbar)

        // Configuración del Navigation Drawer
        drawer = findViewById(R.id.drawer_editar_perfil)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigationdrawer_home_open, R.string.navigationdrawer_home_close)
        drawer.addDrawerListener(toggle)

        // Configuración del Navigation Drawer
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        // Configuración del Navigation Drawer
        val navigationView: NavigationView = findViewById(R.id.nav_view_perfilusuarios)
        navigationView.setNavigationItemSelectedListener(this)

        // Recuperamos el email del usuario
        val bundle = intent.extras
        val email  = bundle?.getString("mail")

        setup(email)

    }

    private fun setup(email: String?){
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
        val btnActualizar = findViewById<Button>(R.id.btn_actualizar_perfil)

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

        ArrayAdapter.createFromResource(
            this,
            R.array.opciones_genero,
            R.layout.spinner_genero
        ).also { adapter ->
            // Especificar el layout a usar cuando aparece la lista de opciones
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Aplicar el adaptador al spinner
            spinnerGenero.adapter = adapter
        }

        btnVolver.setOnClickListener {
            val perfilIntent = Intent(this, PerfilActivity::class.java)
            startActivity(perfilIntent)
            Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show()
        }

        btnActualizar.setOnClickListener {
            currentPasswordDialog { currentPassword ->
                val email = cmpMail.text.toString()
                val nuevaContrasenia = cmpContrasenia.text.toString()
                val nombre = cmpNombre.text.toString()
                val edad = cmpEdad.text.toString().toIntOrNull() ?: 0
                val peso = cmpPeso.text.toString().toDoubleOrNull() ?: 0.0
                val pesoObjetivo = cmpPesoObjetivo.text.toString().toDoubleOrNull() ?: 0.0
                val altura = cmpAltura.text.toString().toIntOrNull() ?: 0
                val address = cmpAddress.text.toString()
                val telefono = cmpTelefono.text.toString()
                val actividad = spinnerActividad.selectedItem.toString()
                val genero = spinnerGenero.selectedItem.toString()

                val user = auth.currentUser

                if (user != null) {
                    val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
                    user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                        if (reauthTask.isSuccessful) {
                            Log.d("EditarPerfilActivity", "Reautenticación exitosa")
                            updateUserProfile(user, email, nuevaContrasenia, nombre, edad, peso, pesoObjetivo, altura, address, telefono, actividad, genero)
                        } else {
                            Log.e("EditarPerfilActivity", "Error de reautenticación: ${reauthTask.exception?.message}")
                            Toast.makeText(this, "Error de reautenticación: ${reauthTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("EditarPerfilActivity", "Usuario no autenticado")
                    Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                }
            }
        }
   }

    @SuppressLint("MissingInflatedId")
    private fun currentPasswordDialog(callback: (String) -> Unit){
        val dialogView = layoutInflater.inflate(R.layout.dialog_password, null)
        val EditTextPassword = dialogView.findViewById<EditText>(R.id.editText_password)

        AlertDialog.Builder(this)
            .setTitle("Reautenticación Requerida")
            .setMessage("Ingrese su contraseña actual")
            .setView(dialogView)
            .setPositiveButton("Aceptar"){_, _ ->
                val password = EditTextPassword.text.toString()

                if (password.isNotEmpty()){
                    callback(password)
                }else{
                    showAlert("Por favor, ingrese su contraseña")
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun updateUserProfile(user: FirebaseUser, email: String, nuevaContrasenia: String,
                                  nombre: String, edad: Int, peso: Double, pesoObjetivo: Double, altura: Int, address: String,
                                  telefono: String, actividad: String, genero: String) {
        val updateTasks = mutableListOf<Task<Void>>()

        if (email.isNotEmpty() && email != user.email) {
            updateTasks.add(user.updateEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("EditarPerfilActivity", "Email actualizado exitosamente")
                } else {
                    Log.e("EditarPerfilActivity", "Error al actualizar email: ${task.exception?.message}")
                }
            })
        }

        if (nuevaContrasenia.isNotEmpty()) {
            updateTasks.add(user.updatePassword(nuevaContrasenia).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("EditarPerfilActivity", "Contraseña actualizada exitosamente")
                } else {
                    Log.e("EditarPerfilActivity", "Error al actualizar contraseña: ${task.exception?.message}")
                }
            })
        }

        Tasks.whenAllComplete(updateTasks).addOnCompleteListener { allTasks ->
            if (allTasks.isSuccessful) {
                Log.d("EditarPerfilActivity", "Todas las tareas de actualización de Auth completadas exitosamente")
                val userData = hashMapOf(
                    "mail" to (if (email.isNotEmpty() && email != user.email) email else user.email!!),
                    "nombre" to nombre,
                    "edad" to edad,
                    "peso" to peso,
                    "pesoObjetivo" to pesoObjetivo,
                    "altura" to altura,
                    "address" to address,
                    "telefono" to telefono,
                    "actividad" to actividad,
                    "genero" to genero
                )

                val idUsuario = user.email ?: ""
                Log.d("EditarPerfilActivity", "Actualizando datos del perfil en Firestore para el usuario con email: $idUsuario")
                Log.d("EditarPerfilActivity", "Datos del perfil a actualizar: $userData")

                db.collection("usuarios").document(idUsuario)
                    .set(userData)
                    .addOnSuccessListener {
                        Log.d("EditarPerfilActivity", "Datos del perfil actualizados exitosamente en Firestore")
                        Toast.makeText(this, "Perfil actualizado exitosamente", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("EditarPerfilActivity", "Error al actualizar perfil en Firestore: ${e.message}")
                        Toast.makeText(this, "Error al actualizar perfil: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                allTasks.exception?.let {
                    Log.e("EditarPerfilActivity", "Error al completar tareas de actualización: ${it.message}")
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("EditarPerfilActivity", "Error al completar tareas de actualización: ${exception.message}")
        }
    }



    //Función que maneja la lógica del Navigation Drawer
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_home_one -> {
                val homeIntent = Intent(this, HomeActivity::class.java).apply{
                    putExtra("mail", intent.getStringExtra("mail"))}
                startActivity(homeIntent)
                drawer.closeDrawer(GravityCompat.START)
                Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_home_two -> {
                val perfilIntent = Intent(this, PerfilActivity::class.java).apply {
                    putExtra("mail", intent.getStringExtra("mail"))
                }
                startActivity(perfilIntent)
                drawer.closeDrawer(GravityCompat.START)
                Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_home_three -> {
                val misDietasIntent = Intent(this, MisDietasActivity::class.java).apply {
                    putExtra("mail", intent.getStringExtra("mail"))
                }
                startActivity(misDietasIntent)
                drawer.closeDrawer(GravityCompat.START)
                Toast.makeText(this, "Mis dietas", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_home_four -> {
                try {
                    FirebaseAuth.getInstance().signOut()
                    val loginIntent = Intent(this, LoginActivity::class.java)
                    loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(loginIntent)
                    Toast.makeText(this, "Sesión Cerrada Correctamente", Toast.LENGTH_SHORT).show()
                }catch (e: FirebaseAuthException){
                    showAlert("Error al cerrar sesión: ${e.message}")
                }
            }
        }
        return true
    }

    //Función que muestra los mensajes de alerta
    private fun showAlert(mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}