package it.unibs.mp.horace.ui.signup

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import it.unibs.mp.horace.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private var emailValid: Boolean = false
    private var passwordValid: Boolean = false
    private var passwordConfirmValid: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addEmailValidation()
        addPasswordValidation()
        addPasswordConfirmValidation()

        binding.signUp.setOnClickListener {
            val formValid = emailValid && passwordValid && passwordConfirmValid

            if (formValid) {
                Snackbar.make(view, "Everything ok", Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(view, "You fucked up", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun addEmailValidation() {
        binding.email.editText?.doOnTextChanged { text, _, _, _ ->
            if (!Patterns.EMAIL_ADDRESS.matcher(text.toString()).matches()) {
                binding.email.error = "Email address is invalid"
                emailValid = false
            } else {
                binding.email.error = null
                emailValid = true
            }
        }
    }

    private fun addPasswordValidation() {
        binding.password.editText?.doOnTextChanged { text, _, _, _ ->
            val error = validatePassword(text.toString())
            binding.password.error = error
            passwordValid = (error == null)
        }
    }

    private fun addPasswordConfirmValidation() {
        binding.passwordConfirm.editText?.doOnTextChanged { text, _, _, _ ->
            if (text.toString() != binding.password.editText?.text.toString()) {
                binding.passwordConfirm.error = "Password does not match"
                passwordConfirmValid = false
            } else {
                passwordConfirmValid = true
            }
        }
    }

    private fun validatePassword(password: String): String? {
        if (password.length < 8) {
            return "Password must have at least 8 characters"
        }
        if (!password.matches(".*[A-Z].*".toRegex())) {
            return "Password must contain at least 1 uppercase character"
        }
        if (!password.matches(".*[a-z].*".toRegex())) {
            return "Password must contain at least 1 lowercase character"
        }
        if (!password.matches(".*[@#\$%^&+=].*".toRegex())) {
            return "Password must contain at least 1 special character (@#\$%^&+=)"
        }

        return null
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}