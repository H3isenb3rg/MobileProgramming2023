package it.unibs.mp.horace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationBarView
import it.unibs.mp.horace.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    findNavController(item.itemId).navigate(MainNavDirections.actionGlobalHomeFragment())
                    true
                }

                R.id.history -> {
                    findNavController(item.itemId).navigate(MainNavDirections.actionGlobalHistoryFragment())
                    true
                }

                R.id.friends -> {
                    findNavController(item.itemId).navigate(MainNavDirections.actionGlobalFriendsFragment())
                    true
                }

                else -> false
            }
        }
    }
}