package it.unibs.mp.horace.ui.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.backend.LeaderboardItem
import it.unibs.mp.horace.backend.firebase.CurrentUser
import it.unibs.mp.horace.backend.firebase.UserNotificationManager
import it.unibs.mp.horace.backend.firebase.models.User
import it.unibs.mp.horace.databinding.FragmentLeaderboardBinding
import it.unibs.mp.horace.ui.TopLevelFragment
import it.unibs.mp.horace.ui.shareUserProfile
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LeaderboardFragment : TopLevelFragment() {
    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardviewViewAllFriends.setOnClickListener {
            findNavController().navigate(
                LeaderboardFragmentDirections.actionLeaderboardFragmentToFriendsFragment()
            )
        }

        setupWeeklyLeaderboard()
        setupSuggestedFriends()
    }

    /**
     * Loads the weekly leaderboard and sets up the recycler view.
     */
    private fun setupWeeklyLeaderboard() {
        // List is initially empty
        val weeklyLeaderboard: MutableList<LeaderboardItem> = mutableListOf()

        val adapter = WeeklyLeaderboardAdapter(weeklyLeaderboard, requireContext())
        binding.recyclerviewWeeklyLeaderboard.adapter = adapter

        // Load the weekly leaderboard in background
        lifecycleScope.launch {
            val user = CurrentUser()

            // Add the user leaderboard item to the list, sorted by points
            weeklyLeaderboard.addAll(
                user.weeklyLeaderboard()
            )

            // If the leaderboard is empty, show the "no friends" message,
            // otherwise notify the adapter of the new items.
            if (weeklyLeaderboard.isEmpty()) {
                binding.recyclerviewWeeklyLeaderboard.isVisible = false
                binding.buttonShareProfile.setOnClickListener { requireContext().shareUserProfile() }
                binding.layoutNoFriends.isVisible = true
            } else {
                adapter.notifyItemRangeInserted(0, weeklyLeaderboard.size)
            }
        }
    }

    /**
     * Loads the suggested friends and sets up the carousel.
     */
    private fun setupSuggestedFriends() {
        // List of suggested friends, initially empty
        val suggestedFriends: MutableList<User> = mutableListOf()
        // The notification manager is used to send friend requests
        val manager = UserNotificationManager()

        // The adapter for the carousel
        val adapter = SuggestedFriendsAdapter(suggestedFriends) {
            // When a friend is clicked, send a friend request
            lifecycleScope.launch {
                manager.sendFriendRequest(it)
            }
        }
        binding.recyclerviewSuggestedFriends.adapter = adapter

        // Carousel settings
        binding.recyclerviewSuggestedFriends.apply {
            setFlat(true)
            setInfinite(true)
        }

        // Load the suggested friends
        lifecycleScope.launch {
            val user = CurrentUser()

            // Invalid ids are the user id and the ids of the user's friends
            val invalidIds = mutableListOf(user.uid)
            invalidIds.addAll(user.friends().map { it.uid })

            // Query all users that are not the current user
            val userNotFriends = Firebase.firestore.collection(User.COLLECTION_NAME)
                .whereNotIn(User.UID_FIELD, invalidIds)
                .limit(5).get().await()

            if (userNotFriends.isEmpty) {
                binding.recyclerviewSuggestedFriends.isVisible = false
                return@launch
            }

            suggestedFriends.addAll(userNotFriends.mapNotNull { User.parse(it.data) })
            adapter.notifyItemRangeInserted(0, suggestedFriends.size)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
