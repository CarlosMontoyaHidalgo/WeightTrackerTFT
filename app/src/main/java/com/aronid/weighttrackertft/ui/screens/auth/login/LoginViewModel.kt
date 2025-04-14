package com.aronid.weighttrackertft.ui.screens.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.questionnaire.QuestionnaireRepository
import com.aronid.weighttrackertft.data.user.UserRepository
import com.aronid.weighttrackertft.ui.screens.auth.state.LoginState
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
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val questionnaireRepository: QuestionnaireRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    fun onEmailChanged(newEmail: String) {
        _state.update {
            it.copy(
                email = newEmail,
                emailError = validateEmail(newEmail)
            )
        }
        updateLoginEnabled()
    }

    fun onPasswordChanged(newPassword: String) {
        _state.update {
            it.copy(
                password = newPassword,
                passwordError = validatePassword(newPassword)
            )
        }
        updateLoginEnabled()
    }

    fun loginUser(onSuccess: (Boolean) -> Unit) {

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = userRepository.loginUser(
                _state.value.email,
                _state.value.password
            )

            result.onSuccess { user ->
                val userId = user.uid
                val hasCompletedQuestionnaire = questionnaireRepository.getQuestionnaireStatus(userId)
                if (hasCompletedQuestionnaire) {
                    onSuccess(true)
                } else {
                    onSuccess(false)
                }
            }.onFailure { exception ->
                val errorMessage = handleAuthException(exception)
                _state.update { it.copy(error = errorMessage) }
                onSuccess(false)
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun updateLoginEnabled() {
        val isValid = _state.value.emailError == null &&
                _state.value.passwordError == null &&
                _state.value.email.isNotBlank() &&
                _state.value.password.isNotBlank()

        _state.update { it.copy(isFormValid = isValid) }
    }
}
