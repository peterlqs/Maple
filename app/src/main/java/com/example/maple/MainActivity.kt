package com.example.maple

import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.maple.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Bottom menu
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val mic = findViewById<FloatingActionButton>(R.id.fab)

        bottom.background = null
        bottom.menu.getItem(2).isEnabled = false
        val navController = findNavController(R.id.nav_host_fragment)
        bottom.setupWithNavController(navController)
        mic.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.mainRecommendation)
        }
    }
}