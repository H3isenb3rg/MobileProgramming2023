package it.unibs.mp.horace.ui.manuallog

import androidx.lifecycle.ViewModel
import it.unibs.mp.horace.backend.journal.JournalFactory
import it.unibs.mp.horace.models.Activity
import it.unibs.mp.horace.models.TimeEntry
import java.time.LocalDate
import java.time.LocalTime

class ManualLogViewModel : ViewModel() {
    companion object {
        const val ERROR_DATE_IN_FUTURE = "Date cannot be in the future"
        const val ERROR_START_TIME_AFTER_END_TIME = "Start time cannot be after end time"
        const val ERROR_END_TIME_BEFORE_START_TIME = "End time cannot be before start time"
        const val ERROR_DESCRIPTION_LENGTH = "Description cannot be longer than 255 characters"
        const val ERROR_DATE_NULL = "Date cannot be null"
        const val ERROR_START_TIME_NULL = "Start time cannot be null"
        const val ERROR_END_TIME_NULL = "End time cannot be null"
        const val ERROR_ACTIVITY_NULL = "Activity cannot be null"
    }

    private var _activity: Activity? = null
    private var _date: LocalDate? = null
    private var _startTime: LocalTime? = null
    private var _endTime: LocalTime? = null
    private var _description: String? = null

    val journal = JournalFactory.getJournal()

    var activity: Activity?
        get() = _activity
        set(value) {
            validateActivity(value)
            _activity = value
        }

    var date: LocalDate?
        get() = _date
        set(value) {
            validateDate(value)
            _date = value
        }

    var startTime: LocalTime?
        get() = _startTime
        set(value) {
            validateStartTime(value)
            _startTime = value
        }

    var endTime: LocalTime?
        get() = _endTime
        set(value) {
            validateEndTime(value)
            _endTime = value
        }

    var description: String?
        get() = _description
        set(value) {
            validateDescription(value)
            _description = value
        }

    suspend fun save() {
        validateActivity(_activity)
        validateDate(_date)
        validateStartTime(_startTime)
        validateEndTime(_endTime)
        validateDescription(_description)

        val entry = TimeEntry(
            _date.toString(), _startTime.toString(), _endTime.toString(), _activity, _description, 0
        )
        journal.addEntry(entry)
    }

    private fun validateActivity(activity: Activity?) {
        if (activity == null) {
            throw IllegalArgumentException(ERROR_ACTIVITY_NULL)
        }
    }

    private fun validateDate(date: LocalDate?) {
        if (date == null) {
            throw IllegalArgumentException(ERROR_DATE_NULL)
        }
        if (date.isAfter(LocalDate.now())) {
            throw IllegalArgumentException(ERROR_DATE_IN_FUTURE)
        }
    }

    private fun validateStartTime(startTime: LocalTime?) {
        if (startTime == null) {
            throw IllegalArgumentException(ERROR_START_TIME_NULL)
        }
        if (startTime.isAfter(_endTime)) {
            throw IllegalArgumentException(ERROR_START_TIME_AFTER_END_TIME)
        }
    }

    private fun validateEndTime(endTime: LocalTime?) {
        if (endTime == null) {
            throw IllegalArgumentException(ERROR_END_TIME_NULL)
        }
        if (endTime.isBefore(_startTime)) {
            throw IllegalArgumentException(ERROR_END_TIME_BEFORE_START_TIME)
        }
    }

    private fun validateDescription(description: String?) {
        if (description != null && description.length > 255) {
            throw IllegalArgumentException(ERROR_DESCRIPTION_LENGTH)
        }
    }
}