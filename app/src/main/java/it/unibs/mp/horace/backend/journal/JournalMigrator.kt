package it.unibs.mp.horace.backend.journal

import it.unibs.mp.horace.backend.firebase.models.Activity
import it.unibs.mp.horace.backend.firebase.models.Area

class JournalMigrator(private val source: Journal, private val destination: Journal) {

    suspend fun migrate() {
        val sourceEntries = source.getAllTimeEntries()
        val destinationEntries = destination.getAllTimeEntries()

        sourceEntries.forEach {
            if (!destinationEntries.contains(it)) {
                val activity = if (it.activity != null) getOrAddActivity(it.activity) else null
                destination.addTimeEntry(
                    it.description,
                    activity,
                    it.isPomodoro,
                    it.startTime,
                    it.endTime,
                    it.points
                )
            }
        }
    }

    private suspend fun getOrAddActivity(activity: Activity): Activity {
        val area = if (activity.area != null) getOrAddArea(activity.area) else null

        return destination.getAllActivities().find { it == activity }
            ?: destination.addActivity(activity.name, area)
    }

    private suspend fun getOrAddArea(area: Area): Area {
        return destination.getAllAreas().find { it == area }
            ?: destination.addArea(area.name)
    }

}
