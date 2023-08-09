package it.unibs.mp.horace.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import it.unibs.mp.horace.databinding.FragmentNewActivityBinding
import kotlinx.coroutines.launch

class NewActivityFragment : Fragment() {
    private var _binding: FragmentNewActivityBinding? = null
    private val binding get() = _binding!!

    // All the validation logic is in the ViewModel
    private val viewModel: NewActivityViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAreaAutocomplete()

        binding.activity.editText?.addTextChangedListener {
            viewModel.activity = it.toString()
            binding.activity.error = viewModel.activityError
        }

        binding.area.editText?.addTextChangedListener {
            viewModel.area = it.toString()
            binding.area.error = viewModel.areaError
        }

        binding.add.setOnClickListener {
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
                    binding.area.error = viewModel.areaError
                }
            }
        }
    }

    private fun setupAreaAutocomplete() {
        // Load the activities in background
        lifecycleScope.launch {
            val autoCompleteTextView = binding.area.editText as? AutoCompleteTextView
            val currAreas = viewModel.journal.getAllAreas()

            // When the user selects an activity, set it in the ViewModel and update ui
            autoCompleteTextView?.setOnItemClickListener { _, _, position, _ ->
                viewModel.area = currAreas[position].name
                autoCompleteTextView.setText(currAreas[position].name, false)
                binding.activity.error = viewModel.areaError
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