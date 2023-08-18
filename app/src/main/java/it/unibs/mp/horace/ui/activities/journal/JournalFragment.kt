package it.unibs.mp.horace.ui.activities.journal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import it.unibs.mp.horace.backend.journal.JournalDay
import it.unibs.mp.horace.backend.journal.JournalFactory
import it.unibs.mp.horace.databinding.FragmentJournalBinding
import kotlinx.coroutines.launch

class JournalFragment : Fragment() {
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

        val journal = JournalFactory.getJournal(requireContext())
        val journalDays: ArrayList<JournalDay> = ArrayList()

        val adapter = JournalAdapter(requireContext(), journalDays)
        binding.journalsView.adapter = adapter

        lifecycleScope.launch {
            journalDays.addAll(JournalDay.fromTimeEntries(journal.getAllTimeEntries()))
            adapter.notifyItemRangeInserted(0, journalDays.size)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}