package it.unibs.mp.horace.ui.home

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.color.MaterialColors
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
import it.unibs.mp.horace.backend.journal.JournalMigrator
import it.unibs.mp.horace.databinding.FragmentHomeBinding
import it.unibs.mp.horace.ui.MainActivity
import it.unibs.mp.horace.ui.TopLevelFragment
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.ceil
import kotlin.properties.Delegates

class HomeFragment : TopLevelFragment() {

    companion object {
        var POM_PAUSE_END_MILLIS: Long = 30 * 1000
        var POM_WORK_END_MILLIS: Long = 25 * 1000
        var VIBRATE_PATTERN = longArrayOf(300, 500, 300, 500)
    }

    private var _binding: FragmentHomeBinding? = null
    internal val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var prefs: Settings
    private lateinit var journal: Journal
    private lateinit var mainActivity: MainActivity
    private lateinit var timeTextView: TextView

    // The vibrator service.
    private var vibrator: Vibrator? = null

    // The currently selected activity.
    private var selectedActivity: Activity? = null

    // Number of seconds displayed on the stopwatch.
    internal var decs: Int by Delegates.observable(0) { _, _, _ -> updateTimer() }

    // Specifies if the pomodoro is currently in a work or pause section
    internal var isPomodoroPaused: Boolean? by Delegates.observable(null) { _, _, _ -> updatePomodoroSection() }

    // Whether the timer is in pomodoro mode or not.
    internal var isPomodoro: Boolean by Delegates.observable(false) { _, _, _ -> updateMode() }

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
        mainActivity = requireActivity() as MainActivity
        timeTextView = binding.textviewTime

