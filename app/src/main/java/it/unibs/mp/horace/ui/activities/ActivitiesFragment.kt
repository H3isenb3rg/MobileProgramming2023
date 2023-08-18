package it.unibs.mp.horace.ui.activities

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.renderer.PieChartRenderer
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import com.google.android.material.color.MaterialColors
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.journal.Journal
import it.unibs.mp.horace.backend.journal.JournalFactory
import it.unibs.mp.horace.databinding.FragmentActivitiesBinding
import it.unibs.mp.horace.ui.TopLevelFragment
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ActivitiesFragment : TopLevelFragment() {
    private var _binding: FragmentActivitiesBinding? = null
    private val binding get() = _binding!!

    private lateinit var journal: Journal

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActivitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        journal = JournalFactory.getJournal(requireContext())

        binding.viewJournal.setOnClickListener {
            findNavController().navigate(
                ActivitiesFragmentDirections.actionActivitiesFragmentToHistoryFragment()
            )
        }

        lifecycleScope.launch {
            val streak = journal.streak()

            // Only show the streak if it is at least 2 days long.
            if (streak < 2) {
                binding.streakContainer.isVisible = false
                return@launch
            }

            binding.streak.text = getString(R.string.streak, journal.streak())
            binding.streakStartDate.text = getString(
                R.string.streak_start_date, LocalDate.now().minusDays(streak.toLong()).format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                )
            )
        }

        // Setup the line chart in a coroutine to avoid blocking the UI thread.
        lifecycleScope.launch {
            setupLineChart(view)
        }

        // The pie chart is in yet a different coroutine because it is independent from the line chart.
        lifecycleScope.launch {
            setupPieChart(view)
        }
    }

    private suspend fun setupLineChart(view: View) {
        // Get the total times logged in the last 7 days.
        val chartEntries = journal.totalHoursInLastWeek().map {
            Entry(
                it.key.dayOfMonth.toFloat(), it.value.toFloat()
            )
        }

        val dataset = LineDataSet(chartEntries, getString(R.string.activities_in_last_7_days))

        dataset.apply {
            // Set line color
            color = MaterialColors.getColor(
                view, com.google.android.material.R.attr.colorSecondary
            )

            // Set circle style
            setDrawCircleHole(false)
            setCircleColor(
                MaterialColors.getColor(
                    view, com.google.android.material.R.attr.colorSecondary
                )
            )

            // Set line width and circle radius
            lineWidth = 3f
            circleRadius = 5f

            // Hide value labels
            setDrawValues(false)
        }

        binding.recentActivitiesChart.apply {
            data = LineData(dataset)

            // Disable zooming, scrolling, etc.
            setPinchZoom(false)
            setTouchEnabled(false)
            description.isEnabled = false
            legend.isEnabled = false

            // No data text
            setNoDataText(context.getString(R.string.no_activities_last_seven_days))
            setNoDataTextColor(
                MaterialColors.getColor(
                    view, com.google.android.material.R.attr.colorOnBackground
                )
            )

            // X axis
            xAxis.axisMinimum = LocalDate.now().minusDays(7).dayOfMonth.toFloat()
            xAxis.axisMaximum = LocalDate.now().dayOfMonth.toFloat()
            xAxis.labelCount = 7
            xAxis.valueFormatter = LineChartDateFormatter(context)
            xAxis.textColor = MaterialColors.getColor(
                view, com.google.android.material.R.attr.colorOnBackground
            )
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)
            setXAxisRenderer(
                MultilineXAxisRenderer(
                    viewPortHandler, xAxis, getTransformer(YAxis.AxisDependency.LEFT)
                )
            )
            setExtraOffsets(0f, 0f, 0f, 20f)

            // Left Y axis
            axisLeft.textColor = MaterialColors.getColor(
                view, com.google.android.material.R.attr.colorOnBackground
            )
            axisLeft.valueFormatter = LineChartHourFormatter(requireContext())
            axisLeft.setDrawAxisLine(false)

            // Right Y axis (disabled)
            axisRight.setDrawAxisLine(false)
            axisRight.setDrawGridLines(false)
        }

        // Refresh the chart
        binding.recentActivitiesChart.invalidate()
    }

    private suspend fun setupPieChart(view: View) {
        // Get the total times logged in the last 7 days.
        val chartEntries =
            journal.mostFrequentActivities().toList().sortedByDescending { (_, value) -> value }
                .take(5).map {
                    PieEntry(
                        it.second.toFloat(), it.first.name
                    )
                }

        val dataset = PieDataSet(chartEntries, "Activities")

        // Pie slice colors
        val colors = listOf(
            com.google.android.material.R.attr.colorPrimary,
            com.google.android.material.R.attr.colorSecondary,
            com.google.android.material.R.attr.colorTertiary,
            com.google.android.material.R.attr.colorPrimaryContainer,
            com.google.android.material.R.attr.colorSecondaryContainer,
            com.google.android.material.R.attr.colorTertiaryContainer,
        ).map { MaterialColors.getColor(view, it) }

        dataset.apply {
            setColors(colors)
            setValueTextColors(colors)

            // Put labels and values outside of the pie slices
            xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            valueLinePart1OffsetPercentage = 50f
            valueLinePart1Length = 0.8f
            valueLinePart2Length = 0f
            valueLineColor = ColorTemplate.COLOR_NONE
            valueTextSize = 10f
            valueFormatter = PercentFormatter(binding.activitiesFrequencyChart)
        }

        binding.activitiesFrequencyChart.apply {
            data = PieData(dataset)
            renderer = PieChartLabelRenderer(this, animator, viewPortHandler)

            // Disable zooming, scrolling, etc.
            setTouchEnabled(false)
            description.isEnabled = false
            legend.isEnabled = false

            // Set no data text
            setNoDataText(context.getString(R.string.no_activities_yet))
            setNoDataTextColor(
                MaterialColors.getColor(
                    view, com.google.android.material.R.attr.colorOnBackground
                )
            )

            // Configure the hole in the middle
            setHoleColor(
                MaterialColors.getColor(
                    view, com.google.android.material.R.attr.colorSurface
                )
            )
            holeRadius = 50f
            transparentCircleRadius = 0f

            // Show percentage values
            setUsePercentValues(true)

            // Configure labels
            setEntryLabelColor(
                MaterialColors.getColor(
                    view, com.google.android.material.R.attr.colorOnBackground
                )
            )
            setEntryLabelTextSize(18f)
            setEntryLabelTypeface(Typeface.DEFAULT_BOLD)
            setExtraOffsets(0f, 0f, 0f, 30f)
        }

        binding.activitiesFrequencyChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Formatter for the dates on the X axis of the line chart.
class LineChartDateFormatter(val context: Context) : IndexAxisValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        // Get the date of the day of the month corresponding to the value.
        val date = LocalDate.now().withDayOfMonth(
            value.toInt()
        )

        val dayName = if (date.equals(LocalDate.now())) {
            context.getString(R.string.today)
        } else if (date.equals(LocalDate.now().minusDays(1))) {
            context.getString(R.string.yesterday)
        } else {
            // Get the name of the day of the week.
            date.format(DateTimeFormatter.ofPattern("E"))
        }

        // Return the name of the day of the week and the date in the format "dd/MM".
        return dayName + "\n" + date.format(
            DateTimeFormatter.ofPattern("dd/MM")
        )
    }
}

