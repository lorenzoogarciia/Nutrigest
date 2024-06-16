package com.example.nutrigest

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.nutrigest.clases.Usuarios
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore

class NutriDietasActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    //Instancia de la base de datos
    private val db = FirebaseFirestore.getInstance()

    //Variables de la interfaz del menú lateral
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    //Variables globales de la interfaz
    private lateinit var emailUsuarioActual: String
    private lateinit var usuariosSpinner: Spinner
    private lateinit var dietasAdapter: ArrayAdapter<String>
    private val dietasList = arrayListOf<String>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutri_dietas)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_nutridietas)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_nutridietas)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigationdrawer_nutrihome_open, R.string.navigationdrawer_nutrihome_close)
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view_nutridietas)
        navigationView.setNavigationItemSelectedListener(this)

        //Recuperamos el mail del nutricionista
        val bundle = intent.extras
        val email: String?  = bundle?.getString("mail")

        setup(email.toString())
    }

    //Función que inicializa todos los elementos de la Activity y su backend
    private fun setup(email: String?) {
        //Inicialización de los elementos de la interfaz
        val btnCrearDieta = findViewById<Button>(R.id.btn_crear_dieta)
        usuariosSpinner = findViewById(R.id.spinnerUsuariosNutriDietas)
        val listViewDietas: ListView = findViewById(R.id.listViewDietas)
        // Inicialización del adaptador de dietas
        dietasAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dietasList)
        listViewDietas.adapter = dietasAdapter

        //Actualizamos los datos del usuario en la interfaz
        try {
            actualizarDatosUsuarios(email.toString())
        } catch (e: Exception) {
            mostrarAlerta("Error al cargar datos: ${e.message}")
        }

        //Inicialización de los elementos del Spinner
        try {
            configurarSpinnerUsuarios()
        } catch (e: Exception) {
            mostrarAlerta("Error al cargar usuarios: ${e.message}")
        }

        //Si pulsamos en una dieta, nos lleva a la actividad de ver dieta pasándole el id de la dieta
        listViewDietas.setOnItemClickListener { parent, view, position, id ->
            val idDieta = dietasList[position]
            val verDietaintent = Intent(this, VerDietaActivity::class.java)
            verDietaintent.putExtra("idDieta", idDieta)  //Pasamos el id de la dieta pulsada
            verDietaintent.putExtra("mailUsuario", emailUsuarioActual) //Pasamos el mail del usuario seleccionado
            verDietaintent.putExtra("mailNutricionista", email) //Pasamos el mail del nutricionista
            startActivity(verDietaintent)
        }

        //Lógica del botón para crear dietas
        btnCrearDieta.setOnClickListener {
            val crearDietaIntent = Intent(this, CrearDietaActivity::class.java).apply {
                putExtra("mail", email)
            }
            startActivity(crearDietaIntent)
            Toast.makeText(this, "Crear Dieta", Toast.LENGTH_SHORT).show()
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

    //Función para actualizar los datos del usuario en el Navheader
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
                    val txtNutridietas = findViewById<TextView>(R.id.txt_nutridietas)
                    txtNutridietas.text = "¡Bienvenido ${nombre} estas son las dietas creadas! "
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

    //Función para cargar las dietas de un usuario y mostrarlas en un ListView
    private fun configurarSpinnerUsuarios() {
        //Lista de usuarios
        val usuariosList = arrayListOf<Usuarios>()

        //Adaptador para el Spinner
        val usuariosAdapter = object : ArrayAdapter<Usuarios>(this, android.R.layout.simple_spinner_item, usuariosList) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val label = super.getView(position, convertView, parent) as TextView
                label.text = getItem(position)?.nombre
                return label
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val label = super.getDropDownView(position, convertView, parent) as TextView
                label.text = getItem(position)?.nombre
                return label
            }
        }

        usuariosSpinner.adapter = usuariosAdapter

        //Cargamos los usuarios de la base de datos en el Spinner
        FirebaseFirestore.getInstance().collection("usuarios")
            .get()
            .addOnSuccessListener { documentos ->
                for (documento in documentos) {
                    val usuario = documento.toObject(Usuarios::class.java)
                    usuariosList.add(usuario)
                }
                usuariosAdapter.notifyDataSetChanged()
            }

        //Listener para el Spinner, que carga las dietas del usuario seleccionado
        usuariosSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val usuarioSeleccionado = parent.getItemAtPosition(position) as Usuarios
                emailUsuarioActual = usuarioSeleccionado.mail
                cargarDietas(usuarioSeleccionado.mail)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada
            }
        }

    }

    //Función que carga las dietas de la BBDD del usuario seleccionado en el Spinner
    private fun cargarDietas(mail: String) {
        //Limpiamos la lista de dietas anterior
        dietasList.clear()

        //Cargamos las dietas del usuario en la lista
        FirebaseFirestore.getInstance().collection("usuarios")
            .document(mail)
            .collection("dietas")
            .get()
            .addOnSuccessListener { documentos ->
                for (documento in documentos) {
                    val dietaId = documento.id  // Asumiendo que quieres mostrar el ID de la dieta
                    dietasList.add(dietaId)
                }
                dietasAdapter.notifyDataSetChanged()
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