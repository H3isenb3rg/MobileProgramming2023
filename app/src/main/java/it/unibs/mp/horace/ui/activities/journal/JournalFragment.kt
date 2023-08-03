package it.unibs.mp.horace.ui.activities.journal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import it.unibs.mp.horace.backend.CurrentUser
import it.unibs.mp.horace.backend.journal.FirestoreJournal
import it.unibs.mp.horace.databinding.FragmentJournalBinding
import it.unibs.mp.horace.models.JournalDay
import it.unibs.mp.horace.models.TimeEntry
import kotlinx.coroutines.launch

class JournalFragment : Fragment() {
    private var _binding: FragmentJournalBinding? = null
    private val binding get() = _binding!!

    val user = CurrentUser()

    // TODO: Usare factory per prendere journal
    val firestoreJournal = FirestoreJournal()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJournalBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var entries: List<TimeEntry> = ArrayList()
        val journalDays: ArrayList<JournalDay> = ArrayList()

        lifecycleScope.launch {
            entries = firestoreJournal.entries()
        }.invokeOnCompletion {
            if (entries.isEmpty()) {
                return@invokeOnCompletion
            }
            journalDays.addAll(JournalDay.split(entries))
            val adapter = JournalAdapter()
            binding.journalsView.adapter = adapter
            adapter.addData(journalDays)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}