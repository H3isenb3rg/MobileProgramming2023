package it.unibs.mp.horace.ui.activities.journal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Timestamp
import it.unibs.mp.horace.backend.CurrentUser
import it.unibs.mp.horace.backend.FirestoreJournal
import it.unibs.mp.horace.databinding.FragmentJournalBinding
import it.unibs.mp.horace.models.Activity
import it.unibs.mp.horace.models.Area
import it.unibs.mp.horace.models.JournalDay
import it.unibs.mp.horace.models.TimeEntry
import it.unibs.mp.horace.models.User
import it.unibs.mp.horace.ui.MainActivity
import it.unibs.mp.horace.ui.leaderboard.friends.FriendsAdapter
import it.unibs.mp.horace.ui.shareUserProfile
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class JournalFragment : Fragment() {
    private var _binding: FragmentJournalBinding? = null
    private val binding get() = _binding!!

    val user = CurrentUser()
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
        val daysList: ArrayList<JournalDay> = ArrayList()
        val journalDays: ArrayList<JournalDay> = ArrayList()

        // FIXME: First debug version
        lifecycleScope.launch {
            entries = firestoreJournal.userEntries(user.uid)
        }.invokeOnCompletion {
            daysList.add(JournalDay(entries, entries[0].startLocalDateTime().toLocalDate()))
            val adapter = JournalAdapter()
            binding.journalsView.adapter = adapter
            adapter.addData(daysList)
        }

        // Test Data
        // val area = Area("UNI")
        // val activityMobile = Activity("Mobile Programming", area)
        // val dt1 = LocalDateTime.parse("2023-07-18T10:00:00")
        // val ts1 = Timestamp(dt1.toEpochSecond(ZoneOffset.UTC), dt1.nano)
        // val dt2 = LocalDateTime.parse("2023-07-18T12:45:00")
        // val ts2 = Timestamp(dt2.toEpochSecond(ZoneOffset.UTC), dt2.nano)
        // val entry1 = TimeEntry(null, "Test Entry 1", activityMobile, false, ts1, ts2, 50, user.userData)
//
        // val dt3 = LocalDateTime.parse("2023-07-18T14:00:00")
        // val ts3 = Timestamp(dt3.toEpochSecond(ZoneOffset.UTC), dt3.nano)
        // val dt4 = LocalDateTime.parse("2023-07-18T16:00:00")
        // val ts4 = Timestamp(dt4.toEpochSecond(ZoneOffset.UTC), dt4.nano)
        // val entry2 = TimeEntry(null, "Test Entry 2", activityMobile, false, ts3, ts4, 50, user.userData)
        // val list1 = ArrayList<TimeEntry>()
        // list1.add(entry1)
        // list1.add(entry2)
        // lifecycleScope.launch {
        //     firestoreJournal.addEntry(entry1)
        //     firestoreJournal.addEntry(entry2)
        // }

        // val day1 = JournalDay(list1, LocalDate.parse("2023-07-18"))
        // val daysList = ArrayList<JournalDay>()
        // daysList.add(day1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}