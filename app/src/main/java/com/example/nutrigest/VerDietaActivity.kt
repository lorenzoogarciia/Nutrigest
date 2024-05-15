package com.example.nutrigest

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
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
import com.example.nutrigest.clases.Dietas
import com.example.nutrigest.clases.DietasAdapter
import com.example.nutrigest.interfaces.OnItemActionListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore

class VerDietaActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnItemActionListener {
    //Instancia de la base de datos
    private val db = FirebaseFirestore.getInstance()

    //Variables de la interfaz del menú lateral
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    //Variables para la creación del RecyclerView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_dieta)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_verdieta)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_verdieta)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigationdrawer_nutrihome_open, R.string.navigationdrawer_nutrihome_close)
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view_verdieta)
        navigationView.setNavigationItemSelectedListener(this)


        //Recuperamos el id de la dieta y lo guardamos en una variable
        val idDieta = intent.getStringExtra("idDieta")
        val mailUsuario = intent.getStringExtra("mailUsuario")
        val mailNutricionista = intent.getStringExtra("mailNutricionista")

        Log.d("VerDietaActivity", "Email: $mailUsuario, idDieta: $idDieta, mailNutricionista: $mailNutricionista")

        //Llamamos a la función setup para actualizar los datos del usuario en el Navheader
        setup(idDieta.toString(),mailUsuario.toString(), mailNutricionista.toString())
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setup(idDieta: String?, mailUsuario: String?, mailNutricionista: String?) {
        try {
        actualizarDatosUsuarios(mailNutricionista.toString(), idDieta.toString())
    } catch (e: Exception) {
        showAlert("Error al actualizar datos: ${e.message}")
    }

        // Inicialización del RecyclerView para las dietas
        recyclerView = findViewById(R.id.recyclerView_dietas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Lista para almacenar las dietas obtenidas de Firestore
        val dietasList = mutableListOf<Dietas>()

        // Inicializar el adaptador con la lista vacía inicialmente
        val dietasAdapter = DietasAdapter(dietasList, this, mailUsuario.toString(), idDieta.toString())
        recyclerView.adapter = dietasAdapter

        Log.d("VerDietaActivity", "Email: $mailUsuario, idDieta: $idDieta")

        // Obtener los detalles de la dieta específica
        db.collection("usuarios")
            .document(mailUsuario.toString())
            .collection("dietas")
            .document(idDieta.toString())
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val dieta = documentSnapshot.toObject(Dietas::class.java)
                    dieta?.let {
                        dietasList.add(it)
                        dietasAdapter.notifyDataSetChanged()
                    }
                } else {
                    Log.d("VerDietaActivity", "No se encontró la dieta")
                }
            }
            .addOnFailureListener { e ->
                Log.e("VerDietaActivity", "Error al cargar dietas", e)
            }
    }


    //Función que controla los botones del menú lateral
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_nutrihome_one -> {
                val homeIntent = Intent(this, NutriHomeActivity::class.java).apply{
                    putExtra("mail", intent.getStringExtra("mailNutricionista"))}
                startActivity(homeIntent)
                drawer.closeDrawer(GravityCompat.START)
                Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_nutrihome_two -> {
                val perfilIntent = Intent(this, NutriPerfilActivity::class.java).apply {
                    putExtra("mail", intent.getStringExtra("mailNutricionista"))
                }
                startActivity(perfilIntent)
                drawer.closeDrawer(GravityCompat.START)
                Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_nutrihome_three -> {
                val dietasIntent = Intent(this, NutriDietasActivity::class.java).apply {
                    putExtra("mail", intent.getStringExtra("mailNutricionista"))
                }
                startActivity(dietasIntent)
                drawer.closeDrawer(GravityCompat.START)
                Toast.makeText(this, "Dietas", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_nutrihome_four -> {
                val alimentosIntent = Intent(this, AlimentosActivity::class.java).apply {
                    putExtra("mail", intent.getStringExtra("mailNutricionista"))
                }
                startActivity(alimentosIntent)
                drawer.closeDrawer(GravityCompat.START)
                Toast.makeText(this, "Alimentos", Toast.LENGTH_SHORT).show()

            }
            R.id.nav_nutrihome_five -> {
                val clientesIntent = Intent(this, ClientesActivity::class.java).apply {
                    putExtra("mail", intent.getStringExtra("mailNutricionista"))
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

    //Función para actualizar los datos del usuario en el Navheader
    private fun actualizarDatosUsuarios(email: String?, idDieta: String?){
        val mail = email.toString()
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
                    val txtNutridietas = findViewById<TextView>(R.id.txt_verdieta)
                    txtNutridietas.text = "${idDieta} "
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

    //Función que muestra los mensajes de alerta
    private fun showAlert(mensaje: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onItemDeleted() {
        //Si borramos una dieta volvemos a NutriDietasActivity
        val volverIntent: Intent = Intent(this, NutriDietasActivity::class.java).apply {
            putExtra("mail", intent.getStringExtra("mailNutricionista"))
        }
        startActivity(volverIntent)
    }
}