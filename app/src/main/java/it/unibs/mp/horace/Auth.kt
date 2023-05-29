package it.unibs.mp.horace

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView.FindListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import it.unibs.mp.horace.databinding.AuthBinding

class Auth : AppCompatActivity() {
    private lateinit var binding: AuthBinding
    private lateinit var topBar: MaterialToolbar
    // private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Top App Bar configs
        binding.appBarLayout.topAppBar.title = getString(R.string.action_sign_in)
        binding.appBarLayout.topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        WindowCompat.setDecorFitsSystemWindows(window, false)

        // mAuth = FirebaseAuth.getInstance() TODO: Set up Firebase

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
        }
    }
}