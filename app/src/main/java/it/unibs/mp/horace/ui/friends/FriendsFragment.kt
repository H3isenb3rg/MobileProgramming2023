package it.unibs.mp.horace.ui.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.unibs.mp.horace.TopLevelFragment
import it.unibs.mp.horace.backend.LoggedUser
import it.unibs.mp.horace.databinding.FragmentFriendsBinding

class FriendsFragment : TopLevelFragment() {
    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val user = LoggedUser()
        binding.workGroup.adapter = FriendsAdapter(requireActivity(), user.friends)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}