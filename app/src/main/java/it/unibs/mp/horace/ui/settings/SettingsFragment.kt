package it.unibs.mp.horace.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.firebase.CurrentUser
import it.unibs.mp.horace.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch

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
        if (Firebase.auth.currentUser == null) {
            binding.profileInfo.visibility = View.GONE
            return
        }
        val user = CurrentUser()

        val pickPhoto =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    user.photoUrl = uri
                    lifecycleScope.launch {
                        user.update()
                        binding.photo.load(user.photoUrl ?: R.drawable.ic_default_profile_photo)
                    }
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
        binding.photo.load(user.photoUrl ?: R.drawable.ic_default_profile_photo)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}