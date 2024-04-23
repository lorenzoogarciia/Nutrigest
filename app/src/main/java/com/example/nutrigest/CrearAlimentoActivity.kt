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
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore

class CrearAlimentoActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    //Variables para la creación del menú lateral
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    //Instancia de la base de datos de Firebase
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_alimento)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_crearalimento)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_crearalimento)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigationdrawer_nutrihome_open, R.string.navigationdrawer_nutrihome_close)
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view_crearalimento)
        navigationView.setNavigationItemSelectedListener(this)

        val bundle = intent.extras
        val mail: String?  = bundle?.getString("mail")

        setup(mail.toString())
    }

    private fun setup(email: String?){
        try {
            //Función que actualiza los datos de la UI
            actualizarDatosUsuarios(email.toString())
        }catch (e: Exception){
            showAlert("Error al cargar los datos: ${e.message}")
        }

        //Inicialización de los elementos de la Activity
        val btnCrearAlimento = findViewById<Button>(R.id.btn_guardaralimento)
        val btnVolver = findViewById<Button>(R.id.btn_volveralimento)
        val cmpNombre = findViewById<TextView>(R.id.cmp_nombre_alimento)
        val cmpCantidad = findViewById<TextView>(R.id.cmp_cantidad_alimento)
        val cmpCalorias = findViewById<TextView>(R.id.cmp_calorias_alimento)
        val cmpKilojulios = findViewById<TextView>(R.id.cmp_kilojulios_alimento)
        val cmpHidratos = findViewById<TextView>(R.id.cmp_hidratos_alimento)
        val cmpProteinas = findViewById<TextView>(R.id.cmp_proteinas_alimento)
        val cmpGrasas = findViewById<TextView>(R.id.cmp_grasas_alimento)
        val cmpAzucar = findViewById<TextView>(R.id.cmp_azucares_alimento)
        val cmpFibra = findViewById<TextView>(R.id.cmp_fibra_alimento)
        val cmpSal = findViewById<TextView>(R.id.cmp_sal_alimento)

        //Lógica de los botones
        btnVolver.setOnClickListener {
            val alimentosIntent = Intent(this, AlimentosActivity::class.java).apply {
                putExtra("mail", email)
            }
            startActivity(alimentosIntent)
            Toast.makeText(this, "Alimentos", Toast.LENGTH_SHORT).show()
        }

        btnCrearAlimento.setOnClickListener {
            try {
                //Guardamos las variables que son datos numericos
                val nombre = cmpNombre.text.toString() ?: ""
                val cantidad = cmpCantidad.text.toString().toIntOrNull() ?: 0
                val calorias = cmpCalorias.text.toString().toIntOrNull() ?: 0
                val kiloJulios = cmpKilojulios.text.toString().toDoubleOrNull() ?: 0.0
                val hidratos = cmpHidratos.text.toString().toDoubleOrNull() ?: 0.0
                val proteinas = cmpProteinas.text.toString().toDoubleOrNull() ?: 0.0
                val grasas = cmpGrasas.text.toString().toDoubleOrNull() ?: 0.0
                val azucar = cmpAzucar.text.toString().toDoubleOrNull() ?: 0.0
                val fibra = cmpFibra.text.toString().toDoubleOrNull() ?: 0.0
                val sal = cmpSal.text.toString().toDoubleOrNull() ?: 0.0

                //Comprobamos que los campos Mail y Contraseña no estén vacíos
                if (cmpNombre.text.toString().isNotEmpty() && cmpCalorias.text.toString().isNotEmpty()){

                    //Recogemos los datos de los campos y los guardamos en un HashMap
                    val alimentoData = hashMapOf(
                        "nombre" to nombre,
                        "cantidad" to cantidad,
                        "calorias" to calorias,
                        "kilojulios" to kiloJulios,
                        "hidratos" to hidratos,
                        "proteinas" to proteinas,
                        "grasas" to grasas,
                        "azucar" to azucar,
                        "fibra" to fibra,
                        "sal" to sal)

                    //Guardamos los datos en la base de datos
                    db.collection("alimentos").document(nombre).set(alimentoData)
                        .addOnSuccessListener { document ->
                            Toast.makeText(this, "Alimento guardado correctamente", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            showAlert("Error al guardar el alimento: ${e.message}")
                        }

                }else{
                    showAlert("Por favor, rellene todos los campos")
                }
            }catch (e: FirebaseAuthException){
                showAlert("Error al crear el alimento: ${e.message}")
            }
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
                    val txtNutriPerfil = findViewById<TextView>(R.id.txt_crearalimento)
                    txtNutriPerfil.text = "¡Bienvenido al formulario para crear Alimentos ${nombre}!"
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