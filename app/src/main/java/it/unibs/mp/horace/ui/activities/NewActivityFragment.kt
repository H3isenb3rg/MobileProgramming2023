package it.unibs.mp.horace.ui.activities

import android.os.Bundle
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
import it.unibs.mp.horace.databinding.FragmentNewActivityBinding
import kotlinx.coroutines.launch

class NewActivityFragment : Fragment() {
    private var _binding: FragmentNewActivityBinding? = null
    private val binding get() = _binding!!

    // All the validation logic is in the ViewModel
    private val viewModel: NewActivityViewModel by activityViewModels {
        // Create the ViewModel with the Journal.
        NewActivityViewModelFactory(JournalFactory(requireContext()).getJournal())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAreaAutocomplete()

        binding.textinputActivity.editText?.addTextChangedListener {
            viewModel.activity = it.toString()
            binding.textinputActivity.error = viewModel.activityError
        }

        binding.textinputArea.editText?.addTextChangedListener {
            viewModel.area = it.toString()
            binding.textinputArea.error = viewModel.areaError
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
                    binding.textinputActivity.error = viewModel.areaError
                }
            }
        }

        binding.buttonNewArea.setOnClickListener {
            findNavController().navigate(NewActivityFragmentDirections.actionNewActivityFragmentToNewAreaFragment())
        }
    }

    private fun setupAreaAutocomplete() {
        // Load the activities in background
        lifecycleScope.launch {
            val autoCompleteTextView = binding.textinputArea.editText as? AutoCompleteTextView
            val currAreas = viewModel.journal.getAllAreas()

            // When the user selects an activity, set it in the ViewModel and update ui
            autoCompleteTextView?.setOnItemClickListener { _, _, position, _ ->
                viewModel.area = currAreas[position].name
                autoCompleteTextView.setText(currAreas[position].name, false)
                binding.textinputArea.error = viewModel.areaError
            }
            // Adapter for the autocomplete
            val adapter = AreaAdapter(requireContext(), currAreas)

            autoCompleteTextView?.setAdapter(adapter)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}