        vibrator = if (Build.VERSION.SDK_INT >= 31) {
            val vibratorManager = getSystemService(requireContext(), VibratorManager::class.java)
            vibratorManager?.defaultVibrator
        } else {
            getSystemService(requireContext(), Vibrator::class.java)
        }

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
                if (_binding == null) {
                    return@launch
                }
                binding.buttonSelectActivity.text =
                    selectedActivity?.name ?: getString(R.string.fragment_home_select_activity)
            }
        }

        setupTimer()

        // Change mode in preferences on selector change.
        binding.togglegroupMode.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) {
                return@addOnButtonCheckedListener
            }
            isPomodoro = checkedId != binding.buttonStopwatch.id
            binding.buttonVolumeToggle.isEnabled = isPomodoro
        }

        // Set timer mode from value stored in preferences.
        binding.togglegroupMode.check(
            if (prefs.isModePomodoro) binding.buttonPomodoro.id else binding.buttonStopwatch.id
        )

        // Set volume drawable from value stored in preferences.
        binding.buttonVolumeToggle.setIconResource(volumeDrawable)
        binding.buttonVolumeToggle.isEnabled = isPomodoro

        // Toggle volume in preferences on click.
        binding.buttonVolumeToggle.setOnClickListener {
            prefs.toggleVolume()
            binding.buttonVolumeToggle.setIconResource(volumeDrawable)
        }

        binding.textViewTimePrompt.text =
            if (mainActivity.currStartTime != null) {
                getString(R.string.fragment_home_tap_to_stop)
            } else {
                getString(R.string.fragment_home_tap_to_start)
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

        binding.cardviewTimer.setOnClickListener {
            startStopTimer(it)
        }

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

            R.string.source_quick_action -> {
                startStopTimer(view)
            }
        }
        arguments?.remove("source")
    }

    private fun startStopTimer(view: View) {
        if (mainActivity.currStartTime != null) {
            lifecycleScope.launch {
                submitEntry(view)
            }

            binding.textViewTimePrompt.text = getString(R.string.fragment_home_tap_to_start)
            mainActivity.currStartTime = null

            return
        }

        binding.textViewTimePrompt.text = getString(R.string.fragment_home_tap_to_stop)
        decs = 0
        mainActivity.currStartTime = LocalDateTime.now()
        if (isPomodoro) {
            // Start pomodoro timer
            isPomodoroPaused = false
        }
    }

    // Sets the Number of seconds on the timer.
    // The runTimer() method uses a Handler to increment the seconds and update the text view.
    private fun setupTimer() {
        // Create a new Handler
        val handler = Looper.myLooper()?.let { Handler(it) }

        // Call the post() method, passing in a new Runnable.
        // The post() method processes code without a delay,
        // so the code in the Runnable will run almost immediately.
        handler?.post(object : Runnable {
            override fun run() {
                if (mainActivity.currStartTime != null) {
                    val millis: Long
                    if (isPomodoro) {
                        val diff = ChronoUnit.MILLIS.between(
                            mainActivity.currStartTime, LocalDateTime.now()
                        )  // TODO: change to minutes
                        val currDiff = diff % POM_PAUSE_END_MILLIS
                        if (currDiff <= POM_WORK_END_MILLIS) {
                            // work section
                            if (isPomodoro && isPomodoroPaused == true) {
                                vibrate()
                            }
                            isPomodoroPaused = false
                            millis = POM_WORK_END_MILLIS - currDiff
                        } else {
                            if (isPomodoro && isPomodoroPaused == false) {
                                vibrate()
                            }
                            isPomodoroPaused = true
                            millis = POM_PAUSE_END_MILLIS - currDiff
                        }
                    } else {
                        millis = ChronoUnit.MILLIS.between(
                            mainActivity.currStartTime, LocalDateTime.now()
                        )
                    }
                    decs = (millis / 100).toInt()
                }

                // Post the code again with a delay of 1 second.
                handler.postDelayed(this, 100)
            }
        })
    }

    private suspend fun submitEntry(view: View) {
        val diff = ChronoUnit.SECONDS.between(
            mainActivity.currStartTime!!, LocalDateTime.now()
        )  // TODO: change to minutes
        journal.addTimeEntry(
            null,
            selectedActivity,
            isPomodoro,
            mainActivity.currStartTime!!,
            LocalDateTime.now(),
            computePoints(diff)
        )
        Snackbar.make(
            view, getString(R.string.fragment_home_time_entry_saved), Snackbar.LENGTH_SHORT
        ).show()
    }

    private suspend fun computePoints(diff: Long): Int {
        var points = diff / 10
        // TODO: Group work bonus
        if (journal.streak() > 1) {
            points += ceil(points * 0.2).toLong()
        }
        return points.toInt()
    }

    private fun updateMode() {
        if (isPomodoro) {
            prefs.switchModeToPomodoro()
        } else {
            prefs.switchModeToStopwatch()
        }
    }

    private fun updatePomodoroSection() {
        if (_binding == null) {
            return
        }

        var backgroundColor = MaterialColors.getColor(
            binding.root, com.google.android.material.R.attr.colorPrimaryContainer
        )
        var strokeColor = MaterialColors.getColor(
            binding.root, com.google.android.material.R.attr.colorPrimary
        )
        var textColor = MaterialColors.getColor(
            binding.root, com.google.android.material.R.attr.colorOnPrimaryContainer
        )

        if (isPomodoroPaused == true) {
            backgroundColor = MaterialColors.getColor(
                binding.root, com.google.android.material.R.attr.colorTertiaryContainer
            )
            strokeColor = MaterialColors.getColor(
                binding.root, com.google.android.material.R.attr.colorTertiary
            )
            textColor = MaterialColors.getColor(
                binding.root, com.google.android.material.R.attr.colorOnTertiaryContainer
            )
        }

        binding.cardviewTimer.setCardBackgroundColor(backgroundColor)
        binding.cardviewTimer.strokeColor = strokeColor
        binding.textviewTime.setTextColor(textColor)
        binding.textViewTimePrompt.setTextColor(textColor)
    }

    private fun updateTimer() {
        if (mainActivity.currStartTime == null) {
            return
        }

        val minutes = (decs / 600) % 60
        val secs = (decs / 10) % 60
        val decSecs = decs % 10

        // Format the seconds into hours, minutes,
        // and seconds.
        timeTextView.text = String.format(
            Locale.getDefault(), "%02d:%02d:%02d", minutes, secs, decSecs
        )
    }

    private fun showMigrationDialog() {
        MaterialAlertDialogBuilder(requireContext()).setTitle(resources.getString(R.string.fragment_home_migrate_title))
            .setMessage(resources.getString(R.string.fragment_home_migrate_description))
            .setNegativeButton(resources.getString(R.string.fragment_home_migrate_decline)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.fragment_home_migrate_accept)) { dialog, _ ->
                lifecycleScope.launch {
                    JournalMigrator.migrateLocalToRemote(requireContext())
                    dialog.dismiss()
                }
            }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal fun vibrate() {
        if (!prefs.isVolumeEnabled) {
            return
        }

        if (Build.VERSION.SDK_INT >= 26) {
            vibrator?.vibrate(VibrationEffect.createWaveform(VIBRATE_PATTERN, -1))
        } else {
            @Suppress("DEPRECATION") vibrator?.vibrate(VIBRATE_PATTERN, -1)
        }
    }
}