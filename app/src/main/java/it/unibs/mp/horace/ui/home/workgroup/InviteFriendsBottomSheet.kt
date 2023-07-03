package it.unibs.mp.horace.ui.home.workgroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import it.unibs.mp.horace.backend.CurrentUser
import it.unibs.mp.horace.backend.User
import it.unibs.mp.horace.databinding.BottomSheetInviteFriendsBinding

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
        val invited = user.friendsNotInWorkGroup.associateWith { false }.toMutableMap()
        binding.availableFriends.adapter =
            InviteFriendsAdapter(user.friendsNotInWorkGroup) { selection: User, isInvited: Boolean ->
                invited[selection] = isInvited
            }
        binding.invite.setOnClickListener {
            invited.filter { it.value }.forEach { user.invite(it.key) }
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