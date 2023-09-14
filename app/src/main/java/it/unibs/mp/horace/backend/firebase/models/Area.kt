package it.unibs.mp.horace.backend.firebase.models

data class Area(val id: String, val name: String) {
    companion object {
        const val COLLECTION_NAME = "areas"
        const val ID_FIELD = "id"
        const val NAME_FIELD = "name"

        fun parse(data: Map<String, Any>): Area {
            val id = data[ID_FIELD].toString()
            val name = data[NAME_FIELD].toString()
            return Area(id, name)
        }
    }

    fun stringify(): HashMap<String, Any> {
        return hashMapOf(
            ID_FIELD to id, NAME_FIELD to name
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

    override fun equals(other: Any?): Boolean {
        if (other !is Area) {
            return false
        }
        if (id == other.id) {
            return true
        }
        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}