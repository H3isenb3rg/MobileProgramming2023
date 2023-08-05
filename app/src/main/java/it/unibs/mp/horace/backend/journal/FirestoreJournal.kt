package it.unibs.mp.horace.backend.journal

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
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
     * Returns the [DocumentReference] to the current user document
     */
    private fun getUserDocument(uid: String): DocumentReference {
        return db.collection(User.COLLECTION_NAME).document(uid)
    }

    /**
     * Returns the [CollectionReference] to the entries collection of the specified user
     */
    private fun getEntriesCollection(uid: String): CollectionReference {
        return getUserDocument(uid).collection(TimeEntry.COLLECTION_NAME)
    }

    /**
     * Returns the [CollectionReference] to the activities collection of the specified user
     */
    private fun getActivitiesCollection(uid: String): CollectionReference {
        return getUserDocument(uid).collection(Activity.COLLECTION_NAME)
    }

    /**
     * Returns the [CollectionReference] to the area collection of the specified user
     */
    private fun getAreaCollection(uid: String): CollectionReference {
        return getUserDocument(uid).collection(Area.COLLECTION_NAME)
    }

    override suspend fun entries(): List<TimeEntry> {
        return userEntries(user.uid)
    }

    override suspend fun userEntries(userId: String): List<TimeEntry> {
        val querySnapshot = getEntriesCollection(userId).whereEqualTo(TimeEntry.OWNER_FIELD, userId).get()
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                throw exception
            }.addOnCanceledListener {
                Log.w(TAG, "Entries Fetch job cancelled")
                throw UnknownError("Perché dio madonna ha cancellato il job")
            }.await()

        val result: ArrayList<TimeEntry> = ArrayList()
        for (doc in querySnapshot) {
            result.add(TimeEntry.parse(doc.data))
        }
        return result
    }

    override suspend fun addEntry(raw_entry: HashMap<String, Any>): TimeEntry {
        val newDocument = getEntriesCollection(user.uid).document()
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
        val documents = getActivitiesCollection(userId).get()
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                throw exception
            }.await()

        for (document in documents) {
            val docData = document.data
            if (docData.containsKey(Activity.AREA_FIELD) && docData[Activity.AREA_FIELD] != null) {
                docData[Activity.AREA_FIELD] = getUserArea(userId, docData[Activity.AREA_FIELD].toString())
            }
            result.add(Activity.parse(docData))
        }
        return result
    }

    override suspend fun addActivity(raw_activity: HashMap<String, Any>): Activity {
        val newDocument = getActivitiesCollection(user.uid).document()
        raw_activity[Activity.ID_FIELD] = newDocument.id
        if (raw_activity.containsKey(Activity.AREA_FIELD) && raw_activity[Activity.AREA_FIELD] != null) {
            if (raw_activity[Activity.AREA_FIELD] is String) {
                raw_activity[Activity.AREA_FIELD] = getUserArea(user.uid, raw_activity[Activity.AREA_FIELD].toString())
            }
        }
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
        return userAreas(user.uid)
    }

    override suspend fun userAreas(uid: String): List<Area> {
        val result: ArrayList<Area> = ArrayList()
        val documents = getAreaCollection(uid).get()
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                throw exception
            }.await()

        for (document in documents) {
            result.add(Area.parse(document.data))
        }
        return result
    }

    override suspend fun addArea(name: String): Area {
        val newDocument = getAreaCollection(user.uid).document()
        val newID = newDocument.id
        val area: Area = Area.parse(hashMapOf(Area.ID_FIELD to newID, Area.NAME_FIELD to name))
        newDocument.set(area.stringify()).await()
        return area
    }

    override suspend fun updateArea(area: Area) {
        TODO("Not yet implemented")
    }

    override suspend fun removeArea(area: Area) {
        TODO("Not yet implemented")
    }

    override suspend fun getArea(areaID: String): Area {
        return getUserArea(user.uid, areaID)
    }

    override suspend fun getUserArea(userID: String, areaID: String): Area {
        val result = getAreaCollection(userID).document(areaID).get()
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                throw exception
            }.await()
        return Area.parse(result.data!!)
    }

    override suspend fun streak(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun increaseStreak() {
        TODO("Not yet implemented")
    }
}