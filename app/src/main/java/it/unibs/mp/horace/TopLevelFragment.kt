package it.unibs.mp.horace

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
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.top_level_app_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // When the settings icon is pressed, nav to settings fragment
                if (menuItem.itemId == R.id.settings) {
                    val action = MainNavDirections.actionGlobalSettingsFragment()
                    view.findNavController().navigate(action)
                    return true
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}