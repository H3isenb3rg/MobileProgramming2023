package it.unibs.mp.horace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import it.unibs.mp.horace.databinding.FragmentAuthBinding

class AuthFragment : Fragment() {
    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)

        binding.btnSignIn.setOnClickListener {
            val action = AuthFragmentDirections.actionAuthFragmentToSignInFragment()
            findNavController().navigate(action)
        }
        binding.btnSignUp.setOnClickListener {
            val action = AuthFragmentDirections.actionAuthFragmentToSignUpFragment()
            findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}