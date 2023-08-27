package it.unibs.mp.horace.ui.home.workgroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import it.unibs.mp.horace.backend.Settings
import it.unibs.mp.horace.databinding.DialogSortWorkgroupBinding

class SortWorkGroupDialog : BottomSheetDialogFragment() {
    private var _binding: DialogSortWorkgroupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogSortWorkgroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val settings = Settings(requireContext())

        binding.radiobuttonUsernameAscending.isChecked = settings.isWorkgroupSortAscending
        binding.radiobuttonUsernameDescending.isChecked = !settings.isWorkgroupSortAscending

        binding.radiogroupSortWorkgroup.setOnCheckedChangeListener { _, checkedId ->
            settings.isWorkgroupSortAscending = checkedId == binding.radiobuttonUsernameAscending.id
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}