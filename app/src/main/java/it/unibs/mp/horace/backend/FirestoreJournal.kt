package it.unibs.mp.horace.backend

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.models.Activity
import it.unibs.mp.horace.models.Area
import it.unibs.mp.horace.models.TimeEntry
import kotlinx.coroutines.tasks.await

class FirestoreJournal : Journal {
    companion object {
        const val ENTRY_COLLECTION_NAME = "entries"
        const val TAG = "Firestore Journal"
    }

    /**
     * The Firebase Firestore database.
     */
    private val db: FirebaseFirestore = Firebase.firestore

    override suspend fun entries(): List<TimeEntry> {
        TODO("Not yet implemented")
    }

    override suspend fun userEntries(userId: String): List<TimeEntry> {
        val result: ArrayList<TimeEntry> = ArrayList()
        db.collection(ENTRY_COLLECTION_NAME).whereEqualTo(TimeEntry.OWNER_FIELD, userId).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    result.add(TimeEntry.parse(document.data))
                }
                return@addOnSuccessListener
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                throw exception
            }.await()
        return result
    }

    override suspend fun addEntry(entry: TimeEntry) {
        val entriesCollection = db.collection(ENTRY_COLLECTION_NAME)
        val newDocument = entriesCollection.document()
        entry._id = newDocument.id
        newDocument.set(entry.stringify()).await()
    }

    override suspend fun updateEntry(entry: TimeEntry) {
        TODO("Not yet implemented")
    }

    override suspend fun removeEntry(entry: TimeEntry) {
        TODO("Not yet implemented")
    }

    override suspend fun activities(): List<Activity> {
        TODO("Not yet implemented")
    }

    override suspend fun addActivity(activity: Activity) {
        TODO("Not yet implemented")
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
}