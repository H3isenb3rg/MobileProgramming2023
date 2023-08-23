package it.unibs.mp.horace.backend.firebase.models

/**
 * Represents an activity that can be tracked.
 */
data class Activity(
    val id: String, val name: String, val area: Area?
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
            activityMap[AREA_FIELD] = area.id
        }
        return activityMap
    }

    /**
     * Checks if the activity matches the search text.
     */
    fun fitsSearch(searchText: String): Boolean {
        return name.lowercase().contains(searchText.lowercase())
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Activity) {
            return false
        }
        if (id == other.id) {
            return true
        }
        return name == other.name && area == other.area
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (area?.hashCode() ?: 0)
        return result
    }
}