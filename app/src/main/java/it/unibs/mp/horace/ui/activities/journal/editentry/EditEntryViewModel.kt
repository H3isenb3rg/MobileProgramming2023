package it.unibs.mp.horace.ui.activities.journal.editentry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import it.unibs.mp.horace.backend.firebase.models.Activity
import it.unibs.mp.horace.backend.firebase.models.TimeEntry
import it.unibs.mp.horace.backend.journal.Journal
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Factory for creating a [EditEntryViewModel] with a constructor that takes a [Journal].
 * Required given that [EditEntryViewModel] takes a constructor argument.
 */
class EditEntryViewModelFactory(private val journal: Journal, private val entryId: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditEntryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return EditEntryViewModel(journal, entryId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class EditEntryViewModel(val journal: Journal, private val entryId: String) : ViewModel() {
    companion object {
        const val ERROR_DATE_IN_FUTURE = "Date can't be in the future"
        const val ERROR_START_TIME_AFTER_END_TIME = "Start is after end"
        const val ERROR_END_TIME_BEFORE_START_TIME = "End is before start"
        const val ERROR_DESCRIPTION_LENGTH = "Description can't be longer than 255 characters"
        const val ERROR_DATE_NULL = "Date is required"
        const val ERROR_START_TIME_NULL = "Start time is required"
        const val ERROR_END_TIME_NULL = "End time is required"
        const val ERROR_ACTIVITY_NULL = "Activity is required"
    }

    private lateinit var timeEntry: TimeEntry

    init {
        viewModelScope.launch {
            // The time entry should exist, otherwise crash the app
            timeEntry = journal.getTimeEntry(entryId)!!
        }
    }

    private var _activity: Activity? = null
    private var _date: LocalDate? = null
    private var _startTime: LocalTime? = null
    private var _endTime: LocalTime? = null
    private var _description: String? = null

    var activity: Activity?
        get() = _activity
        set(value) {
            _activity = value
            validateActivity()
        }

    var date: LocalDate?
        get() = _date
        set(value) {
            _date = value
            validateDate()
        }

    var startTime: LocalTime?
        get() = _startTime
        set(value) {
            _startTime = value
            validateStartTime()
        }

    var endTime: LocalTime?
        get() = _endTime
        set(value) {
            _endTime = value
            validateEndTime()
        }

    var description: String?
        get() = _description
        set(value) {
            _description = value
            validateDescription()
        }

    var activityError: String? = null
        private set
    var dateError: String? = null
        private set
    var startTimeError: String? = null
        private set
    var endTimeError: String? = null
        private set
    var descriptionError: String? = null
        private set

    private val isEverythingValid
        get() =
            activityError == null && dateError == null && startTimeError == null && endTimeError == null && descriptionError == null

    /**
     * Save the time entry to the journal, if everything is valid.
     * Otherwise throws an [IllegalStateException].
     */
    suspend fun save() {
        validateActivity()
        validateDate()
        validateStartTime()
        validateEndTime()
        validateDescription()

        if (!isEverythingValid) {
            throw IllegalStateException()
        }
        val startDateTime = LocalDateTime.of(date, startTime)
        val endDateTime = LocalDateTime.of(date, endTime)

        timeEntry.description = description
        timeEntry.startTime = startDateTime
        timeEntry.endTime = endDateTime
        timeEntry.activity = activity

        journal.updateTimeEntry(timeEntry)
    }

    private fun validateActivity() {
        activityError = when (_activity) {
            null -> ERROR_ACTIVITY_NULL
            else -> null
        }
    }

    private fun validateDate() {
        dateError = when {
            _date == null -> ERROR_DATE_NULL
            _date!!.isAfter(LocalDate.now()) -> ERROR_DATE_IN_FUTURE
            else -> null
        }
    }

    private fun validateStartTime() {
        startTimeError = when {
            _startTime == null -> ERROR_START_TIME_NULL
            _endTime != null && _startTime!!.isAfter(_endTime) -> ERROR_START_TIME_AFTER_END_TIME
            else -> null
        }
    }

    private fun validateEndTime() {
        endTimeError = when {
            _endTime == null -> ERROR_END_TIME_NULL
            _startTime != null && _endTime!!.isBefore(_startTime) -> ERROR_END_TIME_BEFORE_START_TIME
            else -> null
        }
    }

    private fun validateDescription() {
        descriptionError = when {
            _description != null && _description!!.length > 255 -> ERROR_DESCRIPTION_LENGTH
            else -> null
        }
    }
}