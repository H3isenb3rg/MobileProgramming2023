package it.unibs.mp.horace.ui.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibs.mp.horace.backend.journal.JournalFactory
import it.unibs.mp.horace.models.Activity
import it.unibs.mp.horace.models.Area
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class NewActivityViewModel : ViewModel() {
    companion object {
        const val ERROR_ACTIVITY_NULL = "Activity is required"
        const val ERROR_ACTIVITY_EXISTS = "Activity already exists"
        // FIXME: Fa cagare, per il momento segnalo così che sarà creata una nuova area
        const val WARNING_AREA_EXISTS = "WARNING: new Area"
    }

    /**
     * The activities journal
     */
    val journal = JournalFactory.getJournal()

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
            validateArea()
        }

    var activityError: String? = null
        private set

    var areaError: String? = null
        private set

    private val isEverythingValid get() =
        activityError == null && areaError == null

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
        val rawActivity: HashMap<String, Any>

        if (area != null) {
            if (currArea == null) {
                currArea = journal.addArea(area!!)
            }
            rawActivity = hashMapOf(
                Activity.NAME_FIELD to activity!!,
                Activity.AREA_FIELD to currArea
            )
        } else {
            rawActivity = hashMapOf(Activity.NAME_FIELD to activity!!)
        }

        journal.addActivity(rawActivity)
    }

    // TODO: Decidere se possono esistere activity con stesso nome in aree diverse o no
    private fun validateActivity() {
        activityError = when (_activity) {
            null -> ERROR_ACTIVITY_NULL
            else -> null
        }
        if (activityError != null) {
            return
        }

        viewModelScope.launch {
            val filteredAct = journal.activities().filter {
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

    private fun validateArea(): Area? {
        areaError = null
        var currArea: Area?

        runBlocking {
            currArea = viewModelScope.run {
                try {
                    return@run journal.areas().single {
                        it.name == _area
                    }
                } catch (e: NoSuchElementException) {
                    return@run null
                }
            }
        }
        return currArea
    }

}