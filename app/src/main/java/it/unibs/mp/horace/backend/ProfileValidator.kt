package it.unibs.mp.horace.backend

import android.util.Patterns

/**
 * Base class for profile validation.
 */
class ProfileValidator {
    // Return codes
    companion object {
        const val OK = 0
        const val ERROR_USERNAME_LENGTH = 1
        const val ERROR_USERNAME_CHARS = 2
        const val ERROR_EMAIL = 3
        const val ERROR_PASSWORD_LENGTH = 4
        const val ERROR_PASSWORD_UPPERCASE = 5
        const val ERROR_PASSWORD_LOWERCASE = 6
        const val ERROR_PASSWORD_SPECIAL = 7
        const val ERROR_PASSWORD_CONFIRM = 8
    }

    // Validation flags
    var isUsernameValid = false
        private set
    var isEmailValid = false
        private set
    var isPasswordValid = false
        private set
    var isPasswordConfirmValid = false
        private set

    fun updateUsername(username: String): Int {
        isUsernameValid = false

        if (username.length < 3) {
            return ERROR_USERNAME_LENGTH
        }

        if (!username.matches("^[a-zA-Z0-9_-]{3,}$".toRegex())) {
            return ERROR_USERNAME_CHARS
        }

        isUsernameValid = true
        return OK
    }

    fun updateEmail(email: String): Int {
        isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()

        if (!isEmailValid) {
            return ERROR_EMAIL
        }

        return OK
    }

    fun updatePassword(password: String): Int {
        isPasswordValid = false

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

        isPasswordValid = true
        return OK
    }

    fun updatePasswordConfirm(password: String, passwordConfirm: String): Int {
        isPasswordConfirmValid = (password == passwordConfirm)

        if (!isPasswordConfirmValid) {
            return ERROR_PASSWORD_CONFIRM
        }

        return OK
    }
}