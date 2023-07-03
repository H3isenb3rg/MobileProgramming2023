package it.unibs.mp.horace.ui.auth.signin

import androidx.lifecycle.ViewModel
import it.unibs.mp.horace.ProfileValidator

class SignInViewModel : ViewModel() {
    private var validator = ProfileValidator()

    val isEverythingValid: Boolean
        get() = validator.isEmailValid && validator.isPasswordValid

    fun updateEmail(email: String): Int {
        return validator.updateEmail(email)
    }

    fun updatePassword(password: String): Int {
        return validator.updatePassword(password)
    }
}