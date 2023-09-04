package it.unibs.mp.horace.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.Settings
import it.unibs.mp.horace.backend.firebase.models.Activity
import it.unibs.mp.horace.backend.journal.FirestoreJournal
import it.unibs.mp.horace.backend.journal.Journal
import it.unibs.mp.horace.backend.journal.JournalFactory
import it.unibs.mp.horace.backend.journal.JournalMigrator
import it.unibs.mp.horace.backend.journal.RoomJournal
import it.unibs.mp.horace.databinding.FragmentHomeBinding
import it.unibs.mp.horace.ui.TopLevelFragment
import kotlinx.coroutines.launch

class HomeFragment : TopLevelFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var prefs: Settings
    private lateinit var journal: Journal

    private var selectedActivity: Activity? = null

    /**
     * The current volume drawable, depends on whether the volume is enabled or not.
     */
    private val volumeDrawable: Int
        get() = if (prefs.isVolumeEnabled) {
            R.drawable.ic_volume_on
        } else R.drawable.ic_volume_off

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
        journal = JournalFactory(requireContext()).getJournal()

        // If the fragment is reached after a successful auth operation, show a snack bar.
        // Source is not read from navArgs because, for example,
        // if the user signs out from the settings fragment, they are redirected
        // to the home and shown a snackbar. If they then go to Activities, and then press back,
        // the snackbar is shown again.
        // Removing the source argument solves this.
        // See https://stackoverflow.com/questions/62639146/android-navargs-clear-on-back.
        when (HomeFragmentArgs.fromBundle(requireArguments()).source) {
            R.string.source_sign_in -> {
                Snackbar.make(
                    view, getString(R.string.signed_in_successfully), Snackbar.LENGTH_SHORT
                ).show()

                lifecycleScope.launch {
                    showMigrationDialog()
                }
            }

            R.string.source_sign_up -> Snackbar.make(
                view, getString(R.string.signed_up_successfully), Snackbar.LENGTH_SHORT
            ).show()

            R.string.source_sign_out -> {
                Snackbar.make(
                    view, getString(R.string.signed_out_successfully), Snackbar.LENGTH_SHORT
                ).show()

                // Remove activity id from args and preferences.
                arguments?.remove("activityId")
                prefs.activityId = null
            }

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

        // Read activity id from args, fall back to value stored in preferences.
        val activityId =
            HomeFragmentArgs.fromBundle(requireArguments()).activityId ?: prefs.activityId

        // If the id is not null, update the activity in preferences and update the button text.
        if (activityId != null) {
            prefs.activityId = activityId
            lifecycleScope.launch {
                selectedActivity = journal.getActivity(activityId)
                binding.buttonSelectActivity.text =
                    selectedActivity?.name ?: getString(R.string.fragment_home_select_activity)
            }
        }


        // Set timer mode from value stored in preferences.
        binding.togglegroupMode.check(
            if (prefs.isModePomodoro) binding.buttonPomodoro.id else binding.buttonStopwatch.id
        )

        // Change mode in preferences on selector change.
        binding.togglegroupMode.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) {
                return@addOnButtonCheckedListener
            }
            if (checkedId == binding.buttonStopwatch.id) {
                prefs.switchModeToStopwatch()
            } else {
                prefs.switchModeToPomodoro()
            }
        }

        // Set volume drawable from value stored in preferences.
        binding.buttonVolumeToggle.setIconResource(volumeDrawable)

        // Toggle volume in preferences on click.
        binding.buttonVolumeToggle.setOnClickListener {
            prefs.toggleVolume()
            binding.buttonVolumeToggle.setIconResource(volumeDrawable)
        }

        binding.buttonWorkgroup.setOnClickListener {
            findNavController().navigate(
                if (auth.currentUser == null) {
                    HomeFragmentDirections.actionGlobalAuth()
                } else {
                    HomeFragmentDirections.actionHomeFragmentToWorkGroupGraph()
                }
            )
        }

        binding.buttonSelectActivity.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSelectActivityGraph())
        }
    }

    private suspend fun showMigrationDialog() {
        val localJournal = RoomJournal(requireContext())
        if (localJournal.isEmpty()) {
            return
        }

        val remoteJournal = FirestoreJournal()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.fragment_home_migrate_title))
            .setMessage(resources.getString(R.string.fragment_home_migrate_description))
            .setNegativeButton(resources.getString(R.string.fragment_home_migrate_decline)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.fragment_home_migrate_accept)) { dialog, _ ->
                lifecycleScope.launch {
                    JournalMigrator(localJournal, remoteJournal).migrate()
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}