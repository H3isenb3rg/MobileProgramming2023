package it.unibs.mp.horace.ui.auth.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.ProfileValidator
import it.unibs.mp.horace.R
import it.unibs.mp.horace.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private val args: SignInFragmentArgs by navArgs()
    private val viewModel: SignInViewModel by viewModels()

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private val email get() = binding.email.editText?.text.toString()
    private val password get() = binding.password.editText?.text.toString()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = Firebase.auth

        if (args.sourceResetPassword) {
            Snackbar.make(
                view, "Password reset link sent", Snackbar.LENGTH_LONG
            ).show()
        }

        addChangeListeners()

        binding.signIn.setOnClickListener {
            validateAll()

            if (!viewModel.isEverythingValid) {
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        findNavController().navigate(
                            SignInFragmentDirections.actionSignInFragmentToHomeFragment()
                        )
                    } else {
                        Snackbar.make(
                            view,
                            getString(R.string.invalid_email_or_password),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
        }

        binding.signUp.setOnClickListener {
            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToSignUpFragment())
        }

        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(
                SignInFragmentDirections.actionSignInFragmentToResetPasswordFragment(
                    binding.email.editText?.text.toString()
                )
            )
        }
    }

    private fun addChangeListeners() {
        binding.email.editText?.addTextChangedListener {
            validateEmail()
        }
        binding.password.editText?.addTextChangedListener {
            validatePassword()
        }
    }

    private fun validateAll() {
        validateEmail()
        validatePassword()
    }

    private fun validateEmail() {
        binding.email.error =
            if (viewModel.updateEmail(email) == ProfileValidator.OK) null else getString(R.string.email_invalid)
    }

    private fun validatePassword() {
        binding.password.error =
            if (viewModel.updatePassword(password) == ProfileValidator.OK) null else getString(R.string.password_min_length)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}