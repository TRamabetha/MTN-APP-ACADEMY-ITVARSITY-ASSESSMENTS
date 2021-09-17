package com.example.vimbasafetyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity4 : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        Toast.makeText(this,"Activity Screen Three", Toast.LENGTH_SHORT).show()

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout4)
        val navView: NavigationView = findViewById(R.id.nav_view4)

        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {

            when(it.itemId){

                R.id.nav_home -> {
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_one -> {
                    val intent = Intent(this,MainActivity2::class.java)
                    startActivity(intent)
                }
                R.id.nav_two -> {
                    val intent = Intent(this,MainActivity3::class.java)
                    startActivity(intent)
                }
                R.id.nav_three -> {
                    Toast.makeText(this,"nav three clicked", Toast.LENGTH_SHORT).show()
                }

            }

            true

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(toggle.onOptionsItemSelected(item)){ return true}

        return super.onOptionsItemSelected(item)
    }
}