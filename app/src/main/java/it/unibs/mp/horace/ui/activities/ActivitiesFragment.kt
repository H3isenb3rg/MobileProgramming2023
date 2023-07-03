package it.unibs.mp.horace.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import it.unibs.mp.horace.TopLevelFragment
import it.unibs.mp.horace.databinding.FragmentActivitiesBinding

class ActivitiesFragment : TopLevelFragment() {
    private var _binding: FragmentActivitiesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActivitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewJournal.setOnClickListener {
            findNavController().navigate(
                ActivitiesFragmentDirections.actionActivitiesFragmentToHistoryFragment()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}