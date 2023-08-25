package it.unibs.mp.horace.ui.activities.journal.editentry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.unibs.mp.horace.backend.firebase.models.Activity
import it.unibs.mp.horace.backend.firebase.models.TimeEntry
import it.unibs.mp.horace.backend.journal.Journal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Factory for creating a [EditEntryViewModel] with a constructor that takes a [Journal].
 * Required given that [EditEntryViewModel] takes a constructor argument.
 */
class EditEntryViewModelFactory(private val journal: Journal) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditEntryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return EditEntryViewModel(journal) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class EditEntryViewModel(val journal: Journal) : ViewModel() {
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

    var timeEntry: TimeEntry = TimeEntry()

    var activity: Activity?
        get() = timeEntry.activity
        set(value) {
            timeEntry.activity = value
            validateActivity()
        }

    var date: LocalDate?
        get() = timeEntry.startTime.toLocalDate()
        set(value) {
            timeEntry.startTime = LocalDateTime.of(value, timeEntry.startTime.toLocalTime())
            timeEntry.endTime = LocalDateTime.of(value, timeEntry.endTime.toLocalTime())
            validateDate()
        }

    var startTime: LocalTime?
        get() = timeEntry.startTime.toLocalTime()
        set(value) {
            timeEntry.startTime = LocalDateTime.of(timeEntry.startTime.toLocalDate(), value)
            validateStartTime()
        }

    var endTime: LocalTime?
        get() = timeEntry.endTime.toLocalTime()
        set(value) {
            timeEntry.endTime = LocalDateTime.of(timeEntry.endTime.toLocalDate(), value)
            validateEndTime()
        }

    var description: String?
        get() = timeEntry.description
        set(value) {
            timeEntry.description = value
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

        journal.updateTimeEntry(timeEntry)
    }

    private fun validateActivity() {
        activityError = when (activity) {
            null -> ERROR_ACTIVITY_NULL
            else -> null
        }
    }

    private fun validateDate() {
        dateError = when {
            date == null -> ERROR_DATE_NULL
            date!!.isAfter(LocalDate.now()) -> ERROR_DATE_IN_FUTURE
            else -> null
        }
    }

    private fun validateStartTime() {
        startTimeError = when {
            startTime == null -> ERROR_START_TIME_NULL
            endTime != null && startTime!!.isAfter(endTime) -> ERROR_START_TIME_AFTER_END_TIME
            else -> null
        }
    }

    private fun validateEndTime() {
        endTimeError = when {
            endTime == null -> ERROR_END_TIME_NULL
            startTime != null && endTime!!.isBefore(startTime) -> ERROR_END_TIME_BEFORE_START_TIME
            else -> null
        }
    }

    private fun validateDescription() {
        descriptionError = when {
            description != null && description!!.length > 255 -> ERROR_DESCRIPTION_LENGTH
            else -> null
        }
    }
}