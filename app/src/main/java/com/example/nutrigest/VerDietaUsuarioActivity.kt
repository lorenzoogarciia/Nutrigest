@file:Suppress("NAME_SHADOWING")

package com.example.nutrigest

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrigest.clases.Dietas
import com.example.nutrigest.clases.DietasUsuarioAdapter
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore

class VerDietaUsuarioActivity : AppCompatActivity(),  NavigationView.OnNavigationItemSelectedListener {
    //Instancia de la base de datos
    private val db = FirebaseFirestore.getInstance()

    //Variables de la interfaz del menú lateral
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    //Variables para la creación del RecyclerView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_dieta_usuario)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_verdietausuario)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_verdietausuario)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigationdrawer_nutrihome_open, R.string.navigationdrawer_nutrihome_close)
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view_verdietausuario)
        navigationView.setNavigationItemSelectedListener(this)

        //Recuperamos el id de la dieta y lo guardamos en una variable
        val idDieta = intent.getStringExtra("idDieta")
        val mail = intent.getStringExtra("mail")


        //Llamamos a la función para actualizar la UI y mostrar las dietas del usuario
        setup(mail.toString(), idDieta.toString())
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setup(email: String?, idDieta: String?){
        //Inicializamos el botón de regresar
        val btnRegresar = findViewById<Button>(R.id.btn_regresar_dietausuario)

        //Actualizamos los datos del usuario en la interfaz
        try {
            actualizarDatosUsuarios(email.toString(), idDieta.toString())
        }catch (e: Exception){
            mostrarAlerta("Error al actualizar datos: ${e.message}")
        }

        // Inicialización del RecyclerView para las dietas
        recyclerView = findViewById(R.id.recyclerView_dietasusuario)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Lista para almacenar las dietas obtenidas de Firestore
        val dietasList = mutableListOf<Dietas>()

        // Inicializamos el adaptador con la lista vacía inicialmente
        val dietasUsuarioAdapter = DietasUsuarioAdapter(dietasList)
        recyclerView.adapter = dietasUsuarioAdapter

        // Obtenemos los detalles de la dieta específica
        db.collection("usuarios")
            .document(email.toString())
            .collection("dietas")
            .document(idDieta.toString())
            .get()
            .addOnSuccessListener { dietaBD ->
                if (dietaBD.exists()) {
                    val dieta = dietaBD.toObject(Dietas::class.java)
                    dieta?.let {
                        dietasList.add(it)
                        dietasUsuarioAdapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(this, "No se encontraron dietas", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                mostrarAlerta("Error al obtener dietas: ${e.message}")
            }

        //Botón para regresar a la actividad anterior
        btnRegresar.setOnClickListener {
            val misDietasIntent = Intent(this, MisDietasActivity::class.java).apply {
                putExtra("mail", email)
            }
            startActivity(misDietasIntent)
            Toast.makeText(this, "Mis Dietas", Toast.LENGTH_SHORT).show()
        }
    }

    //Función que actualiza los datos del usuario en la interfaz
    @SuppressLint("SetTextI18n")
    private fun actualizarDatosUsuarios(email: String?, idDieta: String?){
        val mail = email.toString()
        val mailUsuario = db.collection("usuarios").document(mail)

        try {
            mailUsuario.get().addOnSuccessListener { documento ->
                if (documento != null) {
                    //Variables que recogen los datos de la base de datos
                    val nombre = documento.getString("nombre")
                    val mail = documento.getString("mail")

                    //Variables que actualizan los datos en la interfaz del Navheader
                    val nombreUI = findViewById<TextView>(R.id.navheader_usuarios_nombre)
                    val emailUI = findViewById<TextView>(R.id.navheader_usuarios_email)

                    //Actualización de los datos en la interfaz del navheader
                    nombreUI.text = nombre
                    emailUI.text = mail

                    //Actualización del id de la dieta en la interfaz
                    val txtMisDietas = findViewById<TextView>(R.id.txt_verdietausuario)
                    txtMisDietas.text = "$idDieta"
                } else {
                    mostrarAlerta("No se encontraron datos en la dieta")
                }
            }.addOnFailureListener { exception ->
                mostrarAlerta("Error al obtener datos: $exception")
            }
        }catch (FireBaseException: Exception){
            mostrarAlerta( "Error al Actualizar Interfaz: ${FireBaseException.message}")
        }
    }

    //Función que controla los botones del menú lateral
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
                val dietasIntent = Intent(this, MisDietasActivity::class.java).apply {
                    putExtra("mail", intent.getStringExtra("mailNutricionista"))
                }
                startActivity(dietasIntent)
                drawer.closeDrawer(GravityCompat.START)
                Toast.makeText(this, "Mis Dietas", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_home_four -> {
                try {
                    FirebaseAuth.getInstance().signOut()
                    val loginIntent = Intent(this, LoginActivity::class.java)
                    loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(loginIntent)
                    Toast.makeText(this, "Sesión Cerrada Correctamente", Toast.LENGTH_SHORT).show()
                }catch (e: FirebaseAuthException){
                    mostrarAlerta("Error al cerrar sesión: ${e.message}")
                }
            }
        }
        return true
    }

    //Funciones para el menú lateral
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