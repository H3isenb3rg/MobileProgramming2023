package it.unibs.mp.horace.backend.journal

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.backend.CurrentUser
import it.unibs.mp.horace.models.Activity
import it.unibs.mp.horace.models.Area
import it.unibs.mp.horace.models.TimeEntry
import it.unibs.mp.horace.models.User
import kotlinx.coroutines.tasks.await

class FirestoreJournal : Journal {
    companion object {
        const val TAG = "Firestore Journal"
    }

    override suspend fun getCurrentUid(): String {
        return user.uid
    }

    /**
     * The Firebase Firestore database.
     */
    private val db: FirebaseFirestore = Firebase.firestore

    /**
     * User currently logged in
     */
    val user: CurrentUser = CurrentUser()

    /**
     * Reference to the current user document
     */
    private val userDocument = db.collection(User.COLLECTION_NAME).document(user.uid)

    /**
     * Reference to the current user entries collection
     */
    private val entriesCollection = userDocument.collection(TimeEntry.COLLECTION_NAME)

    /**
     * Reference to the current user activities collection
     */
    private val activitiesCollection = userDocument.collection(Activity.COLLECTION_NAME)

    override suspend fun entries(): List<TimeEntry> {
        return userEntries(user.uid)
    }

    override suspend fun userEntries(userId: String): List<TimeEntry> {
        val getEntriesTask = entriesCollection.whereEqualTo(TimeEntry.OWNER_FIELD, userId).get()
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                throw exception
            }.addOnCanceledListener {
                Log.w(TAG, "Entries Fetch job cancelled")
                throw UnknownError("Perché dio madonna ha cancellato il job")
            }
        getEntriesTask.await()
        val querySnapshot: QuerySnapshot = getEntriesTask.result
        val result: ArrayList<TimeEntry> = ArrayList()
        for (doc in querySnapshot) {
            result.add(TimeEntry.parse(doc.data))
        }
        return result
    }

    override suspend fun addEntry(raw_entry: HashMap<String, Any>): TimeEntry {
        val newDocument = entriesCollection.document()
        raw_entry[TimeEntry.ID_FIELD] = newDocument.id
        raw_entry[TimeEntry.OWNER_FIELD] = user.userData
        val timeEntry: TimeEntry = TimeEntry.parse(raw_entry)
        newDocument.set(timeEntry.stringify()).await()
        return timeEntry
    }

    override suspend fun updateEntry(entry: TimeEntry) {
        TODO("Not yet implemented")
    }

    override suspend fun removeEntry(entry: TimeEntry) {
        TODO("Not yet implemented")
    }

    override suspend fun activities(): List<Activity> {
        return userActivities(user.uid)
    }

    override suspend fun userActivities(userId: String): List<Activity> {
        val result: ArrayList<Activity> = ArrayList()
        activitiesCollection.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    result.add(Activity.parse(document.data))
                }
                return@addOnSuccessListener
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                throw exception
            }.await()
        return result
    }

    override suspend fun addActivity(raw_activity: HashMap<String, Any>): Activity {
        val newDocument = activitiesCollection.document()
        raw_activity[TimeEntry.ID_FIELD] = newDocument.id
        val activity: Activity = Activity.parse(raw_activity)
        newDocument.set(activity.stringify()).await()
        return activity
    }

    override suspend fun updateActivity(activity: Activity) {
        TODO("Not yet implemented")
    }

    override suspend fun removeActivity(activity: Activity) {
        TODO("Not yet implemented")
    }

    override suspend fun areas(): List<Area> {
        TODO("Not yet implemented")
    }

    override suspend fun addArea(area: Area) {
        TODO("Not yet implemented")
    }

    override suspend fun updateArea(area: Area) {
        TODO("Not yet implemented")
    }

    override suspend fun removeArea(area: Area) {
        TODO("Not yet implemented")
    }

    override suspend fun streak(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun increaseStreak() {
        TODO("Not yet implemented")
    }
}