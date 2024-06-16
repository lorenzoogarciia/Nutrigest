package com.example.nutrigest

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore

class MisDietasActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    //Instancia de la base de datos
    private val db = FirebaseFirestore.getInstance()

    //Variables del menú lateral
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    //Variables para el ListView de las dietas
    private lateinit var dietasUsuarioAdapter: ArrayAdapter<String>
    private val dietasList = arrayListOf<String>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_dietas)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_mis_dietas)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_misdietas)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigationdrawer_home_open, R.string.navigationdrawer_home_close)
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view_misdietas)
        navigationView.setNavigationItemSelectedListener(this)

        //Recuperamos el mail del usuario
        val bundle = intent.extras
        val email: String?  = bundle?.getString("mail")

        setup(email.toString())
        }

    //Función que inicializa todos los elementos de la Activity y su backend
    private fun setup(email: String?){
        try {
            actualizarDatosUsuarios(email.toString())
        }catch (e: Exception){
            mostrarAlerta("Error al actualizar datos: ${e.message}")
        }

        try {
            cargarDietas(email.toString())
        }catch (e: Exception){
            mostrarAlerta("Error al cargar dietas: ${e.message}")
        }

        val listViewDietas: ListView = findViewById(R.id.listViewDietasUsuario)
        //Obtenemos las dietas del usuario
        dietasUsuarioAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dietasList)
        listViewDietas.adapter = dietasUsuarioAdapter

        //Si pulsamos en una dieta, nos lleva a la actividad de ver dieta pasándole el id de la dieta
        listViewDietas.setOnItemClickListener { parent, view, position, id ->
            val idDieta = dietasList[position]  //Obtenemos el id de la dieta seleccionada
            val verDietaintent = Intent(this, VerDietaUsuarioActivity::class.java)
            verDietaintent.putExtra("idDieta", idDieta)  // Pasamos el id de la dieta
            verDietaintent.putExtra("mail", email) //Pasamos el mail del usuario
            startActivity(verDietaintent)
        }


    }

    //Función que controla los botones del menú lateral
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_home_one -> {
                val homeIntent = Intent(this, HomeActivity::class.java).apply {
                    putExtra("mail", intent.getStringExtra("mail"))
                }
                startActivity(homeIntent)
                Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
                drawer.closeDrawer(GravityCompat.START)
            }
            R.id.nav_home_two -> {
                val perfilIntent = Intent(this, PerfilActivity::class.java).apply{
                    putExtra("mail", intent.getStringExtra("mail"))
                }
                startActivity(perfilIntent)
                drawer.closeDrawer(GravityCompat.START)
                Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()

            }
            R.id.nav_home_three -> {
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
                    mostrarAlerta( "Error al cerrar sesión: ${e.message}")
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

    //Función que actualiza los datos del usuario en el Navheader
    private fun actualizarDatosUsuarios(email: String){
        val mail = email
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

                    //Actualización del mensaje de bienvenida
                    val txtMisDietas = findViewById<TextView>(R.id.txt_misdietas)
                    txtMisDietas.text = "¡Bienvenido a tus dietas ${nombre}! "
                } else {
                    mostrarAlerta("No se encontraron datos")
                }
            }.addOnFailureListener { exception ->
                mostrarAlerta("Error al obtener datos: $exception")
            }
        }catch (FireBaseException: Exception){
            mostrarAlerta("Error al Actualizar UI: ${FireBaseException.message}")
        }
    }

    //Función que carga las dietas del usuario
    private fun cargarDietas(mail: String) {
        //Limpiamos la lista de dietas
        dietasList.clear()

        //Obtenemos las dietas del usuario y las cargamos en la lista
        FirebaseFirestore.getInstance().collection("usuarios")
            .document(mail)
            .collection("dietas")
            .get()
            .addOnSuccessListener { documentos ->
                for (documento in documentos) {
                    val dietaId = documento.id
                    dietasList.add(dietaId)
                }
                dietasUsuarioAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar dietas: ${e.message}", Toast.LENGTH_LONG).show()
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
