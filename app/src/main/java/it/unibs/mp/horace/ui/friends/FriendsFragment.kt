package it.unibs.mp.horace.ui.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.unibs.mp.horace.MainActivity
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
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).hookSearchBar(binding.searchBar)

        val user = LoggedUser()

        val adapter = FriendsAdapter(user.friends)
        binding.fullFriendsList.adapter = adapter


        /**
        binding.filteredFriendsList.adapter = adapter

        binding.searchView.editText.addTextChangedListener {
        val searchText = binding.searchView.text.toString()
        adapter.filter.filter(searchText)
        }

        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
        val searchText = binding.searchView.text.toString()
        adapter.filter.filter(searchText)
        binding.searchBar.text = searchText
        binding.searchView.hide();
        true
        }
         */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}