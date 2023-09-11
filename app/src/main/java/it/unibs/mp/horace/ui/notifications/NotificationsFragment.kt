package it.unibs.mp.horace.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import it.unibs.mp.horace.backend.firebase.NotificationDay
import it.unibs.mp.horace.backend.firebase.UserNotificationManager
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
        val notificationsDays: MutableList<NotificationDay> = mutableListOf()

        val adapter = NotificationsDayAdapter(requireContext(), notificationsDays) {
            lifecycleScope.launch { manager.acceptInvitation(it) }
        }

        binding.recyclerviewNotifications.adapter = adapter

        // Load notification in background
        lifecycleScope.launch {
            val userNotifications = manager.fetchNotifications()

            // If there are no notifications, show a message
            if (userNotifications.isEmpty()) {
                binding.textviewNoNotifications.isVisible = true
                return@launch
            }

            binding.textviewNoNotifications.isVisible = false

            // Add notification to list and notify the adapter
            notificationsDays.addAll(NotificationDay.fromNotifications(userNotifications))
            adapter.notifyItemRangeInserted(0, notificationsDays.size)

            // Mark the displayed notifications as read
            userNotifications.forEach { manager.markNotificationAsRead(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}