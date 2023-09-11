package it.unibs.mp.horace.ui.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import it.unibs.mp.horace.backend.firebase.models.Area
import it.unibs.mp.horace.backend.journal.Journal
import kotlinx.coroutines.launch

/**
 * Factory for creating a [NewActivityViewModel] with a constructor that takes a [Journal].
 * Required given that [NewActivityViewModel] takes a constructor argument.
 */
class NewActivityViewModelFactory(private val journal: Journal) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return NewActivityViewModel(journal) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class NewActivityViewModel(val journal: Journal) : ViewModel() {
    companion object {
        const val ERROR_ACTIVITY_NULL = "Activity is required"
        const val ERROR_ACTIVITY_EXISTS = "Activity already exists"
    }

    private var _activity: String? = null
    private var _area: String? = null

    var activity: String?
        get() = _activity
        set(value) {
            _activity = value
            validateActivity()
        }

    var area: String?
        get() = _area
        set(value) {
            _area = value
            viewModelScope.launch { validateArea() }
        }

    var activityError: String? = null
        private set

    var areaError: String? = null
        private set

    private val isEverythingValid
        get() = activityError == null && areaError == null

    /**
     * Save the time entry to the journal, if everything is valid.
     * Otherwise throws an [IllegalStateException].
     */
    suspend fun save() {
        validateActivity()
        var currArea = validateArea()

        if (!isEverythingValid) {
            throw IllegalStateException()
        }

        if (area != null) {
            if (currArea == null) {
                currArea = journal.addArea(area!!)
            }
        }

        journal.addActivity(activity!!, currArea)
    }

    private fun validateActivity() {
        activityError = when (_activity) {
            null -> ERROR_ACTIVITY_NULL
            else -> null
        }
        if (activityError != null) {
            return
        }

        viewModelScope.launch {
            val filteredAct = journal.getAllActivities().filter {
                it.name == _activity
            }
            if (filteredAct.isNotEmpty()) {
                throw IllegalStateException()
            }
        }.invokeOnCompletion {
            if (it != null) {
                activityError = ERROR_ACTIVITY_EXISTS
            }
        }
    }

    private suspend fun validateArea(): Area? {
        areaError = null

        return journal.getAllAreas().firstOrNull {
            it.name == _area
        }
    }

}