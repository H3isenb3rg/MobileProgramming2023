package it.unibs.mp.horace.ui.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.TopLevelFragment
import it.unibs.mp.horace.backend.CurrentUser
import it.unibs.mp.horace.backend.User
import it.unibs.mp.horace.backend.UserNotificationManager
import it.unibs.mp.horace.databinding.FragmentLeaderboardBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

        binding.viewAllFriends.setOnClickListener {
            findNavController().navigate(
                LeaderboardFragmentDirections.actionLeaderboardFragmentToFriendsFragment()
            )
        }

        if (Firebase.auth.currentUser == null) {
            return
        }

        val suggestedFriends: MutableList<User> = mutableListOf()

        val manager = UserNotificationManager()
        fun sendFriendRequest(user: User) {
            lifecycleScope.launch {
                manager.sendFriendRequest(user)
                suggestedFriends.remove(user)
            }
        }

        val adapter = SuggestedFriendsAdapter(suggestedFriends, ::sendFriendRequest)

        binding.suggestedFriends.adapter = adapter
        binding.suggestedFriends.apply {
            setFlat(true)
            setInfinite(true)
        }

        runBlocking {
            val user = CurrentUser()

            var query = Firebase.firestore.collection(User.COLLECTION_NAME)
                .whereNotEqualTo(User.UID_FIELD, user.uid)

            val friendsIds = user.friends().map { it.uid }

            if (friendsIds.isNotEmpty()) {
                query = query.whereNotIn(User.UID_FIELD, friendsIds)
            }

            val userNotFriends = query.limit(4).get().await()

            suggestedFriends.addAll(userNotFriends.toObjects(User::class.java))
            adapter.notifyItemRangeInserted(0, suggestedFriends.size)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
