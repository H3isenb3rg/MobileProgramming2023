package it.unibs.mp.horace.ui.auth.signin

import android.util.Patterns
import androidx.lifecycle.ViewModel

class SignInViewModel : ViewModel() {
    private var _isEmailValid = false
    private var _isPasswordValid = false

    val isEverythingValid: Boolean
        get() = _isEmailValid && _isPasswordValid

    fun updateEmail(email: String): Boolean {
        _isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        return _isEmailValid
    }

    fun updatePassword(password: String): Boolean {
        _isPasswordValid = password.length >= 8
        return _isPasswordValid
    }
}