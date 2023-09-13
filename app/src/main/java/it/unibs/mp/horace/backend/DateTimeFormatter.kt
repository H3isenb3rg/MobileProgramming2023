package it.unibs.mp.horace.backend

import android.content.Context
import it.unibs.mp.horace.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DateTimeFormatter(val context: Context) {
    fun formatDate(date: LocalDate): String {
        return when (date) {
            LocalDate.now() -> {
                context.getString(R.string.today)
            }

            LocalDate.now().minusDays(1) -> {
                context.getString(R.string.yesterday)
            }

            else -> date.format(DateTimeFormatter.ofPattern("E, d MMM yyyy"))
        }
    }

    fun formatTime(time: LocalTime): String = time.format(DateTimeFormatter.ofPattern("HH:mm"))

    fun formatDuration(duration: Int): String {
        val hours = duration / 3600
        val minutes = (duration % 3600) / 60

        val secondsString = context.getString(R.string.seconds, duration)
        val minutesString = context.resources.getQuantityString(R.plurals.minutes, minutes, minutes)
        val hoursString = context.resources.getQuantityString(R.plurals.hours, hours, hours)

        return if (duration < 60) {
            secondsString
        } else if (hours == 0) {
            minutesString
        } else if (minutes == 0) {
            hoursString
        } else {
            "$hoursString $minutesString"
        }
    }
}