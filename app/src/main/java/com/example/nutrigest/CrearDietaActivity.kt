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
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
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

        //Recuperamos el mail del nutricionista
        val bundle = intent.extras
        val email: String?  = bundle?.getString("mail")

        setup(email.toString())
    }

    //Función que inicializa todos los elementos de la Activity y su backend
    private fun setup(email: String?) {
        //Actualizamos los datos del usuario en la interfaz
        try {
            actualizarDatosUsuarios(email.toString())
        } catch (e: Exception) {
            mostrarAlerta("Error al cargar datos: ${e.message}")
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


        //Variable de la lista de alimentos
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

        // Cargamos los usuarios en el spinner
        val usuariosList = arrayListOf<Usuarios>()

        //Funciones para que los spinners muestren solo el nombre de los usuarios
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


        //Obtenemos los alimentos de la base de datos y los añadimos a la lista
        FirebaseFirestore.getInstance().collection("alimentos")
            .get()
            .addOnSuccessListener { documentos ->
                for (documento in documentos) {
                    val alimento = documento.toObject(Alimentos::class.java)
                    alimentosList.add(alimento)
                }
                alimentosAdapter.notifyDataSetChanged()
            }

        //Obtenemos los usuarios de la base de datos y los añadimos a la lista
        FirebaseFirestore.getInstance().collection("usuarios")
            .get()
            .addOnSuccessListener { documentos ->
                for (documento in documentos) {
                    val usuario = documento.toObject(Usuarios::class.java)
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

        //Funcionalidad del botón Añadir a almuerzo
        btnAlmuerzo.setOnClickListener {
            val alimento = alimentosSpinner.selectedItem as Alimentos
            dieta["Almuerzo"]?.add(alimento)
            Toast.makeText(this, "Alimento añadido a almuerzo", Toast.LENGTH_SHORT).show()
        }

        //Funcionalidad del botón Añadir a merienda
        btnMerienda.setOnClickListener {
            val alimento = alimentosSpinner.selectedItem as Alimentos
            dieta["Merienda"]?.add(alimento)
            Toast.makeText(this, "Alimento añadido a merienda", Toast.LENGTH_SHORT).show()
        }

        //Funcionalidad del botón Añadir a cena
        btnCena.setOnClickListener {
            val alimento = alimentosSpinner.selectedItem as Alimentos
            dieta["Cena"]?.add(alimento)
            Toast.makeText(this, "Alimento añadido a cena", Toast.LENGTH_SHORT).show()
        }

        //Funcionalidad del botón crear dieta
        btnGuardarDieta.setOnClickListener {
            // Obtenemos el usuario seleccionado
            val usuarioSeleccionado = usuariosSpinner.selectedItem as Usuarios

            //Creamos id de la dieta para el usuario Seleccionado
            val idDieta = usuarioSeleccionado.crearIdDieta(usuarioSeleccionado.nombre)

            // Guardamos la dieta en la base de datos al usuario seleccionado
            FirebaseFirestore.getInstance().collection("usuarios")
                .document(usuarioSeleccionado.mail)
                .collection("dietas")
                .document(idDieta)
                .set(dieta)
                .addOnSuccessListener {
                    Toast.makeText(this, "$idDieta guardada correctamente", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    mostrarAlerta("Error al guardar dieta: ${e.message}")
                }
        }
    }

    //Función que actualiza los datos del usuario en la interfaz
    @SuppressLint("SetTextI18n")
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
                    val txtCrearDieta = findViewById<TextView>(R.id.txt_creardieta)
                    txtCrearDieta.text = "¡Bienvenido a la herramienta para crear Dietas, ${nombre}!"
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