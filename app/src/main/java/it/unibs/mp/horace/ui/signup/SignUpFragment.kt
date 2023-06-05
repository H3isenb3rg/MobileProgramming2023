package it.unibs.mp.horace.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.R
import it.unibs.mp.horace.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private val viewModel: SignUpViewModel by viewModels()

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val email get() = binding.email.editText?.text.toString()
    private val password get() = binding.password.editText?.text.toString()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = Firebase.auth

        addChangeListeners()

        binding.signUp.setOnClickListener {
            if (!viewModel.isEverythingValid) {
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        findNavController().navigate(SignUpFragmentDirections.actionGlobalHomeFragment())
                    } else {
                        Snackbar.make(
                            view, getString(R.string.sign_up_firebase_error), Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    private fun addChangeListeners() {
        binding.email.editText?.doOnTextChanged { text, _, _, _ ->
            if (!viewModel.validateEmail(text.toString())) {
                binding.email.error = getString(R.string.email_invalid)
            }
        }

        binding.password.editText?.doOnTextChanged { text, _, _, _ ->
            binding.email.error = when (viewModel.validatePassword(text.toString())) {
                SignUpViewModel.ERROR_PASSWORD_LENGTH -> getString(R.string.password_min_length)
                SignUpViewModel.ERROR_PASSWORD_UPPERCASE -> getString(R.string.password_uppercase)
                SignUpViewModel.ERROR_PASSWORD_LOWERCASE -> getString(R.string.password_lowercase)
                SignUpViewModel.ERROR_PASSWORD_SPECIAL -> getString(R.string.password_special)
                else -> null
            }
        }

        binding.passwordConfirm.editText?.doOnTextChanged { text, _, _, _ ->
            if (!viewModel.validatePasswordConfirm(
                    password, text.toString()
                )
            ) {
                binding.passwordConfirm.error = getString(R.string.password_does_not_match)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}