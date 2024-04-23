package com.example.nutrigest

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
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
import com.example.nutrigest.clases.Alimentos
import com.example.nutrigest.clases.Usuarios
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore

class CrearDietaActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    //Instancia de la base de datos
    private val db = FirebaseFirestore.getInstance()

    //Variables de la interfaz del menú lateral
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_dieta)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_creardieta)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_creardieta)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigationdrawer_nutrihome_open, R.string.navigationdrawer_nutrihome_close)
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view_creardieta)
        navigationView.setNavigationItemSelectedListener(this)

        val bundle = intent.extras
        val email: String?  = bundle?.getString("mail")

        setup(email.toString())
    }

    private fun setup(email: String?) {
        //Actualizamos los datos del usuario en la interfaz
        try {
            actualizarDatosUsuarios(email.toString())
        } catch (e: Exception) {
            showAlert("Error al cargar datos: ${e.message}")
        }

        //Inicialización de los botones de la interfaz
        val btnVolver = findViewById<Button>(R.id.btn_volver_dieta)
        val btnGuardarDieta = findViewById<Button>(R.id.btn_guardar_dieta)
        val btnDesayuno = findViewById<Button>(R.id.btn_addDesayuno)
        val btnMediaManana = findViewById<Button>(R.id.btn_addMediaManana)
        val btnAlmuerzo = findViewById<Button>(R.id.btn_addAlmuerzo)
        val btnMerienda = findViewById<Button>(R.id.btn_addMerienda)
        val btnCena = findViewById<Button>(R.id.btn_addCena)

        //Variables para los dos Spinners de la interfaz
        val alimentosSpinner: Spinner = findViewById(R.id.spinnerAlimentos)
        val usuariosSpinner: Spinner = findViewById(R.id.spinnerUsuarios)


        // Cargar alimentos
        val alimentosList = arrayListOf<Alimentos>()

        //Funciones para que los spinners muestren solo el nombre de los alimentos
        val alimentosAdapter = object : ArrayAdapter<Alimentos>(this, R.layout.spinner_creardieta, alimentosList) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.text = getItem(position)?.nombre
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.text = getItem(position)?.nombre
                return view
            }
        }
        alimentosSpinner.adapter = alimentosAdapter

        // Cargar usuarios
        val usuariosList = arrayListOf<Usuarios>()

        //Funciones para que los spinners muestren solo el nombre de los alimentos
        val usuariosAdapter = object : ArrayAdapter<Usuarios>(this, R.layout.spinner_creardieta, usuariosList) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.text = getItem(position)?.nombre
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.text = getItem(position)?.nombre
                return view
            }
        }
        usuariosSpinner.adapter = usuariosAdapter


        // Cargar alimentos desde Firestore al Spinner
        FirebaseFirestore.getInstance().collection("alimentos")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val alimento = document.toObject(Alimentos::class.java)
                    alimentosList.add(alimento)
                }
                alimentosAdapter.notifyDataSetChanged()
            }

        // Cargar usuarios desde Firestore al Spinner
        FirebaseFirestore.getInstance().collection("usuarios")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val usuario = document.toObject(Usuarios::class.java)
                    usuariosList.add(usuario)
                }
                usuariosAdapter.notifyDataSetChanged()
            }

        //Funcionalidad del botón Volver
        btnVolver.setOnClickListener {
            val dietasIntent = Intent(this, NutriDietasActivity::class.java).apply {
                putExtra("mail", intent.getStringExtra("mail"))
            }
            startActivity(dietasIntent)
            Toast.makeText(this, "Volviendo a Dietas", Toast.LENGTH_SHORT).show()
        }

        //Lista de comidas de la dieta
        val dieta = hashMapOf(
            "Desayuno" to mutableListOf<Alimentos>(),
            "Media mañana" to mutableListOf<Alimentos>(),
            "Almuerzo" to mutableListOf<Alimentos>(),
            "Merienda" to mutableListOf<Alimentos>(),
            "Cena" to mutableListOf<Alimentos>()
        )

        //Funcionalidad del botón Añadir a desayuno
        btnDesayuno.setOnClickListener {
            val alimento = alimentosSpinner.selectedItem as Alimentos
            dieta["Desayuno"]?.add(alimento)
            Toast.makeText(this, "Alimento añadido al desayuno", Toast.LENGTH_SHORT).show()
        }

        //Funcionalidad del botón Añadir a media mañana
        btnMediaManana.setOnClickListener {
            val alimento = alimentosSpinner.selectedItem as Alimentos
            dieta["Media mañana"]?.add(alimento)
            Toast.makeText(this, "Alimento añadido a media mañana", Toast.LENGTH_SHORT).show()
        }

        //Funcionalidad del botón crear dieta
        btnGuardarDieta.setOnClickListener {
            // Obtener el usuario seleccionado
            val usuarioSeleccionado = usuariosSpinner.selectedItem as Usuarios
            // Guardar la dieta en Firestore bajo el usuario seleccionado
            FirebaseFirestore.getInstance().collection("usuarios")
                .document(usuarioSeleccionado.mail)
                .collection("dietas")
                .add(dieta)
                .addOnSuccessListener {
                    Toast.makeText(this, "Dieta guardada correctamente para el usuario ${usuarioSeleccionado.nombre}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    //Función que actualiza los datos del usuario en la interfaz
    @SuppressLint("SetTextI18n")
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
                    val txtCrearDieta = findViewById<TextView>(R.id.txt_creardieta)
                    txtCrearDieta.text = "¡Bienvenido a la herramienta para crear Dietas, ${nombre}!"
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

    //Función con la que manejamos los botones del menú lateral
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