package it.unibs.mp.horace.ui.home.selectactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import it.unibs.mp.horace.backend.Settings
import it.unibs.mp.horace.databinding.DialogSortActivitiesBinding

class SortActivitiesDialog : BottomSheetDialogFragment() {
    private var _binding: DialogSortActivitiesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogSortActivitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val settings = Settings(requireContext())

        binding.radiobuttonNameAscending.isChecked = settings.isActivitySortAscending
        binding.radiobuttonNameDescending.isChecked = !settings.isActivitySortAscending

        binding.radiogroupSortActivities.setOnCheckedChangeListener { _, checkedId ->
            settings.isActivitySortAscending = checkedId == binding.radiobuttonNameAscending.id
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}