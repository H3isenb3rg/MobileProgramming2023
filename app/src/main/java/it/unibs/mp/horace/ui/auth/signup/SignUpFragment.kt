package it.unibs.mp.horace.ui.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.CurrentUser
import it.unibs.mp.horace.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private val viewModel: SignUpViewModel by viewModels()

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val username get() = binding.username.editText?.text.toString()
    private val email get() = binding.email.editText?.text.toString()
    private val password get() = binding.password.editText?.text.toString()
    private val passwordConfirm get() = binding.passwordConfirm.editText?.text.toString()
    private val terms get() = binding.terms.isChecked

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
            validateAll()

            if (!viewModel.isEverythingValid) {
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        val currentUser = CurrentUser()
                        currentUser.username = username
                        FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
                            if (!tokenTask.isSuccessful) {
                                throw IllegalStateException("Could not get FCM token")
                            }
                            // Get new FCM registration token
                            currentUser.fcmToken = task.result.toString()
                            currentUser.update()
                        }

                        findNavController().navigate(
                            SignUpFragmentDirections.actionGlobalHome(
                                resources.getString(R.string.sign_up)
                            )
                        )
                    } else {
                        Snackbar.make(
                            view, getString(R.string.api_error), Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    private fun addChangeListeners() {
        binding.username.editText?.addTextChangedListener {
            validateUsername()
        }
        binding.email.editText?.addTextChangedListener {
            validateEmail()
        }
        binding.password.editText?.addTextChangedListener {
            validatePassword()
        }
        binding.passwordConfirm.editText?.addTextChangedListener {
            validatePasswordConfirm()
        }
        binding.terms.setOnCheckedChangeListener { _, _ ->
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
        binding.username.error = when (viewModel.updateUsername(username)) {
            SignUpViewModel.ERROR_USERNAME_LENGTH -> getString(R.string.username_min_length)
            SignUpViewModel.ERROR_USERNAME_CHARS -> getString(R.string.username_chars)
            else -> null
        }
    }

    private fun validateEmail() {
        binding.email.error =
            if (viewModel.updateEmail(email)) null else getString(R.string.email_invalid)
    }

    private fun validatePassword() {
        binding.password.error = when (viewModel.updatePassword(password)) {
            SignUpViewModel.ERROR_PASSWORD_LENGTH -> getString(R.string.password_min_length)
            SignUpViewModel.ERROR_PASSWORD_UPPERCASE -> getString(R.string.password_uppercase)
            SignUpViewModel.ERROR_PASSWORD_LOWERCASE -> getString(R.string.password_lowercase)
            SignUpViewModel.ERROR_PASSWORD_SPECIAL -> getString(R.string.password_special)
            else -> null
        }
    }

    private fun validatePasswordConfirm() {
        binding.passwordConfirm.error = if (viewModel.updatePasswordConfirm(
                password, passwordConfirm
            )
        ) null else getString(R.string.password_does_not_match)
    }

    private fun validateTerms() {
        binding.terms.isErrorShown = !viewModel.updateTerms(terms)
        binding.terms.errorAccessibilityLabel = getString(R.string.terms_error)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}