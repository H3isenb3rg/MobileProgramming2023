package it.unibs.mp.horace.ui.manuallog

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.navigation.fragment.findNavController
import it.unibs.mp.horace.databinding.FragmentManualLogBinding
import it.unibs.mp.horace.models.Activity
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class ManualLogFragment : Fragment() {
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

        setupActivitiesAutocomplete()

        // When the date EditText is clicked, show the date picker
        binding.date.editText?.setOnClickListener {
            showDatePicker { date ->
                viewModel.date = date
                binding.date.editText?.setText(viewModel.date.toString())
                binding.date.error = viewModel.dateError
            }
        }

        // When the start time EditText is clicked, show the time picker
        binding.startTime.editText?.setOnClickListener {
            showTimePicker { time ->
                viewModel.startTime = time
                binding.startTime.editText?.setText(viewModel.startTime.toString())
                binding.startTime.error = viewModel.startTimeError
            }
        }

        // When the end time EditText is clicked, show the time picker
        binding.endTime.editText?.setOnClickListener {
            showTimePicker { time ->
                viewModel.endTime = time
                binding.endTime.editText?.setText(viewModel.endTime.toString())
                binding.endTime.error = viewModel.endTimeError
            }
        }

        binding.description.editText?.addTextChangedListener {
            viewModel.description = it.toString()
            binding.description.error = viewModel.descriptionError
        }

        binding.save.setOnClickListener {
            // Save the time entry in background
            lifecycleScope.launch {
                try {
                    // Try to save the time entry
                    viewModel.save()

                    // If everything is ok, go back to previous screen
                    findNavController().navigateUp()
                } catch (e: IllegalStateException) {
                    // Show the errors
                    binding.activity.error = viewModel.activityError
                    binding.date.error = viewModel.dateError
                    binding.startTime.error = viewModel.startTimeError
                    binding.endTime.error = viewModel.endTimeError
                    binding.description.error = viewModel.descriptionError
                }
            }
        }
    }

    private fun setupActivitiesAutocomplete() {
        // List of activities to show in the autocomplete, initially empty
        val activities: ArrayList<Activity> = arrayListOf()
        // Adapter for the autocomplete
        val adapter = ActivitiesAdapter(requireContext(), activities)

        val autoCompleteTextView = binding.activity.editText as? AutoCompleteTextView

        // When the user selects an activity, set it in the ViewModel and update ui
        autoCompleteTextView?.setOnItemClickListener { _, _, position, _ ->
            viewModel.activity = activities[position]
            autoCompleteTextView.setText(activities[position].name, false)
            binding.activity.error = viewModel.activityError
        }

        // Load the activities in background
        lifecycleScope.launch {
            activities.addAll(viewModel.journal.activities())
            adapter.notifyDataSetChanged()
        }

        autoCompleteTextView?.setAdapter(adapter)
    }

    /**
     * Shows a date picker which only allows picking dates in the past
     * and defaults to today's date.
     */
    private fun showDatePicker(onSelected: (date: LocalDate) -> Unit) {
        val picker = DatePickerDialog(
            requireContext(), { _, year, month, dayOfMonth ->
                onSelected(LocalDate.of(year, month + 1, dayOfMonth))
            }, LocalDate.now().year, LocalDate.now().monthValue, LocalDate.now().dayOfMonth
        )
        picker.datePicker.maxDate = System.currentTimeMillis()

        picker.show()
    }

    /**
     * Shows a time picker which defaults to the system's time format.
     */
    private fun showTimePicker(onSelected: (time: LocalTime) -> Unit) {
        val isSystem24Hour = is24HourFormat(requireContext())

        val picker = TimePickerDialog(
            requireContext(), { _, hourOfDay, minute ->
                onSelected(LocalTime.of(hourOfDay, minute))
            }, LocalTime.now().hour, LocalTime.now().minute, isSystem24Hour
        )

        picker.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}