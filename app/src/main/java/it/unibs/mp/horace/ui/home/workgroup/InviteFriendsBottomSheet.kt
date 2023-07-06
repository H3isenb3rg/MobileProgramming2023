package it.unibs.mp.horace.ui.home.workgroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import it.unibs.mp.horace.backend.CurrentUser
import it.unibs.mp.horace.backend.User
import it.unibs.mp.horace.databinding.BottomSheetInviteFriendsBinding
import kotlinx.coroutines.launch

class InviteFriendsBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetInviteFriendsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetInviteFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val user = CurrentUser()

        // Initialize values to empty list
        var friendsNotInWorkGroup: List<User> = listOf()
        var invited: MutableMap<User, Boolean> = mutableMapOf()

        val adapter =
            InviteFriendsAdapter(friendsNotInWorkGroup) { selection: User, isInvited: Boolean ->
                invited[selection] = isInvited
            }
        binding.availableFriends.adapter = adapter

        // Load actual values in background so the app doesn't freeze
        lifecycleScope.launch {
            friendsNotInWorkGroup = user.friendsNotInWorkGroup()
            invited = friendsNotInWorkGroup.associateWith { false }.toMutableMap()

            adapter.notifyItemRangeInserted(0, friendsNotInWorkGroup.size)
        }

        binding.invite.setOnClickListener {
            lifecycleScope.launch {
                invited.filter { it.value }.forEach { user.sendWorkGroupRequest(it.key) }
            }
            findNavController().navigate(
                InviteFriendsBottomSheetDirections.actionInviteFriendsBottomSheetToWorkGroupBottomSheet()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}