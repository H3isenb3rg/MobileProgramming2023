package it.unibs.mp.horace.ui.settings.updateprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import it.unibs.mp.horace.backend.CurrentUser
import it.unibs.mp.horace.databinding.BottomSheetConfirmDeleteBinding
import kotlinx.coroutines.launch

class BottomSheetConfirmDelete : BottomSheetDialogFragment() {
    private var _binding: BottomSheetConfirmDeleteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetConfirmDeleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.confirm.setOnClickListener {
            lifecycleScope.launch {
                CurrentUser().delete()

                findNavController().navigate(
                    BottomSheetConfirmDeleteDirections.actionBottomSheetConfirmDeleteToHomeFragment()
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}