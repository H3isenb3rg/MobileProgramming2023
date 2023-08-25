package it.unibs.mp.horace.ui.activities.journal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.journal.JournalFactory
import it.unibs.mp.horace.databinding.DialogEntryOptionsBinding
import kotlinx.coroutines.launch

class EntryOptionsDialog : BottomSheetDialogFragment() {
    private var _binding: DialogEntryOptionsBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<EntryOptionsDialogArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogEntryOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val journal = JournalFactory(requireContext()).getJournal()

        binding.cardEdit.setOnClickListener {
            findNavController().navigate(
                EntryOptionsDialogDirections.actionEntryOptionsDialogToEditEntryFragment(args.entryId)
            )
        }

        binding.cardDelete.setOnClickListener {
            lifecycleScope.launch {
                val entry = journal.getTimeEntry(args.entryId)
                if (entry != null) {
                    journal.removeTimeEntry(entry)
                }
                Snackbar.make(
                    view,
                    getString(R.string.dialog_entry_options_deleted), Snackbar.LENGTH_SHORT
                ).show()

                findNavController().navigate(
                    EntryOptionsDialogDirections.actionEntryOptionsDialogToJournalFragment()
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}