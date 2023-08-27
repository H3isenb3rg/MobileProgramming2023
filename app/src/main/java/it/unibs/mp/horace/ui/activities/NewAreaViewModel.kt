package it.unibs.mp.horace.ui.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import it.unibs.mp.horace.backend.journal.Journal
import kotlinx.coroutines.launch

/**
 * Factory for creating a [NewAreaViewModel] with a constructor that takes a [Journal].
 * Required given that [NewAreaViewModel] takes a constructor argument.
 */
class NewAreaViewModelFactory(private val journal: Journal) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewAreaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return NewAreaViewModel(journal) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class NewAreaViewModel(val journal: Journal) : ViewModel() {
    companion object {
        const val ERROR_AREA_NULL = "Area is required"
        const val ERROR_AREA_EXISTS = "Area already exists"
    }

    private var _name: String? = null

    /**
     * The name of the area.
     */
    var name: String?
        get() = _name
        set(value) {
            _name = value

            // Validate the area in background.
            viewModelScope.launch { validateArea() }
        }

    var error: String? = null
        private set

    /**
     * Save the area to the journal, if everything is valid.
     * Otherwise throws an [IllegalStateException].
     */
    suspend fun save() {
        validateArea()

        if (error != null) {
            throw IllegalStateException(error)
        }

        journal.addArea(name!!)
    }

    private suspend fun validateArea() {
        if (name == null) {
            error = ERROR_AREA_NULL
            return
        }

        if (journal.getAllAreas().any { it.name == name }) {
            error = ERROR_AREA_EXISTS
        }
    }

}
