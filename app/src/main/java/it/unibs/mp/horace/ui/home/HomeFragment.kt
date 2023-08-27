package it.unibs.mp.horace.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import it.unibs.mp.horace.backend.journal.Journal
import it.unibs.mp.horace.backend.journal.JournalFactory
import it.unibs.mp.horace.backend.room.LocalDatabase
import it.unibs.mp.horace.backend.room.models.LocalTimer
import it.unibs.mp.horace.databinding.FragmentHomeBinding
import it.unibs.mp.horace.ui.TopLevelFragment
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.properties.Delegates

class HomeFragment : TopLevelFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val database: LocalDatabase by lazy { LocalDatabase.getInstance(requireContext()) }

    private lateinit var auth: FirebaseAuth
    private lateinit var prefs: Settings
    private lateinit var journal: Journal

    private var selectedActivity: Activity? = null

    // Timer related variables
    // Number of seconds displayed
    // on the stopwatch.
    internal var decs: Int by Delegates.observable(0) { _, _, _ -> updateTimer() }
    private var startTime: LocalDateTime? = null

    // Get the text view.
    private lateinit var timeView: TextView
    private lateinit var timeDecsView: TextView

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
        lifecycleScope.launch {
            startTime = getCachedTime()
        }

        return binding.root
    }

    private suspend fun getCachedTime(): LocalDateTime? {
        val rawStartTime = database.timerDao().get(LocalTimer.ID).firstOrNull()
        return if (rawStartTime != null) {
            LocalDateTime.parse(rawStartTime.startTime)
        } else {
            null
        }
    }

    override fun onPause() {
        lifecycleScope.launch {
            if (startTime != null) {
                Log.i("HOME", "Saving current time")
                database.timerDao().insert(LocalTimer(startTime = startTime.toString()))
                Log.i("HOME", "Saved current time on fragment pause")
            } else {
                database.timerDao().delete()
            }
        }

        super.onPause()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        prefs = Settings(requireContext())
        journal = JournalFactory(requireContext()).getJournal()

        timeView = binding.textviewTime
        timeDecsView = binding.textviewTimeDecs

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

                showMigrationDialog()
            }

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

        binding.textviewTimeButton.setOnClickListener {
            if (startTime != null) {
                binding.textViewTimePrompt.text = getString(R.string.fragment_home_tap_to_start)
                // TODO: Save TimeEntry
                startTime = null
            } else {
                decs = 0
                startTime = LocalDateTime.now()
                binding.textViewTimePrompt.text = getString(R.string.fragment_home_tap_to_stop)
            }
        }
        runTimer()
    }

    // Sets the Number of seconds on the timer.
    // The runTimer() method uses a Handler
    // to increment the seconds and
    // update the text view.
    private fun runTimer() {

        // Creates a new Handler
        val handler = Looper.myLooper()?.let { Handler(it) }

        // Call the post() method,
        // passing in a new Runnable.
        // The post() method processes
        // code without a delay,
        // so the code in the Runnable
        // will run almost immediately.
        handler?.post(object : Runnable {
            override fun run() {
                if (startTime != null) {
                    val millis = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now())
                    decs = (millis / 100).toInt()
                }

                // Post the code again
                // with a delay of 1 second.
                handler.postDelayed(this, 100)
            }
        })
    }

    private fun updateTimer() {
        if (startTime == null) {
            return
        }

        val minutes = (decs / 600) % 60
        val secs = (decs / 10) % 60
        val decSecs = decs % 10

        // Format the seconds into hours, minutes,
        // and seconds.
        val time = String.format(
            Locale.getDefault(),
            "%d:%02d",
            minutes, secs
        )
        val decsTime = String.format(
            Locale.getDefault(),
            ".%01d",
            decSecs
        )

        timeView.text = time
        timeDecsView.text = decsTime
    }

    private fun showMigrationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.fragment_home_migrate_title))
            .setMessage(resources.getString(R.string.fragment_home_migrate_description))
            .setNegativeButton(resources.getString(R.string.fragment_home_migrate_decline)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.fragment_home_migrate_accept)) { dialog, _ ->
                lifecycleScope.launch {
                    JournalFactory(requireContext()).migrateLocalJournal()
                    dialog.dismiss()
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}