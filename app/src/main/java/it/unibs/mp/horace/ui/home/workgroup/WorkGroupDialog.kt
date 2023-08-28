package it.unibs.mp.horace.ui.home.workgroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import it.unibs.mp.horace.backend.Settings
import it.unibs.mp.horace.backend.firebase.CurrentUser
import it.unibs.mp.horace.backend.firebase.models.User
import it.unibs.mp.horace.databinding.DialogWorkGroupBinding
import kotlinx.coroutines.launch

class WorkGroupDialog : BottomSheetDialogFragment() {
    private var _binding: DialogWorkGroupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogWorkGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val settings = Settings(requireContext())
        val user = CurrentUser()

        val workGroup: MutableList<User> = mutableListOf()
        val adapter = WorkGroupAdapter(workGroup) {
            lifecycleScope.launch { user.removeFromWorkGroup(it) }
        }

        binding.recyclerviewWorkGroup.adapter = adapter

        lifecycleScope.launch {
            workGroup.addAll(user.workGroup())

            // If the work group is empty, show a message
            if (workGroup.isEmpty()) {
                binding.textviewNoWorkGroup.visibility = View.VISIBLE
                binding.recyclerviewWorkGroup.visibility = View.GONE
                return@launch
            }

            // Sort according to the settings.
            if (settings.isWorkgroupSortAscending) workGroup.sortBy { it.username }
            else workGroup.sortByDescending { it.username }

            adapter.notifyItemRangeInserted(0, workGroup.size)
        }

        binding.buttonInvite.setOnClickListener {
            findNavController().navigate(
                WorkGroupDialogDirections.actionWorkGroupDialogToInviteFriendsDialog()
            )
        }

        binding.buttonSort.setOnClickListener {
            findNavController().navigate(
                WorkGroupDialogDirections.actionWorkGroupDialogToSortWorkGroupDialog()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}