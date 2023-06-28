package it.unibs.mp.horace.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import coil.load
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.CurrentUser
import it.unibs.mp.horace.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        // Add the preferences fragment as a child of this fragment.
        childFragmentManager.beginTransaction()
            .replace(binding.preferencesContainer.id, PreferencesFragment()).commit()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val user: CurrentUser

        try {
            user = CurrentUser()
        } catch (e: IllegalAccessError) {
            binding.profileInfo.visibility = View.GONE
            return
        }

        val pickPhoto =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    user.photoUrl = uri
                    // FIXME: Foto non viene aggiornata immediatamente. Devo uscire e rientrare nella schermata (Riga sotto deve essere eseguita dopo che aggiornamento della foto è completato)
                    binding.photo.load(user.photoUrl ?: R.drawable.default_profile_photo)
                }
            }

        binding.username.text = user.username
        binding.email.text = user.email
        binding.editProfilePhoto.visibility = if (user.provider == CurrentUser.Provider.EMAIL) {
            View.VISIBLE
        } else {
            View.GONE
        }

        binding.editProfilePhoto.setOnClickListener {
            pickPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.photo.load(user.photoUrl ?: R.drawable.default_profile_photo)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}