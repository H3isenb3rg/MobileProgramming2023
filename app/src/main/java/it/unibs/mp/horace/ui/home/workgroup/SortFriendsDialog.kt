package it.unibs.mp.horace.ui.home.workgroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import it.unibs.mp.horace.backend.Settings
import it.unibs.mp.horace.databinding.DialogSortFriendsBinding

class SortFriendsDialog : BottomSheetDialogFragment() {
    private var _binding: DialogSortFriendsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogSortFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val settings = Settings(requireContext())

        binding.radiobuttonUsernameAscending.isChecked = settings.isFriendsSortAscending
        binding.radiobuttonUsernameDescending.isChecked = !settings.isFriendsSortAscending

        binding.radiogroupSortFriends.setOnCheckedChangeListener { _, checkedId ->
            settings.isFriendsSortAscending = checkedId == binding.radiobuttonUsernameAscending.id
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}