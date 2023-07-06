package it.unibs.mp.horace.ui.leaderboard.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import it.unibs.mp.horace.MainActivity
import it.unibs.mp.horace.backend.CurrentUser
import it.unibs.mp.horace.backend.User
import it.unibs.mp.horace.databinding.FragmentFriendsBinding
import kotlinx.coroutines.launch


class FriendsFragment : Fragment() {
    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = CurrentUser()
        // Set friends initially to an empty list
        var friends = ArrayList<User>()
        val adapter = FriendsAdapter(friends)

        binding.fullFriendsList.adapter = adapter

        // Load friends in background
        lifecycleScope.launch {
            friends = ArrayList(user.friends())

            // Notify adapter of the new data
            adapter.notifyItemRangeInserted(0, friends.size)
        }

        (requireActivity() as MainActivity).hookSearchBar(binding.searchBar, adapter) { text ->
            adapter.filter.filter(text)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}