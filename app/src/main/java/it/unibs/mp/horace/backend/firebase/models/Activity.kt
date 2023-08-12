package it.unibs.mp.horace.backend.firebase.models

/**
 * Represents an activity that can be tracked.
 */
data class Activity(
    var id: String, var name: String, var area: Area?
) {
    companion object {
        const val COLLECTION_NAME = "activities"
        const val ID_FIELD = "id"
        const val NAME_FIELD = "name"
        const val AREA_FIELD = "area"

        /**
         * Parses a map into an Activity object.
         */
        fun parse(data: Map<String, Any>): Activity {
            val id = data[ID_FIELD].toString()
            val name = data[NAME_FIELD].toString()
            val area = if (data.containsKey(AREA_FIELD) && data[AREA_FIELD] != null) {
                data[AREA_FIELD] as Area
            } else {
                null
            }

            return Activity(id, name, area)
        }
    }

    // No-argument constructor required for Firestore.
    constructor() : this(
        "", "", null
    )

    /**
     * Parses the object into a map.
     */
    fun stringify(): HashMap<String, Any> {
        val activityMap: HashMap<String, Any> = hashMapOf(
            ID_FIELD to id, NAME_FIELD to name
        )
        if (area != null) {
            activityMap[AREA_FIELD] = area!!.id
        }
        return activityMap
    }

    /**
     * Checks if the user matches the search text.
     */
    fun fitsSearch(searchText: String): Boolean {
        return name.lowercase().contains(searchText.lowercase())
    }
}