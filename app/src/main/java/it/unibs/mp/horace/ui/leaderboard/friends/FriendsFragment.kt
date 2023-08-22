package it.unibs.mp.horace.ui.leaderboard.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import it.unibs.mp.horace.backend.Settings
import it.unibs.mp.horace.backend.firebase.CurrentUser
import it.unibs.mp.horace.backend.firebase.models.User
import it.unibs.mp.horace.databinding.FragmentFriendsBinding
import it.unibs.mp.horace.ui.MainActivity
import it.unibs.mp.horace.ui.SortFragment
import it.unibs.mp.horace.ui.shareUserProfile
import kotlinx.coroutines.launch


class FriendsFragment : SortFragment() {
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

        val settings = Settings(requireContext())

        // Set friends initially to an empty list
        val friends = ArrayList<User>()
        val adapter = FriendsAdapter(friends)
        binding.recyclerviewFriends.adapter = adapter

        // Load friends in background
        lifecycleScope.launch {
            friends.addAll(CurrentUser().friends())

            if (friends.isEmpty()) {
                binding.layoutNoFriends.visibility = View.VISIBLE
                binding.buttonShareProfile.setOnClickListener { requireContext().shareUserProfile() }
                return@launch
            }

            if (settings.isFriendsSortAscending) friends.sortBy { it.username }
            else friends.sortByDescending { it.username }

            // Notify adapter of the new data
            adapter.notifyItemRangeInserted(0, friends.size)
        }

        // Hook search bar to search view.
        // On text change, filter the adapter.
        (requireActivity() as MainActivity).hookSearchBar(binding.searchbar, adapter)
    }

    override fun onSortSelected() {
        findNavController().navigate(FriendsFragmentDirections.actionFriendsFragmentToSortFriendsDialog())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}