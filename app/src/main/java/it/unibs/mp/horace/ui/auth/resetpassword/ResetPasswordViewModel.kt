package it.unibs.mp.horace.ui.auth.resetpassword

import android.util.Patterns
import androidx.lifecycle.ViewModel

class ResetPasswordViewModel : ViewModel() {
    private var _isEmailValid = false

    val isEverythingValid: Boolean
        get() = _isEmailValid

    fun updateEmail(email: String): Boolean {
        _isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        return _isEmailValid
    }
}