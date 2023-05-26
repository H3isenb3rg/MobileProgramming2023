package it.unibs.mp.timetracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {
    lateinit var topAppBar: MaterialToolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        topAppBar = findViewById(R.id.topAppBar)
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.login -> {
                    val intent = Intent(this, Auth::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}