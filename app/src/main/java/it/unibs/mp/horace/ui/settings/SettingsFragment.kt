package it.unibs.mp.horace.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.MainActivity
import it.unibs.mp.horace.R

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var auth: FirebaseAuth
    private val isLoggedIn: Boolean get() = (auth.currentUser != null)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        auth = Firebase.auth

        val profilePref = findPreference<Preference>("profileInfo")!!
        profilePref.isVisible = isLoggedIn

        val authPref = findPreference<Preference>("auth")!!
        authPref.isVisible = !isLoggedIn
        authPref.setOnPreferenceClickListener {
            this.findNavController()
                .navigate(SettingsFragmentDirections.actionSettingsFragmentToAuthGraph())
            true
        }

        val logoutPref = findPreference<Preference>("logout")!!
        logoutPref.isVisible = isLoggedIn
        logoutPref.setOnPreferenceClickListener {
            auth.signOut()
            findNavController().navigate(SettingsFragmentDirections.actionGlobalHomeFragment())
            true
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            "theme" -> (requireActivity() as MainActivity).switchTheme(
                sharedPreferences.getString(
                    "theme", resources.getString(R.string.default_theme)
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }
}