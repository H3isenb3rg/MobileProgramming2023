package it.unibs.mp.horace.ui.manuallog

import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import it.unibs.mp.horace.databinding.FragmentManualLogBinding
import it.unibs.mp.horace.models.Activity
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

class ManualLogFragment : Fragment() {
    companion object {
        const val DATE_PICKER_TAG = "datePicker"
        const val START_TIME_PICKER_TAG = "startTimePicker"
        const val END_TIME_PICKER_TAG = "endTimePicker"
    }

    private var _binding: FragmentManualLogBinding? = null
    private val binding get() = _binding!!

    // All the validation logic is in the ViewModel
    private val viewModel: ManualLogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManualLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Create the adapter for the activity autocomplete
        val activities: MutableList<Activity> = mutableListOf()
        val adapter = ActivitiesAdapter(
            requireContext(), activities
        ) { activity -> viewModel.activity = activity }

        lifecycleScope.launch {
            activities.addAll(viewModel.journal.activities())
            adapter.notifyDataSetChanged()
        }

        (binding.activity.editText as? AutoCompleteTextView)?.setAdapter(adapter)


        // When the date EditText is clicked, show the date picker
        binding.date.setOnClickListener {
            val datePicker = createDatePicker()

            datePicker.addOnPositiveButtonClickListener { date ->
                // The date picker returns a Long representing the date in milliseconds,
                // which we convert to a LocalDate
                try {
                    viewModel.date =
                        Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate()

                    // Set the text of the EditText to the date picked
                    binding.date.editText?.setText(viewModel.date.toString())
                } catch (e: IllegalArgumentException) {
                    // If the date is invalid, show the error message
                    binding.date.error = e.message
                }
            }

            datePicker.show(parentFragmentManager, DATE_PICKER_TAG)
        }

        binding.startTime.setOnClickListener {
            val timePicker = createTimePicker()

            timePicker.addOnPositiveButtonClickListener {
                try {
                    viewModel.startTime = LocalTime.of(timePicker.hour, timePicker.minute)
                    binding.startTime.editText?.setText(viewModel.startTime.toString())
                } catch (e: IllegalArgumentException) {
                    binding.startTime.error = e.message
                }
            }

            timePicker.show(parentFragmentManager, START_TIME_PICKER_TAG)
        }

        binding.endTime.setOnClickListener {
            val timePicker = createTimePicker()

            timePicker.addOnPositiveButtonClickListener {
                try {
                    viewModel.endTime = LocalTime.of(timePicker.hour, timePicker.minute)
                    binding.endTime.editText?.setText(viewModel.endTime.toString())
                } catch (e: IllegalArgumentException) {
                    binding.endTime.error = e.message
                }
            }

            timePicker.show(parentFragmentManager, END_TIME_PICKER_TAG)
        }

        binding.description.editText?.addTextChangedListener {
            try {
                viewModel.description = it.toString()
            } catch (e: IllegalArgumentException) {
                binding.description.error = e.message
            }
        }

        binding.save.setOnClickListener {
            // Save the time entry in background
            lifecycleScope.launch { viewModel.save() }
        }
    }

    /**
     * Creates a date picker which only allows picking dates in the past
     * and defaults to today's date.
     */
    private fun createDatePicker(): MaterialDatePicker<Long> {
        return MaterialDatePicker.Builder.datePicker().setCalendarConstraints(
            CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now()).build()
        ).setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()
    }

    /**
     * Creates a time picker which defaults to the system's time format.
     */
    private fun createTimePicker(): MaterialTimePicker {
        val isSystem24Hour = is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        return MaterialTimePicker.Builder().setTimeFormat(clockFormat).build()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}