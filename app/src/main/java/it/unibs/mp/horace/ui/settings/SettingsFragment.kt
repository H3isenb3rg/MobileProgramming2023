package it.unibs.mp.horace.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.R

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var auth: FirebaseAuth

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        auth = Firebase.auth

        setPreferencesFromResource(R.xml.preferences, rootKey)

        val authPref = findPreference<Preference>("auth")!!
        authPref.setOnPreferenceClickListener {
            this.findNavController()
                .navigate(SettingsFragmentDirections.actionSettingsFragmentToAuthGraph())
            true
        }

        val logoutPref = findPreference<Preference>("logout")!!
        logoutPref.isVisible = (auth.currentUser != null)
        logoutPref.setOnPreferenceClickListener {
            auth.signOut()
            findNavController().navigate(SettingsFragmentDirections.actionGlobalHomeFragment())
            true
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        // if (key == "theme") {
        // val newTheme =
        //     sharedPreferences.getInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        // AppCompatDelegate.setDefaultNightMode(newTheme)
        // }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences
            ?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
            ?.unregisterOnSharedPreferenceChangeListener(this)
    }
}