package com.aronid.weighttrackertft.ui.screens.questionnaire

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.data.questionnaire.ButtonConfigs
import com.aronid.weighttrackertft.data.questionnaire.UserQuestionnaireState
import com.aronid.weighttrackertft.data.user.UserRepository
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.utils.button.getDefaultBorderConfig
import com.aronid.weighttrackertft.utils.button.getDefaultButtonConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserQuestionnaireViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(UserQuestionnaireState())
    val state: StateFlow<UserQuestionnaireState> = _state

    init {
        checkFormValidity(false)
    }

    fun onCheckboxChanged(isChecked: Boolean) {
        checkFormValidity(isChecked)
    }

    private fun checkFormValidity(isChecked: Boolean) {
        _state.update {
            val (text, layout, state) = getDefaultButtonConfig(isChecked)
            val border = getDefaultBorderConfig()

            it.copy(
                baseState = it.baseState.copy(
                    isFormValid = isChecked,
                    buttonConfigs = ButtonConfigs(text, layout, state, border)
                )
            )
        }
    }

    fun uploadData(navHostController: NavHostController) {
        val user = userRepository.getCurrentUser()

        user?.let { currentUser ->
            viewModelScope.launch {
                try {
                    val updates = mapOf<String, Any>(
                        "termsAccepted" to true
                    )
                    userRepository.updateUserFields(currentUser.uid, updates)
                    navHostController.navigate(NavigationRoutes.PersonalInformation.route)
                } catch (e: Exception) {
                    Log.e("UserQuestionnaireViewModel", "Error al subir los datos: ${e.message}")
                }
            }
        }
    }
}
