package com.example.nutrigest

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrigest.clases.Nutricionistas
import com.example.nutrigest.clases.PerfilNutricionistasControlador
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore

class NutriPerfilActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    //Variables para el ReciclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var perfilNutricionistasControlador: PerfilNutricionistasControlador

    //Variables para el Navigation Drawer
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    //Instancia de la base de datos
    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutri_perfil)

        // Configuración del Navigation Drawer
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_perfilnutricionistas)
        setSupportActionBar(toolbar)

        // Configuración del Navigation Drawer
        drawer = findViewById(R.id.drawer_perfilnutricionistas)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigationdrawer_nutrihome_open, R.string.navigationdrawer_nutrihome_close)
        drawer.addDrawerListener(toggle)

        // Configuración del Navigation Drawer
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        // Configuración del Navigation Drawer
        val navigationView: NavigationView = findViewById(R.id.nav_view_perfilnutricionistas)
        navigationView.setNavigationItemSelectedListener(this)

        // Recuperamos el email del usuario
        val bundle = intent.extras
        val mail: String?  = bundle?.getString("mail")

        //Llamada a la función setup
        setup(mail.toString())
    }

    //Función que inicializa todos los elementos y funciones de la Activity
    private fun setup(email: String?){
        //Llamada a las funciones que actualizan la UI y muestran los datos del Nutricionista
        try {
            actualizarDatosUsuarios(email.toString())
        }catch (FireBaseException: Exception){
            showAlert("Error al Actualizar UI: ${FireBaseException.message}")
        }

        try {
            mostrarDatosNutricionista(email.toString())
        }catch (FireBaseException: Exception){
            showAlert("Error al Mostrar Datos del Nutricionista: ${FireBaseException.message}")
        }
    }

    //Función que maneja el comportamiento de los botones del menú lateral
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_nutrihome_one -> {
                val homeIntent = Intent(this, NutriHomeActivity::class.java).apply{
                    putExtra("mail", intent.getStringExtra("mail"))}
                startActivity(homeIntent)
                drawer.closeDrawer(GravityCompat.START)
                Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_nutrihome_two -> {
                drawer.closeDrawer(GravityCompat.START)
                Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_nutrihome_three -> {
                val dietasIntent = Intent(this, NutriDietasActivity::class.java).apply {
                    putExtra("mail", intent.getStringExtra("mail"))
                }
                startActivity(dietasIntent)
                drawer.closeDrawer(GravityCompat.START)
                Toast.makeText(this, "Dietas", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_nutrihome_four -> {
                val alimentosIntent = Intent(this, AlimentosActivity::class.java).apply {
                    putExtra("mail", intent.getStringExtra("mail"))
                }
                startActivity(alimentosIntent)
                drawer.closeDrawer(GravityCompat.START)
                Toast.makeText(this, "Alimentos", Toast.LENGTH_SHORT).show()

            }
            R.id.nav_nutrihome_five -> {
                val clientesIntent = Intent(this, ClientesActivity::class.java).apply {
                    putExtra("mail", intent.getStringExtra("mail"))
                }
                startActivity(clientesIntent)
                drawer.closeDrawer(GravityCompat.START)
                Toast.makeText(this, "Clientes", Toast.LENGTH_SHORT).show()
            }

            R.id.nav_nutrihome_six -> {
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

    //Función que actualiza los datos de la UI
    private fun actualizarDatosUsuarios(email: String){
        val mail = email
        val docRef = db.collection("nutricionistas").document(mail)

        try {

            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    //Variables que recogen los datos de la base de datos
                    val nombre = document.getString("nombre")
                    val mail = document.getString("mail")

                    //Variables que actualizan los datos en la interfaz del Navheader
                    val nombreUI = findViewById<TextView>(R.id.navheader_nutricionistas_name)
                    val emailUI = findViewById<TextView>(R.id.navheader_nutricionistas_email)

                    //Actualización de los datos en la interfaz del navheader
                    nombreUI.text = nombre
                    emailUI.text = mail

                    //Actualización de los datos en la interfaz del HomeActivity
                    val txtNutriPerfil = findViewById<TextView>(R.id.txt_perfilnutricionistas)
                    txtNutriPerfil.text = "¡Bienvenido a tu perfil ${nombre}! "
                } else {
                    showAlert("No se encontraron datos")
                }
            }.addOnFailureListener { exception ->
               showAlert("Error al obtener datos: $exception")
            }
        }catch (FireBaseException: Exception){
            showAlert( "Error al Actualizar UI: ${FireBaseException.message}")
        }
    }

    //Función que muestra los datos del Nutricionista
    private fun mostrarDatosNutricionista(mail: String) {
        val idNutricionista = mail
        if (idNutricionista != null) {
            // Inicialización de los datos del nutricionista
            recyclerView = findViewById(R.id.recyclerView_perfilnutricionistas)
            recyclerView.layoutManager = LinearLayoutManager(this)
            perfilNutricionistasControlador = PerfilNutricionistasControlador(emptyList())
            recyclerView.adapter = perfilNutricionistasControlador

            try {
                db.collection("nutricionistas").document(idNutricionista).get()
                    .addOnSuccessListener { document ->
                        val nutricionista = document.toObject(Nutricionistas::class.java)
                        if (nutricionista != null) {
                            val datosNutricionista = listOf(
                                "Nombre: ${nutricionista.nombre}",
                                "Mail: ${nutricionista.mail}"
                            )
                            // Actualiza los datos del adaptador después de inicializarlo.
                            perfilNutricionistasControlador.actualizarDatos(datosNutricionista)

                            // Actualizamos la línea que nos muestra el total de clientes en la interfaz
                            db.collection("usuarios").get().addOnSuccessListener { usuarios ->
                                val numClientesTextView = findViewById<TextView>(R.id.total_clientes)
                                numClientesTextView.text = "Clientes Totales: ${usuarios.size()}"
                            }
                        } else {
                            showAlert("No se encontraron datos")
                        }
                    }
                    .addOnFailureListener { e ->
                        showAlert("Error al obtener los datos del usuario: ${e.message}")
                    }
            } catch (e: Exception) {
                showAlert("Error al obtener los datos del usuario: ${e.message}")
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
