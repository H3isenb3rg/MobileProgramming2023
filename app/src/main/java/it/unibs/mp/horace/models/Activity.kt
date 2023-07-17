package it.unibs.mp.horace.models

data class Activity(var id: String, var name: String, var area: Area?) {

    // No-argument constructor required for Firestore.
    constructor() : this(
        "", "", null
    )
}