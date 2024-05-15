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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrigest.clases.Alimentos
import com.example.nutrigest.clases.AlimentosAdapter
import com.example.nutrigest.interfaces.OnItemActionListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore

class AlimentosActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    OnItemActionListener {
    //Variables para la creación del menú lateral
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    //Variables para la creación del RecyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var alimentosAdapter: AlimentosAdapter

    //Instancia de la base de datos de Firebase
    private val db = FirebaseFirestore.getInstance()
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alimentos)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_alimentos)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_alimentos)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigationdrawer_nutrihome_open, R.string.navigationdrawer_nutrihome_close)
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view_alimentos)
        navigationView.setNavigationItemSelectedListener(this)

        recyclerView = findViewById(R.id.recyclerView_alimentos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val bundle = intent.extras
        val mail: String?  = bundle?.getString("mail")

        setup(mail.toString())
    }

    //Función que inicializa todos los elementos y funciones de la Activity
    private fun setup(email: String?){
        try {
            //Función que actualiza los datos de la UI
            actualizarDatosUsuarios(email.toString())
        }catch (e: Exception){
            showAlert("Error al cargar los datos: ${e.message}")
        }

        //Inicializar botón de crear alimento
        val btnCrearAlimento = findViewById<Button>(R.id.btn_crear_alimento)

        btnCrearAlimento.setOnClickListener {
            val crearAlimentoIntent = Intent(this, CrearAlimentoActivity::class.java).apply {
                putExtra("mail", email)
            }
            startActivity(crearAlimentoIntent)
            Toast.makeText(this, "Crear Alimento", Toast.LENGTH_SHORT).show()
        }


    }

    //Función que actualiza los datos de la UI
    @SuppressLint("SetTextI18n")
    private fun actualizarDatosUsuarios(mail: String){
        val mail = mail
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
                    val txtNutriPerfil = findViewById<TextView>(R.id.txt_alimentos)
                    txtNutriPerfil.text = "¡Bienvenido a los alimentos creados ${nombre}!"

                    //Función que carga los alimentos en la interfaz
                    cargarAlimentos()
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

    //Función que recoge los datos de la base de datos
    private fun cargarAlimentos() {
        db.collection("alimentos").get().addOnSuccessListener { result ->
            val alimentosList = result.map { document ->
                Alimentos(
                    nombre = document.getString("nombre") ?: "",
                    cantidad = document.getDouble("cantidad") ?: 0.0,
                    calorias = document.getDouble("calorias") ?: 0.0,
                    kilojulios = document.getDouble("kilojulios") ?: 0.0,
                    hidratos = document.getDouble("hidratos") ?: 0.0,
                    proteinas = document.getDouble("proteinas") ?: 0.0,
                    grasas = document.getDouble("grasas") ?: 0.0,
                    azucar = document.getDouble("azucar") ?: 0.0,
                    fibra = document.getDouble("fibra") ?: 0.0,
                    sal = document.getDouble("sal") ?: 0.0
                )
            }
            alimentosAdapter = AlimentosAdapter(alimentosList, this)
            recyclerView.adapter = alimentosAdapter
        }.addOnFailureListener { exception ->
            showAlert("Error al obtener datos: $exception")
        }
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

    //Función que maneja la lógica de los elementos del menú lateral
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
                val perfilIntent = Intent(this, NutriPerfilActivity::class.java).apply {
                    putExtra("mail", intent.getStringExtra("mail"))
                }
                startActivity(perfilIntent)
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

    //Función que actualiza los datos de la interfaz al eliminar un alimento
    override fun onItemDeleted() {
        val mail: String = intent.getStringExtra("mail").toString()
        actualizarDatosUsuarios(mail)
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

}