package it.unibs.mp.horace

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import it.unibs.mp.horace.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.login -> {
                    val intent = Intent(this, AuthActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }
}