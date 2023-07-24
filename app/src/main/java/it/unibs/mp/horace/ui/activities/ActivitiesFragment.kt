package it.unibs.mp.horace.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import it.unibs.mp.horace.databinding.FragmentActivitiesBinding
import it.unibs.mp.horace.models.TimeEntry
import it.unibs.mp.horace.ui.TopLevelFragment
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar

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
            findNavController().navigate(
                ActivitiesFragmentDirections.actionActivitiesFragmentToHistoryFragment()
            )
        }

        val timeEntries: List<TimeEntry> = listOf(TimeEntry(), TimeEntry(), TimeEntry())
        val mapOfTimeEntries =
            timeEntries.groupBy { entry -> entry.startLocalDateTime().toLocalDate() }
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