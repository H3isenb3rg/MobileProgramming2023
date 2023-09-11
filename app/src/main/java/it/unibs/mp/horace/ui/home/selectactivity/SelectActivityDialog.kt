package it.unibs.mp.horace.ui.home.selectactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.Settings
import it.unibs.mp.horace.backend.firebase.models.Activity
import it.unibs.mp.horace.backend.journal.JournalFactory
import it.unibs.mp.horace.databinding.DialogSelectActivityBinding
import kotlinx.coroutines.launch

class SelectActivityDialog : BottomSheetDialogFragment() {
    private var _binding: DialogSelectActivityBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogSelectActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // The settings are required to retrieve the sort order.
        val settings = Settings(requireContext())

        val journal = JournalFactory(requireContext()).getJournal()

        // Create the adapter and the list of activities.
        val activities: MutableList<Activity> = mutableListOf()
        val adapter = SelectActivityAdapter(activities) { activity, isLongClick ->
            if (isLongClick) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getString(R.string.dialog_select_activity_delete_activity_title))
                    .setMessage(resources.getString(R.string.dialog_select_activity_delete_activity_description))
                    .setNegativeButton(
                        resources.getString(R.string.dialog_select_activity_cancel),
                        null
                    )
                    .setPositiveButton(resources.getString(R.string.dialog_select_activity_delete_activity_delete)) { _, _ ->
                        lifecycleScope.launch {
                            journal.removeActivity(activity)
                            activities.remove(activity)
                            binding.recyclerviewActivities.adapter?.notifyItemRemoved(
                                activities.indexOf(
                                    activity
                                )
                            )
                        }
                    }
                    .show()
            } else {
                findNavController().navigate(
                    SelectActivityDialogDirections.actionGlobalHome(activityId = activity.id)
                )
            }
        }

        binding.recyclerviewActivities.adapter = adapter

        lifecycleScope.launch {
            // Retrieve the activities from the journal.
            activities.addAll(journal.getAllActivities())

            // Sort according to the settings.
            if (settings.isSelectActivitySortAscending) activities.sortBy { it.name }
            else activities.sortByDescending { it.name }

            adapter.notifyItemRangeInserted(0, activities.size)

            binding.recyclerviewActivities.isVisible = activities.isNotEmpty()
            binding.textviewNoActivities.isVisible = activities.isEmpty()
        }

        binding.buttonAddNewActivity.setOnClickListener {
            findNavController().navigate(
                SelectActivityDialogDirections.actionGlobalNewActivity()
            )
        }

        binding.buttonSort.setOnClickListener {
            findNavController().navigate(
                SelectActivityDialogDirections.actionSelectActivityDialogToSortActivitiesDialog()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}