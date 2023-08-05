package it.unibs.mp.horace.models

data class Area(var id: String, var name: String) {
    companion object {
        const val COLLECTION_NAME = "areas"
        const val ID_FIELD = "id"
        const val NAME_FIELD = "name"

        fun parse(raw_data: Map<String, Any>): Area{
            val id = raw_data[ID_FIELD].toString()
            val name = raw_data[NAME_FIELD].toString()
            return Area(id, name)
        }
    }

    fun stringify(): HashMap<String, Any> {
        return hashMapOf(
            ID_FIELD to id,
            NAME_FIELD to name
        )
    }

    // No-argument constructor required for Firestore.
    constructor() : this("", "")

    /**
     * Checks if the user matches the search text.
     */
    fun fitsSearch(searchText: String): Boolean {
        return name.lowercase().contains(searchText.lowercase())
    }
}