// Formatter for the hours on the Y axis of the line chart.
class LineChartHourFormatter(val context: Context) : IndexAxisValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return if (value < 1) context.getString(R.string.mins, (value * 60).toInt().toString())
        else if (value == 1f) context.getString(R.string.one_hour)
        else return context.getString(R.string.hrs, String.format("%.1f", value))
    }
}

// Custom X axis renderer to support multiline labels.
// Source: https://stackoverflow.com/q/32509174
class MultilineXAxisRenderer(
    viewPortHandler: ViewPortHandler?, xAxis: XAxis?, trans: Transformer?
) : XAxisRenderer(viewPortHandler, xAxis, trans) {
    override fun drawLabel(
        c: Canvas?,
        formattedLabel: String,
        x: Float,
        y: Float,
        anchor: MPPointF?,
        angleDegrees: Float
    ) {
        val line =
            formattedLabel.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        Utils.drawXAxisValue(
            c, line[0], x, y, mAxisLabelPaint, anchor, angleDegrees
        )
        for (i in 1 until line.size) { // we've already processed 1st line
            Utils.drawXAxisValue(
                c,
                line[i],
                x,
                y + mAxisLabelPaint.textSize * i,
                mAxisLabelPaint,
                anchor,
                angleDegrees
            )
        }
    }
}

