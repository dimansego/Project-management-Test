package com.example.projectmanagement

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.projectmanagement.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        
        // Set up ActionBar with NavController
        // Top-level destinations: no back arrow will be shown on these screens
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.meetingsFragment,
                R.id.profileFragment,
                R.id.loginFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        
        // Set up BottomNavigationView with NavController
        // Only show bottom nav for main graph destinations
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val mainDestinations = setOf(
                R.id.homeFragment,
                R.id.meetingsFragment,
                R.id.profileFragment
            )
            binding.bottomNavigationView.visibility =
                if (mainDestinations.contains(destination.id)) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            // Show menu only on home fragment
            invalidateOptionsMenu()
        }
        
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val currentDestination = navController.currentDestination

        // Only show menu on home fragment
        if (currentDestination?.id == R.id.homeFragment) {
            menuInflater.inflate(R.menu.home_action_bar, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        return when (item.itemId) {
            R.id.action_add_project -> {
                navController.navigate(R.id.action_homeFragment_to_createProjectFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
