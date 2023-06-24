package it.unibs.mp.horace.ui.home

import android.content.SharedPreferences
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
import it.unibs.mp.horace.databinding.FragmentHomeBinding


class HomeFragment : TopLevelFragment() {
    companion object {
        const val MODE_POMODORO = 0
        const val MODE_STOPWATCH = 1
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val args: HomeFragmentArgs by navArgs()

    private lateinit var auth: FirebaseAuth
    private lateinit var prefs: SharedPreferences

    /**
     * The timer mode. Has to be one of `MODE_POMODORO` or `MODE_STOPWATCH`.
     * The default is `MODE_POMODORO`.
     */
    private var mode: Int
        get() = prefs.getInt(getString(R.string.preference_mode), MODE_POMODORO)
        set(value) {
            prefs.edit().putInt(getString(R.string.preference_mode), value).apply()
        }

    /**
     * Whether the volume is enabled or not.
     * The default is disabled.
     */
    private var isVolumeOn: Boolean
        get() = prefs.getBoolean(getString(R.string.preference_volume_on), false)
        set(value) {
            prefs.edit().putBoolean("volume_on", value).apply()
        }

    /**
     * The current volume drawable, depends on whether the volume is enabled or not.
     */
    private val volumeDrawable: Int
        get() = if (isVolumeOn) {
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
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())

        // If the fragment is reached after a successful auth operation, show a snack bar.
        when (args.source) {
            resources.getString(R.string.sign_up) -> Snackbar.make(
                view, getString(R.string.signed_up_successfully), Snackbar.LENGTH_SHORT
            ).show()

            resources.getString(R.string.sign_in) -> Snackbar.make(
                view, getString(R.string.signed_in_successfully), Snackbar.LENGTH_SHORT
            ).show()

            resources.getString(R.string.sign_out) -> Snackbar.make(
                view, getString(R.string.signed_out_successfully), Snackbar.LENGTH_SHORT
            ).show()
        }

        // Set timer mode from value stored in preferences.
        binding.modeSelector.check(
            if (mode == MODE_STOPWATCH) binding.stopwatch.id else binding.pomodoro.id
        )

        // Change mode in preferences on selector change.
        binding.modeSelector.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                mode = if (checkedId == binding.stopwatch.id) MODE_STOPWATCH else MODE_POMODORO
            }
        }

        // Set volume drawable from value stored in preferences.
        binding.volumeToggle.setIconResource(volumeDrawable)

        // Toggle volume in preferences on click.
        binding.volumeToggle.setOnClickListener {
            isVolumeOn = !isVolumeOn
            binding.volumeToggle.setIconResource(volumeDrawable)
        }

        binding.workGroup.setOnClickListener {
            if (auth.currentUser == null) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAuthGraph())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}