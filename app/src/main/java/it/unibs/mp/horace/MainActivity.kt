package it.unibs.mp.horace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import it.unibs.mp.horace.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

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
    }

    /**
     * Sets up toolbar to use navigation
     */
    fun setupToolbar(toolbar: Toolbar, hasSettings: Boolean = false) {
        // Set top level destinations.
        // Up action won't be shown in the top app bar on these screens.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.historyFragment,
                R.id.friendsFragment
            )
        )
        toolbar.setupWithNavController(navController, appBarConfiguration)
        if (hasSettings) {
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.settings -> {
                        val action =
                            MainNavDirections.actionGlobalSettingsFragment()
                        navController.navigate(action)
                        true
                    }

                    else -> false
                }
            }
        }
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
}