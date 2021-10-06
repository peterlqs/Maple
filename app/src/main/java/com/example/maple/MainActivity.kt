package com.example.maple

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Bottom menu
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val mic = findViewById<FloatingActionButton>(R.id.fab)

        // Set background to null so it turns white
        bottom.background = null
        // Set the third item ( getItem(2) ) so when u press it doesnt create ripple ( the place where it dent )
        bottom.menu.getItem(2).isEnabled = false
        // Set up navigation
        val navController = findNavController(R.id.nav_host_fragment)
        bottom.setupWithNavController(navController)
        // Set up the middle circle button ( fab ) to always navigate to MainRecommendation
        mic.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.mainRecommendation)
        }
    }
}