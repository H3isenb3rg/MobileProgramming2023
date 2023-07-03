package it.unibs.mp.horace.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.R
import it.unibs.mp.horace.TopLevelFragment
import it.unibs.mp.horace.backend.Settings
import it.unibs.mp.horace.databinding.FragmentHomeBinding


class HomeFragment : TopLevelFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val args: HomeFragmentArgs by navArgs()

    private lateinit var auth: FirebaseAuth
    private lateinit var prefs: Settings

    /**
     * The current volume drawable, depends on whether the volume is enabled or not.
     */
    private val volumeDrawable: Int
        get() = if (prefs.isVolumeOn) {
            R.drawable.baseline_volume_up_24
        } else R.drawable.baseline_volume_off_24

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        prefs = Settings(
            PreferenceManager.getDefaultSharedPreferences(requireContext()), requireContext()
        )

        // If the fragment is reached after a successful auth operation, show a snack bar.
        when (args.source) {
            R.string.source_sign_up -> Snackbar.make(
                view, getString(R.string.signed_up_successfully), Snackbar.LENGTH_SHORT
            ).show()

            R.string.source_sign_in -> Snackbar.make(
                view, getString(R.string.signed_in_successfully), Snackbar.LENGTH_SHORT
            ).show()

            R.string.source_sign_out -> Snackbar.make(
                view, getString(R.string.signed_out_successfully), Snackbar.LENGTH_SHORT
            ).show()
        }

        // Set timer mode from value stored in preferences.
        binding.modeSelector.check(
            if (prefs.isModePomodoro) binding.stopwatch.id else binding.pomodoro.id
        )

        // Change mode in preferences on selector change.
        binding.modeSelector.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) {
                return@addOnButtonCheckedListener
            }
            if (checkedId == binding.stopwatch.id) {
                prefs.switchModeToStopwatch()
            } else {
                prefs.switchModeToPomodoro()
            }
        }

        // Set volume drawable from value stored in preferences.
        binding.volumeToggle.setIconResource(volumeDrawable)

        // Toggle volume in preferences on click.
        binding.volumeToggle.setOnClickListener {
            prefs.toggleVolume()
            binding.volumeToggle.setIconResource(volumeDrawable)
        }

        binding.workGroup.setOnClickListener {
            findNavController().navigate(
                if (auth.currentUser == null) {
                    HomeFragmentDirections.actionGlobalAuth()
                } else {
                    HomeFragmentDirections.actionHomeFragmentToWorkGroupGraph()
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}