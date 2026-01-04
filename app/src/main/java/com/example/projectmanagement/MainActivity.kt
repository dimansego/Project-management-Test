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
import kotlinx.coroutines.launch

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
            R.id.action_add_new_project -> {
                navController.navigate(R.id.action_homeFragment_to_createProjectFragment)
                true
            }
            R.id.action_join_project -> {
                showJoinProjectDialog(navController)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun showJoinProjectDialog(navController: NavController) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_join_project, null)
        val codeInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.projectCodeInput)
        
        val dialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle("Join Project")
            .setView(dialogView)
            .setPositiveButton("Join", null)
            .setNegativeButton("Cancel", null)
            .create()
        
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(android.app.Dialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val projectCode = codeInput.text?.toString()?.trim()
                if (!projectCode.isNullOrEmpty()) {
                    val app = application as ProjectApplication
                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                        try {
                            val success = app.syncRepository.joinProject(projectCode)
                            if (success) {
                                android.widget.Toast.makeText(this@MainActivity, "Successfully joined project!", android.widget.Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                                // Refresh the home fragment
                                navController.navigate(R.id.homeFragment)
                            } else {
                                android.widget.Toast.makeText(this@MainActivity, "Failed to join project. Invalid code or already a member.", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            android.widget.Toast.makeText(this@MainActivity, "Error: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    android.widget.Toast.makeText(this, "Please enter a project code", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        dialog.show()
    }

    
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
