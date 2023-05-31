package it.unibs.mp.horace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import it.unibs.mp.horace.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // See https://developer.android.com/codelabs/android-navigation.
        val host: NavHostFragment =
            binding.navHostFragment.getFragment() as NavHostFragment? ?: return
        navController = host.navController

        setUpBottomNav()
    }

    /**
     * Sets up the bottom navigation bar to use Navigation
     * and to navigate to the destinations when an item is clicked.
     *
     * For info on the material BottomNavigation component, read the
     * [material docs](https://github.com/material-components/material-components-android/blob/master/docs/components/BottomNavigation.md)
     */
    private fun setUpBottomNav() {
        // Add nav controller to the bottom bar.
        // This is needed because the bar is outside of the NavHostFragment container.
        binding.bottomNav.setupWithNavController(navController)

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    // findNavController should not be used here
                    // as the nav controller is directly available.
                    // Also findNav controller can ONlY be used inside the fragments
                    // (not in the activity) because NavHostFragment has to be a parent.
                    navController.navigate(MainNavDirections.actionGlobalHomeFragment())
                    true
                }

                R.id.history -> {
                    navController.navigate(MainNavDirections.actionGlobalHistoryFragment())
                    true
                }

                R.id.friends -> {
                    navController.navigate(MainNavDirections.actionGlobalFriendsFragment())
                    true
                }

                else -> false
            }
        }
    }
}