package it.unibs.mp.horace.ui.signup

import android.util.Patterns
import androidx.lifecycle.ViewModel

class SignUpViewModel : ViewModel() {
    companion object {
        const val ERROR_USERNAME_LENGTH = 1
        const val ERROR_USERNAME_CHARS = 2

        const val ERROR_PASSWORD_LENGTH = 3
        const val ERROR_PASSWORD_UPPERCASE = 4
        const val ERROR_PASSWORD_LOWERCASE = 5
        const val ERROR_PASSWORD_SPECIAL = 6
    }

    private var _isUsernameValid = false
    private var _isEmailValid = false
    private var _isPasswordValid = false
    private var _isPasswordConfirmValid = false
    private var _isTermsValid = false

    val isEverythingValid: Boolean
        get() = _isUsernameValid && _isEmailValid && _isPasswordValid && _isPasswordConfirmValid && _isTermsValid

    fun updateUsername(username: String): Int? {
        _isUsernameValid = false

        if (username.length < 3) {
            return ERROR_USERNAME_LENGTH
        }

        if (!username.matches("^[a-zA-Z0-9_-]{3,}$".toRegex())) {
            return ERROR_USERNAME_CHARS
        }

        _isUsernameValid = true
        return null
    }

    fun updateEmail(email: String): Boolean {
        _isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        return _isEmailValid
    }

    fun updatePassword(password: String): Int? {
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

    fun updatePasswordConfirm(password: String, passwordConfirm: String): Boolean {
        _isPasswordConfirmValid = (password == passwordConfirm)
        return _isPasswordConfirmValid
    }

    fun updateTerms(isChecked: Boolean): Boolean {
        _isTermsValid = isChecked
        return _isTermsValid
    }
}