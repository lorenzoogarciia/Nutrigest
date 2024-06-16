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
import com.example.nutrigest.clases.UsuariosAdapter
import com.example.nutrigest.interfaces.OnItemActionListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore

class ClientesActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    OnItemActionListener {
    //Variables del menú lateral
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    //Variables para el RecyclerView de los clientes
    private lateinit var usuariosAdapter: UsuariosAdapter
    private lateinit var recyclerView: RecyclerView

    //Instancia de la base de datos
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

        //Recuperamos el mail del nutricionista
        val bundle = intent.extras
        val email: String?  = bundle?.getString("mail")

        setup(email.toString())
    }

    //Función que inicializa todos los elementos de la Activity y su backend
    private fun setup(email: String?){
        try {
            //Actualizamos los datos del nutricionista en la interfaz
            actualizarDatosUsuarios(email.toString())
        }catch (e: Exception){
            mostrarAlerta("Error al actualizar datos de usuario: ${e.message}")
        }

        //Mostramos los clientes en la interfaz
        try {
            mostrarClientes()
        }catch (e: Exception){
            mostrarAlerta("Error al mostrar los clientes: ${e.message}")
        }
    }

    //Función que controla los botones del menú lateral
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
                val alimentosIntent = Intent(this, AlimentosActivity::class.java).apply {
                    putExtra("mail", intent.getStringExtra("mail"))
                }
                startActivity(alimentosIntent)
                drawer.closeDrawer(GravityCompat.START)
                Toast.makeText(this, "Alimentos", Toast.LENGTH_SHORT).show()

            }
            R.id.nav_nutrihome_five -> {
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

    //Función que actualiza la interfaz cuando se elimina un usuario
    override fun onItemDeleted() {
        mostrarClientes()
    }

    //Función que actualiza los datos del nutricionista en la interfaz
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
                    val txtClientes = findViewById<TextView>(R.id.txt_clientes)
                    txtClientes.text = "¡Bienvenido ${nombre}, estos son tus clientes! "
                } else {
                    Toast.makeText(this, "No se encontraron clientes", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                mostrarAlerta("Error al obtener datos: ${exception.message}")
            }
        }catch (FireBaseException: Exception){
            mostrarAlerta("Error al Actualizar UI: ${FireBaseException.message}")
        }
    }

    //Función que muestra los clientes en la interfaz
    private fun mostrarClientes() {
        //Obtenemos los clientes de la base de datos y los guardamos en un ArrayList
        db.collection("usuarios").get().addOnSuccessListener { documentos ->
            val usuariosList = ArrayList<Usuarios>()
            for (documento in documentos) {
                val usuario = documento.toObject(Usuarios::class.java)
                usuariosList.add(usuario)
            }
            //Configuramos el adaptador y el RecyclerView de los clientes
            usuariosAdapter = UsuariosAdapter(usuariosList, this, this)
            recyclerView = findViewById(R.id.recyclerView_Clientes)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = usuariosAdapter
        }.addOnFailureListener { exception ->
            mostrarAlerta("Error al mostrar los clientes: ${exception.message}")
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