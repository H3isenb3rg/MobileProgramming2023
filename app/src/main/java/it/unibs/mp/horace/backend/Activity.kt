package it.unibs.mp.horace.backend

data class Activity(var name: String, var area: Area?) {

    // No-argument constructor required for Firestore.
    constructor() : this("", null)
}