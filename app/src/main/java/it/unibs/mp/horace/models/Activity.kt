package it.unibs.mp.horace.models

data class Activity(var id: String, var name: String, var area: Area?) {

    // No-argument constructor required for Firestore.
    constructor() : this(
        "", "", null
    )

    /**
     * Checks if the user matches the search text.
     */
    fun fitsSearch(searchText: String): Boolean {
        return name.lowercase().contains(searchText.lowercase())
    }
}