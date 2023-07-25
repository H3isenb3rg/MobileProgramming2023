package it.unibs.mp.horace.backend.journal

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * Factory for creating concrete Journal instances.
 */
class JournalFactory {
    companion object {
        /**
         * Returns a Journal instance, depending on
         * whether the user is logged in or not.
         */
        fun getJournal(): Journal {
            return if (Firebase.auth.currentUser != null) {
                FirestoreJournal()
            } else {
                RoomJournal()
            }
        }
    }
}