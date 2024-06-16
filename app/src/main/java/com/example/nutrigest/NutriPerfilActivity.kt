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
import com.example.nutrigest.clases.PerfilNutricionistasAdapter
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore

class NutriPerfilActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    //Variables para el ReciclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var perfilNutricionistasAdapter: PerfilNutricionistasAdapter

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

        //Llamamos a la función setup
        setup(mail.toString())
    }

    //Función que inicializa todos los elementos y funciones de la Activity
    private fun setup(email: String?){
        //Función que actualiza los datos de la interfaz
        try {
            actualizarDatosUsuarios(email.toString())
        }catch (FireBaseException: Exception){
            mostrarAlerta("Error al Actualizar UI: ${FireBaseException.message}")
        }

        //Función que muestra los datos del Nutricionista
        try {
            mostrarDatosNutricionista(email.toString())
        }catch (FireBaseException: Exception){
            mostrarAlerta("Error al Mostrar Datos del Nutricionista: ${FireBaseException.message}")
        }
    }

    //Función que maneja los botones del menú lateral
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

    //Función que actualiza los datos de la UI
    private fun actualizarDatosUsuarios(email: String){
        val mail = email
        val mailNutricionista = db.collection("nutricionistas").document(mail)

        try {

            mailNutricionista.get().addOnSuccessListener { documento ->
                if (documento != null) {
                    //Variables que recogen los datos de la base de datos
                    val nombre = documento.getString("nombre")
                    val mail = documento.getString("mail")

                    //Variables que actualizan los datos en la interfaz del Navheader
                    val nombreUI = findViewById<TextView>(R.id.navheader_nutricionistas_name)
                    val emailUI = findViewById<TextView>(R.id.navheader_nutricionistas_email)

                    //Actualización de los datos en la interfaz del navheader
                    nombreUI.text = nombre
                    emailUI.text = mail

                    //Actualización del mensaje de bienvenida
                    val txtNutriPerfil = findViewById<TextView>(R.id.txt_perfilnutricionistas)
                    txtNutriPerfil.text = "¡Bienvenido a tu perfil ${nombre}! "
                } else {
                    mostrarAlerta("No se encontraron datos")
                }
            }.addOnFailureListener { exception ->
               mostrarAlerta("Error al obtener datos: $exception")
            }
        }catch (FireBaseException: Exception){
            mostrarAlerta( "Error al Actualizar UI: ${FireBaseException.message}")
        }
    }

    //Función que muestra los datos del Nutricionista
    private fun mostrarDatosNutricionista(mail: String) {
        val idNutricionista = mail
        if (idNutricionista != null) {
            // Inicializamos el adaptador y el RecyclerView
            recyclerView = findViewById(R.id.recyclerView_perfilnutricionistas)
            recyclerView.layoutManager = LinearLayoutManager(this)
            perfilNutricionistasAdapter = PerfilNutricionistasAdapter(emptyList())
            recyclerView.adapter = perfilNutricionistasAdapter

            try {
                // Obtenemos los datos del nutricionista de la BBDD
                db.collection("nutricionistas").document(idNutricionista).get()
                    .addOnSuccessListener { documento ->
                        val nutricionista = documento.toObject(Nutricionistas::class.java)
                        if (nutricionista != null) {
                            val datosNutricionista = listOf(
                                "Nombre: ${nutricionista.nombre}",
                                "Mail: ${nutricionista.mail}"
                            )
                            // Actualizamos los datos del adaptador después de inicializarlo.
                            perfilNutricionistasAdapter.actualizarDatos(datosNutricionista)

                            // Actualizamos la línea que nos muestra el total de clientes en la interfaz
                            db.collection("usuarios").get().addOnSuccessListener { usuarios ->
                                val numClientesTextView = findViewById<TextView>(R.id.total_clientes)
                                numClientesTextView.text = "Clientes Totales: ${usuarios.size()}"
                            }
                        } else {
                            mostrarAlerta("No se encontraron datos")
                        }
                    }
                    .addOnFailureListener { e ->
                        mostrarAlerta("Error al obtener los datos del usuario: ${e.message}")
                    }
            } catch (e: Exception) {
                mostrarAlerta("Error al obtener los datos del usuario: ${e.message}")
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
