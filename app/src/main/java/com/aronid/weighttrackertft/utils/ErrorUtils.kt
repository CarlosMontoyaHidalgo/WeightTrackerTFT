package com.aronid.weighttrackertft.utils

import com.aronid.weighttrackertft.R
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

fun handleAuthException(exception: Throwable): Int {
    return when (exception) {
        is FirebaseAuthInvalidUserException -> R.string.error_user_not_found
        is FirebaseAuthInvalidCredentialsException -> R.string.error_invalid_credentials
        else -> R.string.error_generic
    }
}