package com.example.nutrigest

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
import com.example.nutrigest.clases.PerfilUsuariosAdapter
import com.example.nutrigest.clases.Usuarios
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore

class PerfilActivity : AppCompatActivity(),  NavigationView.OnNavigationItemSelectedListener {
    //Variables para el ReciclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var perfilUsuariosAdapter: PerfilUsuariosAdapter

    //Variables para el Navigation Drawer
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    //Instancia de la base de datos
    private val db = FirebaseFirestore.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)



        // Configuración del Navigation Drawer
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_perfilusuarios)
        setSupportActionBar(toolbar)

        // Configuración del Navigation Drawer
        drawer = findViewById(R.id.drawer_perfilusuarios)
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
        val email: String?  = bundle?.getString("mail")

        // Llamamos al método setup para configurar la interfaz
        setup(email.toString())

    }

    //Método que configura la interfaz
    private fun setup(email: String?){
        try {
            actualizarDatosUsuarios(email.toString())
        }catch (e: FirebaseAuthException){
            showAlert("Error al obtener datos: ${e.message}")
        }

        try {
            mostrarDatosUsuario(email.toString())
        }catch (e: FirebaseAuthException){
            showAlert("Error al obtener datos: ${e.message}")
        }

        val btnEditarPerfil = findViewById<TextView>(R.id.btn_editar_perfil)

        btnEditarPerfil.setOnClickListener {
            val editarPerfilIntent = Intent(this, EditarPerfilActivity::class.java).apply {
                putExtra("mail", email)
            }
            startActivity(editarPerfilIntent)
            Toast.makeText(this, "Editar Perfil", Toast.LENGTH_SHORT).show()
        }
    }

    //Función que controla la navegación del Navigation Drawer
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

    //Método para actualizar los datos de los usuarios en la interfaz
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
                    val txtPerfil = findViewById<TextView>(R.id.txt_perfilusuarios)
                    txtPerfil.text = "¡Bienvenido a tu perfil ${nombre}! "
                } else {
                    showAlert("No se encontraron datos")
                }
            }.addOnFailureListener { exception ->
                showAlert( "Error al obtener datos: $exception")
            }
        }catch (FireBaseException: Exception){
            showAlert("Error al Actualizar UI: ${FireBaseException.message}")
        }
    }

    //Función que muestra los datos del Usuario
    private fun mostrarDatosUsuario(mail: String) {
        val idUsuario = mail
        if (idUsuario != null) {
            // Inicializa aquí, asegurándote de que se hace solo una vez.
            recyclerView = findViewById(R.id.recyclerView_perfilusuarios)
            recyclerView.layoutManager = LinearLayoutManager(this)
            perfilUsuariosAdapter = PerfilUsuariosAdapter(emptyList())
            recyclerView.adapter = perfilUsuariosAdapter

            try {
                db.collection("usuarios").document(idUsuario).get()
                    .addOnSuccessListener { document ->
                        val usuario = document.toObject(Usuarios::class.java)
                        if (usuario != null) {
                            val datosUsuario = listOf(
                                "Nombre: ${usuario.nombre}",
                                "Edad: ${usuario.edad} años",
                                "Género: ${usuario.genero}",
                                "Altura: ${usuario.altura} cm",
                                "Peso Actual: ${usuario.peso} kg",
                                "Peso Objetivo: ${usuario.pesoObjetivo} kg",
                                "Actividad: ${usuario.actividad}",
                                "Mail: ${usuario.mail}",
                                "Teléfono: ${usuario.telefono}",
                                "Dirección: ${usuario.address}"
                            )
                            // Actualiza los datos del adaptador después de inicializarlo.
                            perfilUsuariosAdapter.actualizarDatos(datosUsuario)
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

    override fun onPostCreate(savedInstanceState: Bundle?) {
            super.onPostCreate(savedInstanceState)
            toggle.syncState()
        }

        override fun onConfigurationChanged(newConfig: Configuration) {
            super.onConfigurationChanged(newConfig)
            toggle.onConfigurationChanged(newConfig)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            if (toggle.onOptionsItemSelected(item)) {
                return true
            }
            return super.onOptionsItemSelected(item)
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

