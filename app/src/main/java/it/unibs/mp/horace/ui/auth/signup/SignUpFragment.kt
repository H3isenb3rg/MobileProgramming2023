package it.unibs.mp.horace.ui.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.ProfileValidator
import it.unibs.mp.horace.backend.firebase.CurrentUser
import it.unibs.mp.horace.databinding.FragmentSignUpBinding
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private val viewModel: SignUpViewModel by viewModels()

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val username get() = binding.textinputUsername.editText?.text.toString()
    private val email get() = binding.textinputEmail.editText?.text.toString()
    private val password get() = binding.textinputPassword.editText?.text.toString()
    private val passwordConfirm get() = binding.textinputPasswordConfirm.editText?.text.toString()
    private val terms get() = binding.checkboxTerms.isChecked

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = Firebase.auth
        addChangeListeners()

        binding.buttonSignUp.setOnClickListener {
            validateAll()

            if (!viewModel.isEverythingValid) {
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (!task.isSuccessful) {
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            binding.textinputEmail.error = getString(R.string.email_already_used)
                        } else {
                            Snackbar.make(
                                view, getString(R.string.api_error), Snackbar.LENGTH_LONG
                            ).show()
                        }
                        return@addOnCompleteListener
                    }

                    // Add username and save user to database.
                    lifecycleScope.launch {
                        val currentUser = CurrentUser()
                        currentUser.username = username
                        currentUser.update()
                        findNavController().navigate(
                            SignUpFragmentDirections.actionSignUpFragmentToHomeFragment()
                        )
                    }
                }
        }
    }

    private fun addChangeListeners() {
        binding.textinputUsername.editText?.addTextChangedListener {
            validateUsername()
        }
        binding.textinputEmail.editText?.addTextChangedListener {
            validateEmail()
        }
        binding.textinputPassword.editText?.addTextChangedListener {
            validatePassword()

            // Also verify password confirm when password changes.
            validatePasswordConfirm()
        }
        binding.textinputPasswordConfirm.editText?.addTextChangedListener {
            validatePasswordConfirm()
        }
        binding.checkboxTerms.setOnCheckedChangeListener { _, _ ->
            validateTerms()
        }
    }

    private fun validateAll() {
        validateUsername()
        validateEmail()
        validatePassword()
        validatePasswordConfirm()
        validateTerms()
    }

    private fun validateUsername() {
        binding.textinputUsername.error = when (viewModel.updateUsername(username)) {
            ProfileValidator.ERROR_USERNAME_LENGTH -> getString(R.string.username_min_length)
            ProfileValidator.ERROR_USERNAME_CHARS -> getString(R.string.username_chars)
            else -> null
        }
    }

    private fun validateEmail() {
        binding.textinputEmail.error =
            if (viewModel.updateEmail(email) == ProfileValidator.OK) null else getString(R.string.email_invalid)
    }

    private fun validatePassword() {
        binding.textinputPassword.error = when (viewModel.updatePassword(password)) {
            ProfileValidator.ERROR_PASSWORD_LENGTH -> getString(R.string.password_min_length)
            ProfileValidator.ERROR_PASSWORD_UPPERCASE -> getString(R.string.password_uppercase)
            ProfileValidator.ERROR_PASSWORD_LOWERCASE -> getString(R.string.password_lowercase)
            ProfileValidator.ERROR_PASSWORD_SPECIAL -> getString(R.string.password_special)
            else -> null
        }
    }

    private fun validatePasswordConfirm() {
        binding.textinputPasswordConfirm.error = if (viewModel.updatePasswordConfirm(
                password, passwordConfirm
            ) == ProfileValidator.OK
        ) null else getString(R.string.password_does_not_match)
    }

    private fun validateTerms() {
        binding.checkboxTerms.isErrorShown = !viewModel.updateTerms(terms)
        binding.checkboxTerms.errorAccessibilityLabel = getString(R.string.terms_error)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}