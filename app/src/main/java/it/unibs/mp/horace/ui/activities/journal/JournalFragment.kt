package it.unibs.mp.horace.ui.activities.journal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import it.unibs.mp.horace.backend.Settings
import it.unibs.mp.horace.backend.journal.JournalDay
import it.unibs.mp.horace.backend.journal.JournalFactory
import it.unibs.mp.horace.databinding.FragmentJournalBinding
import it.unibs.mp.horace.ui.SortFragment
import kotlinx.coroutines.launch

class JournalFragment : SortFragment() {
    private var _binding: FragmentJournalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJournalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settings = Settings(requireContext())

        val journal = JournalFactory(requireContext()).getJournal()
        val journalDays: ArrayList<JournalDay> = ArrayList()

        val adapter = JournalAdapter(requireContext(), journalDays)
        binding.recyclerviewJournal.adapter = adapter

        lifecycleScope.launch {
            journalDays.addAll(JournalDay.fromTimeEntries(journal.getAllTimeEntries()))

            if (settings.isJournalSortAscending) journalDays.sortBy { it.date }
            else journalDays.sortByDescending { it.date }

            adapter.notifyItemRangeInserted(0, journalDays.size)

            binding.textviewNoEntries.isVisible = journalDays.isEmpty()
        }
    }

    override fun onSortSelected() {
        findNavController().navigate(JournalFragmentDirections.actionJournalFragmentToSortJournalDialog())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}