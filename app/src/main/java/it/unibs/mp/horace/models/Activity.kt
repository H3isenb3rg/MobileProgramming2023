package it.unibs.mp.horace.models

data class Activity(var id: String, var name: String, var area: Area?) {

    companion object {
        const val COLLECTION_NAME = "activities"
        const val ID_FIELD = "id"
        const val NAME_FIELD = "name"
        const val AREA_FIELD = "area"

        fun parse(raw_data: Map<String, Any>): Activity{
            val id = raw_data[ID_FIELD].toString()
            val name = raw_data[NAME_FIELD].toString()
            var area: Area? = null
            if (raw_data.containsKey(AREA_FIELD)) {
                area = raw_data[AREA_FIELD] as Area
            }

            return Activity(id, name, area)
        }
    }

    // No-argument constructor required for Firestore.
    constructor() : this(
        "", "", null
    )

    fun stringify(): HashMap<String, Any> {
        val activityMap: HashMap<String, Any> = hashMapOf(
            ID_FIELD to id,
            NAME_FIELD to name
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