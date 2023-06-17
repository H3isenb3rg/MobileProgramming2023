package it.unibs.mp.horace.ui.settings

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import coil.load
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.LoggedUser
import it.unibs.mp.horace.databinding.ProfileInfoBinding

/**
 * Preference that shows the user profile's info.
 * The info are read from Firebase Authentication.
 *
 * The layout of the preference is specified in preferences.xml.
 */
class ProfilePreference(context: Context, attrs: AttributeSet?) : Preference(context, attrs) {
    private lateinit var binding: ProfileInfoBinding
    private lateinit var loggedUser: LoggedUser

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        binding = ProfileInfoBinding.bind(holder.itemView)
        loggedUser = LoggedUser()

        binding.username.text = loggedUser.username
        binding.email.text = loggedUser.email

        // If the user has a profile photo use it, otherwise fall back to default
        binding.photo.load(loggedUser.photoURL ?: R.drawable.default_profile_photo)
    }
}