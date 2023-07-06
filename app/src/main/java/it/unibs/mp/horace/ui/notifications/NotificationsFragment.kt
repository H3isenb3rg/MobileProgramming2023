package it.unibs.mp.horace.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import it.unibs.mp.horace.backend.CurrentUser
import it.unibs.mp.horace.backend.Invitation
import it.unibs.mp.horace.databinding.FragmentNotificationsBinding
import kotlinx.coroutines.launch

class NotificationsFragment : Fragment() {
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val invitations: MutableList<Invitation> = mutableListOf()

        val adapter = NotificationsAdapter(requireContext(), invitations)
        binding.notificationsList.adapter = adapter

        lifecycleScope.launch {
            val user = CurrentUser()
            val userInvitations = user.invitations()
            invitations.addAll(userInvitations)
            if (userInvitations.isEmpty()) {
                binding.noNotificationsText.visibility = View.VISIBLE
            } else {
                binding.noNotificationsText.visibility = View.GONE
            }
            adapter.notifyItemRangeInserted(0, invitations.size)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}