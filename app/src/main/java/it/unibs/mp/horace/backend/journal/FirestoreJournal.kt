package it.unibs.mp.horace.backend.journal

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.backend.CurrentUser
import it.unibs.mp.horace.backend.LeaderboardItem
import it.unibs.mp.horace.models.Activity
import it.unibs.mp.horace.models.Area
import it.unibs.mp.horace.models.TimeEntry
import it.unibs.mp.horace.models.User
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime

/**
 * Implementation of [Journal] that uses Firebase Firestore as a backend.
 * A [CurrentUser] instance is used to retrieve the current user's data.
 * This class is related only to the current user, and should not be used to
 * access other users' data.
 */
class FirestoreJournal : Journal {
    /**
     * The Firebase Firestore database.
     */
    private val db: FirebaseFirestore = Firebase.firestore

    /**
     * User currently logged in.
     */
    val user: CurrentUser = CurrentUser()

    /**
     * The [DocumentReference] to the current user document.
     */
    private val userDocument: DocumentReference =
        db.collection(User.COLLECTION_NAME).document(user.uid)

    /**
     * The [CollectionReference] to the entries collection of the user.
     */
    private val entriesCollection: CollectionReference =
        userDocument.collection(TimeEntry.COLLECTION_NAME)

    /**
     * The [CollectionReference] to the activities collection of the current user.
     */
    private val activitiesCollection: CollectionReference =
        userDocument.collection(Activity.COLLECTION_NAME)

    /**
     * The [CollectionReference] to the areas collection of the current user.
     */
    private val areasCollection: CollectionReference = userDocument.collection(Area.COLLECTION_NAME)

    override suspend fun getAllTimeEntries(): List<TimeEntry> {
        // Parse the data from the Firestore database and return a list of TimeEntry objects
        return entriesCollection.get().await().map { TimeEntry.parse(fillEntryMap(it.data)) }
    }

    override suspend fun getTimeEntry(id: String): TimeEntry? {
        // Retrieve document from Firestore
        val data = entriesCollection.document(id).get().await().data

        // If the document exists, parse it and return it
        return if (data != null) TimeEntry.parse(fillEntryMap(data)) else null
    }

    override suspend fun addTimeEntry(
        description: String?,
        activity: Activity?,
        isPomodoro: Boolean,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        points: Int
    ): TimeEntry {
        // Create a new document and set the Id field
        val newDocument = entriesCollection.document()
        val timeEntry = TimeEntry(
            id = newDocument.id,
            description = description,
            activity = activity,
            isPomodoro = isPomodoro,
            startTime = startTime,
            endTime = endTime,
            points = points
        )
        newDocument.set(timeEntry.stringify()).await()

        return timeEntry
    }

    override suspend fun updateTimeEntry(entry: TimeEntry) {
        entriesCollection.document(entry.id).set(entry.stringify()).await()
    }

    override suspend fun removeTimeEntry(entry: TimeEntry) {
        entriesCollection.document(entry.id).delete().await()
    }

    override suspend fun getAllActivities(): List<Activity> {
        return activitiesCollection.get().await().map { Activity.parse(fillActivityMap(it.data)) }
    }

    override suspend fun getActivity(id: String): Activity? {
        val data = activitiesCollection.document(id).get().await().data
        return if (data != null) Activity.parse(fillActivityMap(data)) else null
    }

    override suspend fun addActivity(name: String, area: Area?): Activity {
        val newDocument = activitiesCollection.document()
        val activity = Activity(newDocument.id, name, area)
        newDocument.set(activity.stringify()).await()

        return activity
    }

    override suspend fun updateActivity(activity: Activity) {
        activitiesCollection.document(activity.id).set(activity.stringify()).await()
    }

    override suspend fun removeActivity(activity: Activity) {
        activitiesCollection.document(activity.id).delete().await()
    }

    override suspend fun getAllAreas(): List<Area> {
        return areasCollection.get().await().map { Area.parse(it.data) }
    }

    override suspend fun addArea(name: String): Area {
        val newDocument = areasCollection.document()

        val area = Area(newDocument.id, name)
        newDocument.set(area.stringify()).await()

        return area
    }

    override suspend fun updateArea(area: Area) {
        areasCollection.document(area.id).set(area.stringify()).await()
    }

    override suspend fun removeArea(area: Area) {
        areasCollection.document(area.id).delete().await()
    }

    override suspend fun getArea(id: String): Area? {
        val data = areasCollection.document(id).get().await().data
        return if (data != null) Area.parse(data) else null
    }

    /**
     * Fills the given raw MutableMap of the Activity document.
     * Fills the Area field with the correct Area object
     */
    private suspend fun fillActivityMap(
        activity: MutableMap<String, Any>
    ): MutableMap<String, Any> {
        if (activity.containsKey(Activity.AREA_FIELD) && activity[Activity.AREA_FIELD] != null) {
            if (activity[Activity.AREA_FIELD] is String) {
                activity[Activity.AREA_FIELD] = getArea(activity[Activity.AREA_FIELD].toString())!!
            }
        }
        return activity
    }

    /**
     * Fills the given raw MutableMap of the TimeEntry document.
     * Fills the Activity field with the correct Activity object.
     */
    private suspend fun fillEntryMap(
        entry: MutableMap<String, Any>
    ): MutableMap<String, Any> {
        if (entry.containsKey(TimeEntry.ACTIVITY_FIELD) && entry[TimeEntry.ACTIVITY_FIELD] != null) {
            if (entry[TimeEntry.ACTIVITY_FIELD] is String) {
                entry[TimeEntry.ACTIVITY_FIELD] =
                    getActivity(entry[TimeEntry.ACTIVITY_FIELD].toString())!!
            }
        }
        return entry
    }

    suspend fun weeklyLeaderboard(): List<LeaderboardItem> {
        val leaderboard: MutableList<LeaderboardItem> = mutableListOf()

        // Add the current user to the leaderboard
        val userPointsInLastWeek = getAllTimeEntries()
            .filter { it.startTime.isAfter(LocalDateTime.now().minusWeeks(1)) }
            .sumOf { it.points }
        leaderboard.add(
            LeaderboardItem(user.userData, userPointsInLastWeek)
        )

        // Get the friends ids
        val friendsIds = userDocument.collection(User.FRIENDS_COLLECTION_NAME).get().await()
            .mapNotNull { it.getString(User.UID_FIELD) }

        // If the user has friends, add them to the leaderboard
        if (friendsIds.isNotEmpty()) {
            user.friends().forEach {
                val friendEntries = db.collection(User.COLLECTION_NAME).document(it.uid)
                    .collection(TimeEntry.COLLECTION_NAME).get().await()
                    .mapNotNull { entry -> TimeEntry.parse(fillEntryMap(entry.data)) }

                val lastWeekEntries = friendEntries.filter { entry ->
                    entry.isInCurrentWeek()
                }

                leaderboard.add(
                    LeaderboardItem(it, lastWeekEntries.sumOf { entry -> entry.points })
                )
            }
        }

        return leaderboard
    }
}