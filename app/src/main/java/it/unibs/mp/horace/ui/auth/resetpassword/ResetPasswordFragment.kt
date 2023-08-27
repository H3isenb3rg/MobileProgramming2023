package it.unibs.mp.horace.ui.auth.resetpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.R
import it.unibs.mp.horace.databinding.FragmentResetPasswordBinding

class ResetPasswordFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private val args: ResetPasswordFragmentArgs by navArgs()
    private val viewModel: ResetPasswordViewModel by viewModels()

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!
    private val email get() = binding.textinputEmail.editText?.text.toString()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = Firebase.auth
        if (args.email != null) {
            binding.textinputEmail.editText?.setText(args.email)
        }

        binding.buttonSendResetLink.setOnClickListener {
            validateEmail()

            if (!viewModel.isEverythingValid) {
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email).addOnCompleteListener {
                findNavController().navigate(
                    ResetPasswordFragmentDirections.actionResetPasswordFragmentToSignInFragment()
                )
            }
        }
    }

    private fun validateEmail() {
        binding.textinputEmail.error =
            if (viewModel.updateEmail(email)) null else getString(R.string.email_invalid)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}