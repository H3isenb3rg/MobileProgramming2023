package it.unibs.mp.horace.ui.settings.updateprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.ProfileValidator
import it.unibs.mp.horace.backend.firebase.CurrentUser
import it.unibs.mp.horace.databinding.FragmentUpdateProfileBinding
import it.unibs.mp.horace.ui.auth.signup.SignUpFragmentDirections
import kotlinx.coroutines.launch

class UpdateProfileFragment : Fragment() {
    private val viewModel: UpdateProfileViewModel by viewModels()

    private var _binding: FragmentUpdateProfileBinding? = null
    private val binding get() = _binding!!
    private val username get() = binding.textinputUsername.editText?.text.toString()
    private val email get() = binding.textinputEmail.editText?.text.toString()
    private val password get() = binding.textinputPassword.editText?.text.toString()
    private val passwordConfirm get() = binding.textinputPasswordConfirm.editText?.text.toString()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val user = CurrentUser()

        lifecycleScope.launch {
            binding.textinputUsername.editText?.setText(user.username)
            binding.textinputEmail.editText?.setText(user.email)
        }

        addChangeListeners()

        binding.buttonDeleteProfile.setOnClickListener {
            findNavController().navigate(
                UpdateProfileFragmentDirections.actionUpdateProfileFragmentToDialogConfirmDelete()
            )
        }

        binding.buttonSaveChanges.setOnClickListener {
            validateAll()

            if (!viewModel.isEverythingValid) {
                return@setOnClickListener
            }

            user.username = username
            user.email = email

            lifecycleScope.launch {
                // TODO: Handle errors
                user.update()

                findNavController().navigate(
                    SignUpFragmentDirections.actionSignUpFragmentToHomeFragment()
                )
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
            validatePasswordConfirm()
        }
        binding.textinputPasswordConfirm.editText?.addTextChangedListener {
            validatePasswordConfirm()
        }
    }

    private fun validateAll() {
        validateUsername()
        validateEmail()
        validatePassword()
        validatePasswordConfirm()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}