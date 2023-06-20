package it.unibs.mp.horace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
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

        // Apply theme selected in preferences on startup
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        switchTheme(
            prefs.getString(
                getString(R.string.preference_theme), resources.getString(R.string.theme_device)
            )
        )
    }

    /**
     * Sets up action bar to use navigation.
     */
    private fun setupActionBar() {
        // Set material toolbar as action bar.
        // This is required to use the menu provider.
        setSupportActionBar(binding.topAppBar)

        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    /**
     * Adds navigation to the bottom nav.
     */
    private fun setUpBottomNavigation() {
        binding.bottomNav.setupWithNavController(navController)
        binding.bottomNav.setOnItemSelectedListener { item ->
            val action = when (item.itemId) {
                R.id.history -> MainNavDirections.actionGlobalHistoryFragment()
                R.id.friends -> MainNavDirections.actionGlobalFriendsFragment()
                else -> MainNavDirections.actionGlobalHomeFragment(null)
            }
            navController.navigate(action)
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // Handle up button navigation
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /**
     * Switches the device's theme.
     */
    fun switchTheme(theme: String? = resources.getString(R.string.theme_device)) {
        AppCompatDelegate.setDefaultNightMode(
            when (theme) {
                getString(R.string.theme_light) -> AppCompatDelegate.MODE_NIGHT_NO
                getString(R.string.theme_dark) -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }
}