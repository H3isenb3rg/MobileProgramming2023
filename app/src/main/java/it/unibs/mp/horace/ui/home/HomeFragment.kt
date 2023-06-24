package it.unibs.mp.horace.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import it.unibs.mp.horace.R
import it.unibs.mp.horace.TopLevelFragment
import it.unibs.mp.horace.databinding.FragmentHomeBinding


class HomeFragment : TopLevelFragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val args: HomeFragmentArgs by navArgs()

    private lateinit var prefs: SharedPreferences
    private val volumeOn: Boolean
        get() = prefs.getBoolean("volume_on", false)

    private val volumeDrawable: Int
        get() {
            if (volumeOn) {
                return R.drawable.baseline_volume_up_24
            }

            return R.drawable.baseline_volume_off_24
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())

        // If the fragment is reached after a successful auth operation,
        // show a snack bar
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

        binding.modeSelector.check(prefs.getInt("mode", binding.pomodoro.id))
        binding.modeSelector.addOnButtonCheckedListener { _, checkedId, _ ->
            prefs.edit().putInt("mode", checkedId).apply()
        }

        binding.volumeToggle.setIconResource(volumeDrawable)
        binding.volumeToggle.setOnClickListener {
            prefs.edit().putBoolean("volume_on", !volumeOn).apply()
            binding.volumeToggle.setIconResource(volumeDrawable)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}