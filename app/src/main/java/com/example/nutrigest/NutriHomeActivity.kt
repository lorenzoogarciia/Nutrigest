package com.example.nutrigest


import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NutriHomeActivity : AppCompatActivity(),  NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    //Instancia de la BBDD
    //val db = FirebaseFirestore.getInstance()

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutrihome)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_nutrihome)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_nutrihome)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigationdrawer_nutrihome_open, R.string.navigationdrawer_nutrihome_close)
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view_nutricionistas)
        navigationView.setNavigationItemSelectedListener(this)

        //Setup
        val bundle = intent.extras
        val email: String?  = bundle?.getString("mail")

        setup(email.toString())
    }

    private fun setup(email: String?){
        //Inicializamos los objetos en pantalla
        val txtNutrihome = findViewById<TextView>(R.id.txt_nutrihome)

        }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_nutrihome_one -> {
                Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
                drawer.closeDrawer(GravityCompat.START)
            }
            R.id.nav_nutrihome_two -> {
                Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_nutrihome_three -> {
                Toast.makeText(this, "Dietas", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_nutrihome_four -> {
                Toast.makeText(this, "Clientes", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_nutrihome_five -> {
                FirebaseAuth.getInstance().signOut()
                onBackPressed()
                Toast.makeText(this, "Sesión Cerrada Correctamente", Toast.LENGTH_SHORT).show()
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
}