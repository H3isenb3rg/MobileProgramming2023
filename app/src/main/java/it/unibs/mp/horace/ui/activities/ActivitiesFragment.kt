package it.unibs.mp.horace.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import it.unibs.mp.horace.backend.firebase.CurrentUser
import it.unibs.mp.horace.backend.firebase.models.TimeEntry
import it.unibs.mp.horace.databinding.FragmentActivitiesBinding
import it.unibs.mp.horace.ui.TopLevelFragment

class ActivitiesFragment : TopLevelFragment() {
    private var _binding: FragmentActivitiesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActivitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewJournal.setOnClickListener {
            try {
                CurrentUser()
                findNavController().navigate(
                    ActivitiesFragmentDirections.actionActivitiesFragmentToHistoryFragment()
                )
            } catch (e: IllegalAccessError) {
                Log.w("Journal Disabled", e)
            }
        }

        val timeEntries: List<TimeEntry> = listOf(TimeEntry(), TimeEntry(), TimeEntry())
        val mapOfTimeEntries =
            timeEntries.groupBy { entry -> entry.startTime.toLocalDate() }
                .mapValues { group -> group.value.size }

        val chartEntries: List<Entry> = mapOfTimeEntries.map {
            Entry(
                it.key.dayOfMonth.toFloat(), it.value.toFloat()
            )
        }
        val dataset = LineDataSet(chartEntries, "Activities")

        binding.recentActivitiesChart.data = LineData(dataset)
        binding.recentActivitiesChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}