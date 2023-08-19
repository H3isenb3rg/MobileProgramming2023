package it.unibs.mp.horace.ui.manuallog

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import it.unibs.mp.horace.backend.journal.JournalFactory
import it.unibs.mp.horace.databinding.FragmentManualLogBinding
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class ManualLogFragment : Fragment() {
    private var _binding: FragmentManualLogBinding? = null
    private val binding get() = _binding!!

    // All the validation logic is in the ViewModel
    private val viewModel: ManualLogViewModel by activityViewModels {
        ManualLogViewModelFactory(JournalFactory.getJournal(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManualLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // lifecycleScope.launch {
        //     viewModel.journal.addActivity(hashMapOf(
        //         Activity.NAME_FIELD to "Mobile"
        //     ))
        // }.invokeOnCompletion {
        // }
        setupActivitiesAutocomplete()

        // When the date EditText is clicked, show the date picker
        binding.textinputDate.editText?.setOnClickListener {
            showDatePicker { date ->
                viewModel.date = date
                binding.textinputDate.editText?.setText(viewModel.date.toString())
                binding.textinputDate.error = viewModel.dateError
            }
        }

        // When the start time EditText is clicked, show the time picker
        binding.textinputStartTime.editText?.setOnClickListener {
            showTimePicker { time ->
                viewModel.startTime = time
                binding.textinputStartTime.editText?.setText(viewModel.startTime.toString())
                binding.textinputStartTime.error = viewModel.startTimeError
            }
        }

        // When the end time EditText is clicked, show the time picker
        binding.textinputEndTime.editText?.setOnClickListener {
            showTimePicker { time ->
                viewModel.endTime = time
                binding.textinputEndTime.editText?.setText(viewModel.endTime.toString())
                binding.textinputEndTime.error = viewModel.endTimeError
            }
        }

        binding.textinputDescription.editText?.addTextChangedListener {
            viewModel.description = it.toString()
            binding.textinputDescription.error = viewModel.descriptionError
        }

        binding.buttonNewActivity.setOnClickListener {
            findNavController().navigate(
                ManualLogFragmentDirections.actionGlobalNewActivity()
            )
        }

        binding.buttonSave.setOnClickListener {
            // Save the time entry in background
            lifecycleScope.launch {
                try {
                    // Try to save the time entry
                    viewModel.save()

                    // If everything is ok, go back to previous screen
                    findNavController().navigateUp()
                } catch (e: IllegalStateException) {
                    // Show the errors
                    binding.textinputActivity.error = viewModel.activityError
                    binding.textinputDate.error = viewModel.dateError
                    binding.textinputStartTime.error = viewModel.startTimeError
                    binding.textinputEndTime.error = viewModel.endTimeError
                    binding.textinputDescription.error = viewModel.descriptionError
                }
            }
        }
    }

    private fun setupActivitiesAutocomplete() {
        // Load the activities in background
        lifecycleScope.launch {
            val autoCompleteTextView = binding.textinputActivity.editText as? AutoCompleteTextView
            val currActivities = viewModel.journal.getAllActivities()

            // When the user selects an activity, set it in the ViewModel and update ui
            autoCompleteTextView?.setOnItemClickListener { _, _, position, _ ->
                Log.d("TEst", "test")
                viewModel.activity = currActivities[position]
                autoCompleteTextView.setText(currActivities[position].name, false)
                binding.textinputActivity.error = viewModel.activityError
            }
            // Adapter for the autocomplete
            val adapter = ActivitiesAdapter(requireContext(), currActivities)

            autoCompleteTextView?.setAdapter(adapter)
        }
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