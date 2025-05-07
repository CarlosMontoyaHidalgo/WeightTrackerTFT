package com.aronid.weighttrackertft.ui.screens.auth.state

data class SignUpState(
    val email: String = "",
    val password: String = "",
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val isFormValid: Boolean = false,
    val isLoading: Boolean = false,
    val error: Int? = null
)
