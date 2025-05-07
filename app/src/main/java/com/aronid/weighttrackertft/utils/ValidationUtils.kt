package com.aronid.weighttrackertft.utils

import android.util.Patterns
import com.aronid.weighttrackertft.R

fun validateEmail(email: String): Int? {
    return when {
        email.isEmpty() -> R.string.error_email_empty
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> R.string.error_email_invalid
        else -> null
    }
}

fun validatePassword(password: String): Int? {
    return when {
        password.isEmpty() -> R.string.error_password_empty
        password.length < 6 -> R.string.error_password_length
        else -> null
    }
}