// Renders the label of a pie slice on top of the value.
class PieChartLabelRenderer(
    chart: PieChart?, animator: ChartAnimator?, viewPortHandler: ViewPortHandler?
) : PieChartRenderer(chart, animator, viewPortHandler) {
    private var mHasLabelData = false
    private var mHasValueData = false
    private var mEntryLabelCanvas: Canvas? = null
    private var mValueCanvas: Canvas? = null
    private var mEntryLabel: String = ""
    private var mValueText: String = ""
    private var mEntryLabelX = 0f
    private var mValueX = 0f
    private var mEntryLabelY = 0f
    private var mValueY = 0f
    private var mValueColor = 0

    override fun drawEntryLabel(c: Canvas?, label: String, x: Float, y: Float) {
        //instead of calling super save the label data temporary
        //super.drawEntryLabel(c, label, x, y)
        mHasLabelData = true
        //save all entry label information temporary
        mEntryLabelCanvas = c
        mEntryLabel = label
        mEntryLabelX = x
        mEntryLabelY = y
        //and check if we have both label and value data temporary to draw them
        checkToDrawLabelValue()
    }

    override fun drawValue(c: Canvas?, valueText: String, x: Float, y: Float, color: Int) {
        //instead of calling super save the value data temporary
        //super.drawValue(c, valueText, x, y, color)
        mHasValueData = true
        //save all value information temporary
        mValueCanvas = c
        mValueText = valueText
        mValueX = x
        mValueY = y
        mValueColor = color
        //and check if we have both label and value data temporary to draw them
        checkToDrawLabelValue()
    }

    private fun checkToDrawLabelValue() {
        if (mHasLabelData && mHasValueData) {
            drawLabelAndValue()
            mHasLabelData = false
            mHasValueData = false
        }
    }

    private fun drawLabelAndValue() {
        //to show label on top of the value just swap the mEntryLabelY with mValueY
        drawEntryLabelData(mEntryLabelCanvas, mEntryLabel, mEntryLabelX, mValueY)
        drawValueData(mValueCanvas, mValueText, mValueX, mEntryLabelY, mValueColor)
    }

    //This is the same code used in super.drawEntryLabel(c, label, x, y) with any other customization you want in mEntryLabelsPaint
    private fun drawEntryLabelData(c: Canvas?, label: String, x: Float, y: Float) {
        val mEntryLabelsPaint: Paint = paintEntryLabels
        mEntryLabelsPaint.typeface = Typeface.DEFAULT
        mEntryLabelsPaint.textAlign = Paint.Align.CENTER
        c?.drawText(label, x, y, mEntryLabelsPaint)
    }

    //This is the same code used in super.drawValue(c, valueText, x, y, color) with any other customization you want in mValuePaint
    private fun drawValueData(c: Canvas?, valueText: String, x: Float, y: Float, color: Int) {
        mValuePaint.color = color
        mValuePaint.textAlign = Paint.Align.CENTER
        c?.drawText(valueText, x, y, mValuePaint)
    }
}