package it.unibs.mp.horace.ui.home.workgroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import it.unibs.mp.horace.backend.firebase.CurrentUser
import it.unibs.mp.horace.backend.firebase.UserNotificationManager
import it.unibs.mp.horace.backend.firebase.models.User
import it.unibs.mp.horace.databinding.DialogInviteFriendsBinding
import it.unibs.mp.horace.ui.shareUserProfile
import kotlinx.coroutines.launch

class InviteFriendsDialog : BottomSheetDialogFragment() {
    private var _binding: DialogInviteFriendsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogInviteFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val user = CurrentUser()
        val manager = UserNotificationManager()

        // Initialize values to empty list
        val friendsNotInWorkGroup: MutableList<User> = mutableListOf()
        var invited: MutableMap<User, Boolean> = mutableMapOf()

        val adapter =
            InviteFriendsAdapter(friendsNotInWorkGroup) { selection: User, isInvited: Boolean ->
                invited[selection] = isInvited
            }
        binding.recyclerviewAvailableFriends.adapter = adapter

        // Load actual values in background so the app doesn't freeze
        lifecycleScope.launch {
            friendsNotInWorkGroup.addAll(user.friendsNotInWorkGroup())

            // If there are no friends, show a message and a button to share the profile
            if (friendsNotInWorkGroup.isEmpty()) {
                binding.layoutNoFriends.visibility = View.VISIBLE
                binding.buttonShareProfile.setOnClickListener { requireContext().shareUserProfile() }
                return@launch
            }

            invited = friendsNotInWorkGroup.associateWith { false }.toMutableMap()

            adapter.notifyItemRangeInserted(0, friendsNotInWorkGroup.size)
        }

        binding.buttonInvite.setOnClickListener {
            lifecycleScope.launch {
                invited.filter { it.value }.forEach { manager.sendWorkGroupRequest(it.key) }
            }
            findNavController().navigate(
                InviteFriendsDialogDirections.actionInviteFriendsDialogToWorkGroupDialog()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}