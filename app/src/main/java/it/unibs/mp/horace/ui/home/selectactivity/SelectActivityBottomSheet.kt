package it.unibs.mp.horace.ui.home.selectactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import it.unibs.mp.horace.backend.journal.JournalFactory
import it.unibs.mp.horace.databinding.BottomSheetSelectActivityBinding
import it.unibs.mp.horace.models.Activity
import kotlinx.coroutines.launch

class SelectActivityBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetSelectActivityBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSelectActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activities: MutableList<Activity> = mutableListOf()
        val adapter = SelectActivityAdapter(activities)

        binding.activities.adapter = adapter

        lifecycleScope.launch {
            val journal = JournalFactory.getJournal(requireContext())
            activities.addAll(journal.getAllActivities())
            adapter.notifyItemRangeInserted(0, activities.size)
        }

        binding.addNewActivity.setOnClickListener {
            findNavController().navigate(
                SelectActivityBottomSheetDirections.actionGlobalNewActivity()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}