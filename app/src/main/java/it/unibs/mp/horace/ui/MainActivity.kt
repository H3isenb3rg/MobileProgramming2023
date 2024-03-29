package it.unibs.mp.horace.ui

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Filterable
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ShareCompat
import androidx.core.view.WindowCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.MainNavDirections
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.Settings
import it.unibs.mp.horace.backend.firebase.CurrentUser
import it.unibs.mp.horace.databinding.ActivityMainBinding
import java.time.LocalDateTime

fun Context.shareUserProfile() {
    ShareCompat.IntentBuilder(this).setType("text/plain")
        .setChooserTitle(getString(R.string.share_with))
        .setText(getString(R.string.share_text, CurrentUser().uid)).startChooser()
}

class MainActivity : AppCompatActivity() {
    companion object {
        // Up action won't be shown in the top app bar on these screens.
        val TOP_LEVEL_DESTINATIONS = setOf(
            R.id.homeFragment, R.id.activitiesFragment, R.id.leaderboardFragment
        )

        // The destinations in which quick actions should be shown, if enabled.
        val QUICK_ACTIONS_DESTINATIONS = TOP_LEVEL_DESTINATIONS.union(
            setOf(
                R.id.friendsFragment, R.id.journalFragment
            )
        )
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth

    /**
     * Variable used to hold the current start time of the displayed timer
     */
    var currStartTime: LocalDateTime? = null

    // Application preferences
    private lateinit var settings: Settings

    // Callback to close search view on back button press
    private lateinit var closeSearchViewCallback: OnBackPressedCallback

    private var appBarConfiguration: AppBarConfiguration = AppBarConfiguration(
        TOP_LEVEL_DESTINATIONS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        settings = Settings(this)

        // See https://developer.android.com/codelabs/android-navigation.
        // and https://developer.android.com/guide/navigation/integrations/ui
        val host: NavHostFragment = binding.viewFragmentContainer.getFragment() as NavHostFragment
        navController = host.navController

        // Lay out app behind system bars:
        // https://developer.android.com/develop/ui/views/layout/edge-to-edge#lay-out-in-full-screen
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setUpBottomNavigation()
        setupActionBar()
        setupQuickActions()
        setupSearchView()

        // Apply theme selected in preferences on startup
        switchTheme(settings.theme)
    }


    override fun onSupportNavigateUp(): Boolean {
        // Handle up button navigation
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /**
     * Switches the device's theme.
     */
    fun switchTheme(theme: Settings.Theme) {
        AppCompatDelegate.setDefaultNightMode(
            when (theme) {
                Settings.Theme.Light -> AppCompatDelegate.MODE_NIGHT_NO
                Settings.Theme.Dark -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }

    /**
     * Hooks a search bar to the search view.
     */
    fun hookSearchBar(
        searchBar: SearchBar,
        adapter: RecyclerView.Adapter<*>,
        onOpen: () -> Unit = {},
        onClose: () -> Unit = {}
    ) {
        val adapterFilter = (adapter as Filterable).filter

        // Add the adapter to the recycler view
        binding.recyclerviewSearchContent.adapter = adapter

        binding.searchview.apply {
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
                    onOpen()
                } else if (newState == SearchView.TransitionState.HIDING) {
                    // Clear search text when hiding
                    adapterFilter.filter("")
                    closeSearchViewCallback.isEnabled = false
                    updateQuickActionsVisibility(true)
                    onClose()
                }
            }

            // Handle text changes
            editText.addTextChangedListener {
                adapterFilter.filter(text.toString())
            }

            editText.setOnEditorActionListener { _, _, _ ->
                adapterFilter.filter("")
                true
            }
        }
    }

    /**
     * Sets up action bar to use navigation.
     */
    @androidx.annotation.OptIn(com.google.android.material.badge.ExperimentalBadgeUtils::class)
    private fun setupActionBar() {
        // Set material toolbar as action bar.
        // This is required to use the menu provider.
        setSupportActionBar(binding.toolbar)

        // Adds navigation config to the action bar.
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    /**
     * Adds navigation to the bottom nav.
     */
    private fun setUpBottomNavigation() {
        binding.bottomnavigation.setupWithNavController(navController)
        binding.bottomnavigation.setOnItemSelectedListener { item ->
            val action = when (item.itemId) {
                R.id.activitiesFragment -> MainNavDirections.actionGlobalActivities()
                R.id.leaderboardFragment -> if (auth.currentUser == null) {
                    MainNavDirections.actionGlobalAuth()
                } else {
                    MainNavDirections.actionGlobalLeaderboard()
                }

                else -> MainNavDirections.actionGlobalHome()
            }
            navController.navigate(action)
            true
        }
    }

    /**
     * Sets up quick actions to be shown on certain destinations.
     */
    private fun setupQuickActions() {
        // When the destination changes, update the visibility of the quick actions
        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateQuickActionsVisibility(destination.id in QUICK_ACTIONS_DESTINATIONS)

            val manualLogMargins = (binding.fabManualLog.layoutParams as MarginLayoutParams)

            binding.fabStartTimer.text = if (currStartTime == null) {
                getString(R.string.start_timer)
            } else {
                getString(R.string.stop_timer)
            }

            if (destination.id == R.id.homeFragment) {
                binding.fabStartTimer.hide()
                binding.fabManualLog.size = FloatingActionButton.SIZE_NORMAL
                manualLogMargins.bottomMargin = (16f * resources.displayMetrics.density).toInt()
            } else {
                binding.fabManualLog.size = FloatingActionButton.SIZE_MINI
                manualLogMargins.bottomMargin = (84f * resources.displayMetrics.density).toInt()
            }
        }

        binding.fabManualLog.setOnClickListener {
            navController.navigate(MainNavDirections.actionGlobalManualLog())
        }

        binding.fabStartTimer.setOnClickListener {
            navController.navigate(MainNavDirections.actionGlobalHome(source = R.string.source_quick_action))
        }

        // When the user scrolls down, hide the quick actions.
        // Only show them again when the user scrolls to the very top.
        binding.scrollview.viewTreeObserver.addOnScrollChangedListener {
            if (binding.scrollview.scrollY == 0) {
                updateQuickActionsVisibility(true)
            } else updateQuickActionsVisibility(false)
        }
    }

    /**
     * Updates the visibility of the quick actions.
     * If the user disables quick actions in settings, they will always be hidden.
     */
    private fun updateQuickActionsVisibility(shouldShowActions: Boolean) {
        if (shouldShowActions && settings.isQuickActionsEnabled) {
            binding.fabStartTimer.show()
            binding.fabManualLog.show()
        } else {
            binding.fabStartTimer.hide()
            binding.fabManualLog.hide()
        }
    }

    /**
     * Sets up the global search view,
     * where search results of search bars will be shown.
     */
    private fun setupSearchView() {
        binding.searchview.apply {
            closeSearchViewCallback = onBackPressedDispatcher.addCallback(this@MainActivity) {
                if (isShowing) {
                    hide()
                }
            }

            closeSearchViewCallback.isEnabled = false
        }
    }
}