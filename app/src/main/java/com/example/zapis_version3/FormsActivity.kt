package com.example.zapis_version3

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.zapis_version3.databinding.ActivityFormsBinding

class FormsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFormsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_forms)

        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_myprofile -> {
                    navController.navigate(R.id.navigation_myprofile)
                    true
                }
                R.id.navigation_zapis -> {
                    navController.navigate(R.id.navigation_zapis)
                    true
                }
                R.id.navigation_ochered -> {
                    navController.navigate(R.id.navigation_ochered)
                    true
                }
                else -> false
            }
        }
    }
}
