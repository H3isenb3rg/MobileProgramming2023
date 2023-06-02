package it.unibs.mp.horace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import it.unibs.mp.horace.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    // Top level destinations.
    // Up action won't be shown in the top app bar on these screens.
    private var appBarConfiguration: AppBarConfiguration = AppBarConfiguration(
        setOf(
            R.id.homeFragment, R.id.historyFragment, R.id.friendsFragment
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lay out app behind system bars:
        // https://developer.android.com/develop/ui/views/layout/edge-to-edge#lay-out-in-full-screen
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // See https://developer.android.com/codelabs/android-navigation.
        // and https://developer.android.com/guide/navigation/integrations/ui
        val host: NavHostFragment = binding.navHostFragment.getFragment() as NavHostFragment
        navController = host.navController

        setUpBottomNavigation()
        setupActionBar()

        // val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        // val theme = prefs.getInt("theme", AppCompatDelegate.MODE_NIGHT_NO)
        // AppCompatDelegate.setDefaultNightMode(theme)
    }

    /**
     * Sets up action bar to use navigation.
     */
    private fun setupActionBar() {
        // Set material toolbar as action bar
        setSupportActionBar(binding.topAppBar)
        setupActionBarWithNavController(navController, appBarConfiguration)


    }

    /**
     * Adds navigation to the bottom nav.
     */
    private fun setUpBottomNavigation() {
        binding.bottomNav.setupWithNavController(navController)
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
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

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}