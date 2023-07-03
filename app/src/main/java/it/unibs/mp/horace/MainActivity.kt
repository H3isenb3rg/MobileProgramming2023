package it.unibs.mp.horace

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var prefs: SharedPreferences

    // Callback to close search view on back button press
    private lateinit var closeSearchViewCallback: OnBackPressedCallback

    // Top level destinations.
    // Up action won't be shown in the top app bar on these screens.
    private val topLevelDestinations = setOf(
        R.id.homeFragment, R.id.historyFragment, R.id.friendsFragment
    )
    private var appBarConfiguration: AppBarConfiguration = AppBarConfiguration(
        topLevelDestinations
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // See https://developer.android.com/codelabs/android-navigation.
        // and https://developer.android.com/guide/navigation/integrations/ui
        val host: NavHostFragment = binding.navHostFragment.getFragment() as NavHostFragment
        navController = host.navController

        // Lay out app behind system bars:
        // https://developer.android.com/develop/ui/views/layout/edge-to-edge#lay-out-in-full-screen
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setUpBottomNavigation()
        setupActionBar()
        setupQuickActions()
        setupSearchView()

        // Apply theme selected in preferences on startup
        switchTheme(
            prefs.getString(
                getString(R.string.preference_theme), resources.getString(R.string.theme_device)
            )
        )
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

    /**
     * Hooks a search bar to the search view.
     */
    fun hookSearchBar(
        searchBar: SearchBar, adapter: RecyclerView.Adapter<*>, onTextChange: (String) -> Unit
    ) {
        // Add the adapter to the recycler view
        binding.searchContent.adapter = adapter

        binding.searchView.apply {
            // Copy the search bar hint to the search view
            setupWithSearchBar(searchBar)
            hint = searchBar.hint

            // Handle back button to close search view
            val closeSearchViewCallback = onBackPressedDispatcher.addCallback(this@MainActivity) {
                if (isShowing) {
                    hide()
                }
            }

            closeSearchViewCallback.isEnabled = false

            addTransitionListener { _, _, newState ->
                if (newState == SearchView.TransitionState.SHOWING) {
                    closeSearchViewCallback.isEnabled = true
                    updateQuickActionsVisibility(false)
                } else if (newState == SearchView.TransitionState.HIDING) {
                    // Clear search text when hiding
                    onTextChange("")
                    closeSearchViewCallback.isEnabled = false
                    updateQuickActionsVisibility(true)
                }
            }

            // Handle text changes
            editText.addTextChangedListener {
                onTextChange(text.toString())
            }

            editText.setOnEditorActionListener { _, _, _ ->
                onTextChange("")
                true
            }
        }
    }

    /**
     * Sets up action bar to use navigation.
     */
    private fun setupActionBar() {
        // Set material toolbar as action bar.
        // This is required to use the menu provider.
        setSupportActionBar(binding.topAppBar)

        // Adds navigation config to the action bar.
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    /**
     * Adds navigation to the bottom nav.
     */
    private fun setUpBottomNavigation() {
        binding.bottomNav.setupWithNavController(navController)
        binding.bottomNav.setOnItemSelectedListener { item ->
            val action = when (item.itemId) {
                R.id.historyFragment -> MainNavDirections.actionGlobalHistory()
                R.id.friendsFragment -> if (auth.currentUser == null) {
                    MainNavDirections.actionGlobalAuth()
                } else {
                    MainNavDirections.actionGlobalFriends()
                }

                else -> MainNavDirections.actionGlobalHome()
            }
            navController.navigate(action)
            true
        }
    }

    private fun setupQuickActions() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateQuickActionsVisibility(destination.id in topLevelDestinations)
        }
    }

    private fun updateQuickActionsVisibility(shouldShowActions: Boolean) {
        val isVisible = shouldShowActions && prefs.getBoolean(
            getString(R.string.preference_quick_actions), false
        )

        binding.startTimer.isVisible = isVisible
        binding.manualAdd.isVisible = isVisible
    }

    private fun setupSearchView() {
        binding.searchView.apply {
            closeSearchViewCallback = onBackPressedDispatcher.addCallback(this@MainActivity) {
                if (isShowing) {
                    hide()
                }
            }

            closeSearchViewCallback.isEnabled = false
        }
    }
}