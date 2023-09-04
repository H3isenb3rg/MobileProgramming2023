package it.unibs.mp.horace.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import it.unibs.mp.horace.backend.journal.JournalFactory
import it.unibs.mp.horace.databinding.FragmentNewAreaBinding
import kotlinx.coroutines.launch

class NewAreaFragment : Fragment() {
    private var _binding: FragmentNewAreaBinding? = null
    private val binding get() = _binding!!

    // All the validation logic is in the ViewModel.
    private val viewModel: NewAreaViewModel by activityViewModels {
        // Create the ViewModel with the Journal.
        NewAreaViewModelFactory(JournalFactory(requireContext()).getJournal())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewAreaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textinputName.editText?.addTextChangedListener {
            viewModel.name = it.toString()
            binding.textinputName.error = viewModel.error
        }

        binding.textinputSave.setOnClickListener {
            // Save the area in background.
            lifecycleScope.launch {
                try {
                    viewModel.save()
                    findNavController().navigateUp()
                } catch (e: IllegalStateException) {
                    binding.textinputName.error = viewModel.error
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}