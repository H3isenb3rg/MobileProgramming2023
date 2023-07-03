package it.unibs.mp.horace.ui.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import it.unibs.mp.horace.TopLevelFragment
import it.unibs.mp.horace.databinding.FragmentLeaderboardBinding

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
