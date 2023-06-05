package it.unibs.mp.horace.ui.signup

import android.util.Patterns
import androidx.lifecycle.ViewModel

class SignUpViewModel : ViewModel() {
    companion object {
        const val ERROR_PASSWORD_LENGTH = 0
        const val ERROR_PASSWORD_UPPERCASE = 1
        const val ERROR_PASSWORD_LOWERCASE = 2
        const val ERROR_PASSWORD_SPECIAL = 3
    }

    private var _isEmailValid = false
    private var _isPasswordValid = false
    private var _isPasswordConfirmValid = false

    val isEverythingValid: Boolean
        get() = _isEmailValid && _isPasswordValid && _isPasswordConfirmValid

    fun validateEmail(email: String): Boolean {
        _isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        return _isEmailValid
    }

    fun validatePassword(password: String): Int? {
        _isPasswordValid = false

        if (password.length < 8) {
            return ERROR_PASSWORD_LENGTH
        }
        if (!password.matches(".*[A-Z].*".toRegex())) {
            return ERROR_PASSWORD_UPPERCASE
        }
        if (!password.matches(".*[a-z].*".toRegex())) {
            return ERROR_PASSWORD_LOWERCASE
        }
        if (!password.matches(".*[@#\$%^&+=].*".toRegex())) {
            return ERROR_PASSWORD_SPECIAL
        }

        _isPasswordValid = true
        return null
    }

    fun validatePasswordConfirm(password: String, passwordConfirm: String): Boolean {
        _isPasswordConfirmValid = password != passwordConfirm
        return _isPasswordConfirmValid
    }
}