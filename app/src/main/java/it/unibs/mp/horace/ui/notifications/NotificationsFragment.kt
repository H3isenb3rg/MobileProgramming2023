package it.unibs.mp.horace.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import it.unibs.mp.horace.backend.Notification
import it.unibs.mp.horace.backend.UserNotificationManager
import it.unibs.mp.horace.databinding.FragmentNotificationsBinding
import kotlinx.coroutines.launch

class NotificationsFragment : Fragment() {
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val manager = UserNotificationManager()

        // List of notifications is initially empty
        val notifications: MutableList<Notification> = mutableListOf()

        // What to do when the current user performs an action on a notification
        fun onAction(notification: Notification) {
            lifecycleScope.launch { manager.acceptInvitation(notification) }
        }

        val adapter = NotificationsAdapter(requireContext(), notifications, ::onAction)
        binding.notificationsList.adapter = adapter

        // Load notification in background
        lifecycleScope.launch {
            val userNotifications = manager.notifications()

            // If there are no notifications, show a message
            if (userNotifications.isEmpty()) {
                binding.noNotificationsText.visibility = View.VISIBLE
                return@launch
            }

            binding.noNotificationsText.visibility = View.GONE

            // Add notification to list and notify the adapter
            notifications.addAll(userNotifications)
            adapter.notifyItemRangeInserted(0, notifications.size)

            // Mark the displayed notifications as read
            notifications.forEach { manager.markNotificationAsRead(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}