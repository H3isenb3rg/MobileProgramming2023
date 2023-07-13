package it.unibs.mp.horace.ui.auth.signup

import androidx.lifecycle.ViewModel
import it.unibs.mp.horace.backend.ProfileValidator

class SignUpViewModel : ViewModel() {
    private var validator = ProfileValidator()
    private var isTermsValid = false

    val isEverythingValid: Boolean
        get() = validator.isUsernameValid && validator.isEmailValid && validator.isPasswordValid && validator.isPasswordConfirmValid && isTermsValid

    fun updateUsername(username: String): Int {
        return validator.updateUsername(username)
    }

    fun updateEmail(email: String): Int {
        return validator.updateEmail(email)
    }

    fun updatePassword(password: String): Int {
        return validator.updatePassword(password)
    }

    fun updatePasswordConfirm(password: String, passwordConfirm: String): Int {
        return validator.updatePasswordConfirm(password, passwordConfirm)
    }

    fun updateTerms(isChecked: Boolean): Boolean {
        isTermsValid = isChecked
        return isTermsValid
    }
}