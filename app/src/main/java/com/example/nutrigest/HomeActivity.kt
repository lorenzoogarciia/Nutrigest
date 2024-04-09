package com.example.nutrigest

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    //Instancia de la Base de Datos
    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_home)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_home)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigationdrawer_home_open, R.string.navigationdrawer_home_close)
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view_usuarios)
        navigationView.setNavigationItemSelectedListener(this)

        //Setup
        val bundle = intent.extras
        val email: String?  = bundle?.getString("mail")

        setup(email.toString())


    }

   private fun setup(email: String?){

       actualizarDatosUsuarios(email.toString())
        }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val email: String = intent.getStringExtra("mail").toString()
        when(item.itemId){
            R.id.nav_home_one -> {
                Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
                drawer.closeDrawer(GravityCompat.START)
            }
            R.id.nav_home_two -> {
                val perfilIntent = Intent(this, PerfilActivity::class.java).apply{
                    putExtra("mail", email)}
                startActivity(perfilIntent)
                drawer.closeDrawer(GravityCompat.START)
                Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_home_three -> {
                val misDietasIntent = Intent(this, MisDietasActivity::class.java).apply{
                    putExtra("mail", email)}
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
                }catch (e: FirebaseException){
                    showAlert("Error al cerrar sesión: ${e.message}")
                }
            }
        }
        return true
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    //Función que actualiza los datos del usuario
    @SuppressLint("SetTextI18n")
    private fun actualizarDatosUsuarios(email: String){
        val mail = email
        val docRef = db.collection("usuarios").document(mail)

        try {

            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    //Variables que recogen los datos de la base de datos
                    val nombre = document.getString("nombre")
                    val mail = document.getString("mail")

                    //Variables que actualizan los datos en la interfaz del Navheader
                    val nombreUI = findViewById<TextView>(R.id.navheader_usuarios_nombre)
                    val emailUI = findViewById<TextView>(R.id.navheader_usuarios_email)

                    //Actualización de los datos en la interfaz del navheader
                    nombreUI.text = nombre
                    emailUI.text = mail

                    //Actualización de los datos en la interfaz del HomeActivity
                    val txtHome = findViewById<TextView>(R.id.txt_home)
                    txtHome.text = "¡Bienvenido ${nombre}! "
                } else {
                    showAlert("No se encontraron datos")
                }
            }.addOnFailureListener { exception ->
                showAlert("Error al obtener datos: $exception")
            }
        }catch (FireBaseException: Exception){
            showAlert("Error al Actualizar UI: ${FireBaseException.message}")
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