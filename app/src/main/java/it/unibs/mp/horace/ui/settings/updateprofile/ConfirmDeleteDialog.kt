package it.unibs.mp.horace.ui.settings.updateprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import it.unibs.mp.horace.backend.firebase.CurrentUser
import it.unibs.mp.horace.databinding.DialogConfirmDeleteBinding
import kotlinx.coroutines.launch

class ConfirmDeleteDialog : BottomSheetDialogFragment() {
    private var _binding: DialogConfirmDeleteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogConfirmDeleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonConfirm.setOnClickListener {
            lifecycleScope.launch {
                CurrentUser().delete()

                findNavController().navigate(
                    ConfirmDeleteDialogDirections.actionDialogConfirmDeleteToHomeFragment()
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}