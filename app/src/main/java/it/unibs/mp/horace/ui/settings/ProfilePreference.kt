package it.unibs.mp.horace.ui.settings

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.R
import it.unibs.mp.horace.databinding.ProfileInfoBinding

/**
 * Preference that shows the user profile's info.
 * The info are read from Firebase Authentication.
 *
 * The layout of the preference is specified in preferences.xml.
 */
class ProfilePreference(context: Context, attrs: AttributeSet?) : Preference(context, attrs) {
    private lateinit var binding: ProfileInfoBinding
    private lateinit var auth: FirebaseAuth

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        binding = ProfileInfoBinding.bind(holder.itemView)
        auth = Firebase.auth

        binding.username.text = auth.currentUser?.displayName
        binding.email.text = auth.currentUser?.email

        // If the user has a profile photo use it, otherwise fall back to default
        binding.photo.load(auth.currentUser?.photoUrl ?: R.drawable.default_profile_photo)
    }
}