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
import com.example.nutrigest.clases.Usuarios
import com.example.nutrigest.clases.UsuariosControlador
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore

class ClientesActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var usuariosControlador: UsuariosControlador
    private lateinit var recyclerView: RecyclerView

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clientes)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_clientes)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_clientes)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigationdrawer_nutrihome_open, R.string.navigationdrawer_nutrihome_close)
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view_clientes)
        navigationView.setNavigationItemSelectedListener(this)

        val bundle = intent.extras
        val email: String?  = bundle?.getString("mail")

        setup(email.toString())
    }

    private fun setup(email: String?){
        try {
            actualizarDatosUsuarios(email.toString())
        }catch (e: Exception){
            Toast.makeText(this, "Error al actualizar datos de usuario: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        try {
            mostrarClientes()
        }catch (e: Exception){
            showAlert("Error al mostrar los clientes: ${e.message}")
        }
    }

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
                Toast.makeText(this, "Clientes", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_nutrihome_five -> {
                try {
                    FirebaseAuth.getInstance().signOut()
                    val loginIntent = Intent(this, LoginActivity::class.java)
                    loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(loginIntent)
                    Toast.makeText(this, "Sesión Cerrada Correctamente", Toast.LENGTH_SHORT).show()
                }catch (e: FirebaseAuthException){
                    Toast.makeText(this, "Error al cerrar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
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
                    val txtClientes = findViewById<TextView>(R.id.txt_clientes)
                    txtClientes.text = "¡Bienvenido ${nombre} estos son tus clientes! "
                } else {
                    Toast.makeText(this, "No se encontraron datos", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Error al obtener datos: $exception", Toast.LENGTH_SHORT).show()
            }
        }catch (FireBaseException: Exception){
            Toast.makeText(this, "Error al Actualizar UI: ${FireBaseException.message}", Toast.LENGTH_SHORT).show()
        }
    }

    //Función que muestra los clientes en la interfaz
    private fun mostrarClientes() {
        db.collection("usuarios").get().addOnSuccessListener { result ->
            val usuariosList = ArrayList<Usuarios>()
            for (document in result) {
                val usuario = document.toObject(Usuarios::class.java)
                usuariosList.add(usuario)
            }
            usuariosControlador = UsuariosControlador(usuariosList)
            // Mueve estas líneas aquí, después de que usuariosControlador ha sido inicializado.
            recyclerView = findViewById(R.id.recyclerView_Clientes)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = usuariosControlador
        }.addOnFailureListener { exception ->
            showAlert("Error al mostrar los clientes: ${exception.message}")
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
}