package it.unibs.mp.horace.backend

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoggedUser() {
    private var auth: FirebaseAuth = Firebase.auth

    private val user: FirebaseUser

    init {
        val loggedUser = auth.currentUser
        if (loggedUser != null) {
            // User is signed in
            user = loggedUser
            // TODO: use id to retrieve user data from DB and build complete user object
        } else {
            // No user is signed in
            throw IllegalAccessError("User is not logged")
        }
    }

}