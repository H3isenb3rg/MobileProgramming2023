package it.unibs.mp.horace.models

data class Area(var id: String, var name: String) {

    // No-argument constructor required for Firestore.
    constructor() : this("", "")
}