package it.unibs.mp.horace

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import it.unibs.mp.horace.databinding.AuthBinding

class Auth : AppCompatActivity() {
    private lateinit var binding: AuthBinding
    // private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // mAuth = FirebaseAuth.getInstance() TODO: Set up Firebase

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
        }
    }
}