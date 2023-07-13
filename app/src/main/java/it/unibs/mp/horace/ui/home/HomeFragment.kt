package it.unibs.mp.horace.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.Settings
import it.unibs.mp.horace.databinding.FragmentHomeBinding
import it.unibs.mp.horace.ui.TopLevelFragment


class HomeFragment : TopLevelFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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
        prefs = Settings(requireContext())

        // If the fragment is reached after a successful auth operation, show a snack bar.
        // Source is not read from navArgs because, for example,
        // if the user signs out from the settings fragment, they are redirected
        // to the home and shown a snackbar. If they then go to Activities, and then press back,
        // the snackbar is shown again.
        // Removing the source argument solves this.
        // See https://stackoverflow.com/questions/62639146/android-navargs-clear-on-back.
        when (HomeFragmentArgs.fromBundle(requireArguments()).source) {
            R.string.source_sign_in -> Snackbar.make(
                view, getString(R.string.signed_in_successfully), Snackbar.LENGTH_SHORT
            ).show()

            R.string.source_sign_up -> Snackbar.make(
                view, getString(R.string.signed_up_successfully), Snackbar.LENGTH_SHORT
            ).show()

            R.string.source_sign_out -> Snackbar.make(
                view, getString(R.string.signed_out_successfully), Snackbar.LENGTH_SHORT
            ).show()

            R.string.source_friend_request -> Snackbar.make(
                view, getString(R.string.friend_request_successful), Snackbar.LENGTH_SHORT
            ).show()
        }
        arguments?.remove("source")

        val uid = HomeFragmentArgs.fromBundle(requireArguments()).uid
        if (uid != null && auth.currentUser != null) {
            arguments?.remove("source")

            findNavController().navigate(
                HomeFragmentDirections.actionGlobalUserDetails(uid)
            )
        }


        // Set timer mode from value stored in preferences.
        binding.modeSelector.check(
            if (prefs.isModePomodoro) binding.pomodoro.id else binding.stopwatch.id
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