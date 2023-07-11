package it.unibs.mp.horace.backend

data class Area(var name: String) {

    // No-argument constructor required for Firestore.
    constructor() : this("")
}