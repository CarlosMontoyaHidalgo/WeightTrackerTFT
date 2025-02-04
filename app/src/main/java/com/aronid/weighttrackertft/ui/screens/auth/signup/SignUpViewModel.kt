package com.aronid.weighttrackertft.ui.screens.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.UserRepository
import com.aronid.weighttrackertft.ui.screens.auth.state.SignUpState
import com.aronid.weighttrackertft.utils.handleAuthException
import com.aronid.weighttrackertft.utils.validateEmail
import com.aronid.weighttrackertft.utils.validatePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state: StateFlow<SignUpState> = _state

    fun onEmailChanged(newEmail: String) {
        _state.update {
            it.copy(
                email = newEmail,
                emailError = validateEmail(newEmail)
            )
        }
        updateSignUpEnabled()
    }

    fun onPasswordChanged(newPassword: String) {
        _state.update {
            it.copy(
                password = newPassword,
                passwordError = validatePassword(newPassword)
            )
        }
        updateSignUpEnabled()
    }

    fun signUpUser(onSuccess: () -> Unit) {
        if (!_state.value.isFormValid) return

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = userRepository.createUser(
                _state.value.email,
                _state.value.password
            )

            result.onSuccess {
                onSuccess()
            }.onFailure { exception ->
                val errorMessage = handleAuthException(exception)
                _state.update { it.copy(error = errorMessage) }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun updateSignUpEnabled() {
        val isValid = _state.value.emailError == null &&
                _state.value.passwordError == null &&
                _state.value.email.isNotBlank() &&
                _state.value.password.isNotBlank()

        _state.update { it.copy(isFormValid = isValid) }
    }
}

