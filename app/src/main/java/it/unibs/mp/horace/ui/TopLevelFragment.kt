package it.unibs.mp.horace.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.MainNavDirections
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.firebase.UserNotificationManager

/**
 * A fragment that is a root of the navigation graph.
 * In top level fragments the settings icon will be shown in the app bar.
 */
abstract class TopLevelFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // The menu host is the class where the app bar is located
        val menuHost: MenuHost = requireActivity()

        // Adds the menu with the settings icon to the app bar
        menuHost.addMenuProvider(object : MenuProvider {
            @androidx.annotation.OptIn(com.google.android.material.badge.ExperimentalBadgeUtils::class)
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                if (Firebase.auth.currentUser != null) {
                    menuInflater.inflate(R.menu.menu_top_auth, menu)

                    // Badge for new notifications
                    val badge = BadgeDrawable.create(requireActivity())
                    badge.isVisible = false
                    BadgeUtils.attachBadgeDrawable(
                        badge, requireActivity().findViewById(R.id.topAppBar), R.id.notifications
                    )

                    // Show badge on notifications icon if there are new notifications
                    val manager = UserNotificationManager()
                    manager.addOnNotificationListener {
                        badge.isVisible = true
                    }
                } else {
                    menuInflater.inflate(R.menu.menu_top_no_auth, menu)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.settings -> {
                        view.findNavController().navigate(MainNavDirections.actionGlobalSettings())
                        true
                    }

                    R.id.notifications -> {
                        view.findNavController()
                            .navigate(MainNavDirections.actionGlobalNotifications())
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}