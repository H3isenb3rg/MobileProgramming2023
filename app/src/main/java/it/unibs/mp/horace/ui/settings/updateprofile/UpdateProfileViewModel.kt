package it.unibs.mp.horace.ui.settings.updateprofile

import androidx.lifecycle.ViewModel
import it.unibs.mp.horace.backend.ProfileValidator

class UpdateProfileViewModel : ViewModel() {
    private var validator = ProfileValidator()

    val isEverythingValid: Boolean
        get() = validator.isUsernameValid && validator.isEmailValid && validator.isPasswordValid && validator.isPasswordConfirmValid

    fun updateUsername(username: String): Int {
        return validator.updateUsername(username)
    }

    fun updateEmail(email: String): Int {
        return validator.updateEmail(email)
    }

    fun updatePassword(password: String): Int {
        // Password can be empty
        if (password.isEmpty()) {
            return ProfileValidator.OK
        }
        return validator.updatePassword(password)
    }

    fun updatePasswordConfirm(password: String, passwordConfirm: String): Int {
        if (passwordConfirm.isEmpty() && password.isEmpty()) {
            return ProfileValidator.OK
        }
        return validator.updatePasswordConfirm(password, passwordConfirm)
    }
}