package it.unibs.mp.horace.ui.home.workgroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import it.unibs.mp.horace.backend.firebase.CurrentUser
import it.unibs.mp.horace.backend.firebase.models.User
import it.unibs.mp.horace.databinding.BottomSheetWorkGroupBinding
import kotlinx.coroutines.launch

class WorkGroupBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetWorkGroupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetWorkGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val workGroup: MutableList<User> = mutableListOf()
        val adapter = WorkGroupAdapter(workGroup)
        binding.workGroup.adapter = adapter

        lifecycleScope.launch {
            val user = CurrentUser()
            workGroup.addAll(user.workGroup())
            adapter.notifyItemRangeInserted(0, workGroup.size)
        }

        binding.invite.setOnClickListener {
            findNavController().navigate(
                WorkGroupBottomSheetDirections.actionWorkGroupBottomSheetToInviteFriendsBottomSheet()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}