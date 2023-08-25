package it.unibs.mp.horace.ui.activities.journal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import it.unibs.mp.horace.backend.Settings
import it.unibs.mp.horace.databinding.DialogSortJournalBinding

class SortJournalDialog : BottomSheetDialogFragment() {
    private var _binding: DialogSortJournalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogSortJournalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val settings = Settings(requireContext())

        binding.radiobuttonMostRecentDate.isChecked = settings.isJournalSortMostRecent
        binding.radiobuttonLeastRecentDate.isChecked = !settings.isJournalSortMostRecent

        binding.radiogroupSortJournal.setOnCheckedChangeListener { _, checkedId ->
            settings.isJournalSortMostRecent = checkedId == binding.radiobuttonMostRecentDate.id
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}