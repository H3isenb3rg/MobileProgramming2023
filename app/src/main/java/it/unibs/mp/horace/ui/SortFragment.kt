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
import it.unibs.mp.horace.R

/**
 * A fragment with a sort action in the app bar.
 */
abstract class SortFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // The menu host is the class where the app bar is located
        val menuHost: MenuHost = requireActivity()

        // Adds the menu with the settings icon to the app bar
        menuHost.addMenuProvider(object : MenuProvider {
            @androidx.annotation.OptIn(com.google.android.material.badge.ExperimentalBadgeUtils::class)
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_top_sort, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.sort -> {
                        onSortSelected()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    abstract fun onSortSelected()
}