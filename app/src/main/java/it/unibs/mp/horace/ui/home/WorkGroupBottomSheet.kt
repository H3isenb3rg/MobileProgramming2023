package it.unibs.mp.horace.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import it.unibs.mp.horace.backend.LoggedUser
import it.unibs.mp.horace.databinding.BottomSheetWorkGroupBinding

class WorkGroupBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetWorkGroupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetWorkGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val user = LoggedUser()
        binding.friends.adapter = WorkGroupAdapter(requireActivity(), user.friends)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}