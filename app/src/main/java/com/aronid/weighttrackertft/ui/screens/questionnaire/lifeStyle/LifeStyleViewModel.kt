package com.aronid.weighttrackertft.ui.screens.questionnaire.lifeStyle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.questionnaire.ButtonConfigs
import com.aronid.weighttrackertft.data.questionnaire.LifeStyleOption
import com.aronid.weighttrackertft.data.questionnaire.LifeStyleState
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
class LifeStyleViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(LifeStyleState())
    val state: StateFlow<LifeStyleState> = _state

    init {
        checkFormValidity()
    }

    fun getActivityLevelOptions(): List<LifeStyleOption> {
        return listOf(
            LifeStyleOption(
                id = "sedentary",
                titleRes = R.string.activity_level_sedentary,
                descriptionRes = R.string.activity_level_sedentary_desc
            ),
            LifeStyleOption(
                id = "moderate",
                titleRes = R.string.activity_level_moderate,
                descriptionRes = R.string.activity_level_moderate_desc
            ),
            LifeStyleOption(
                id = "active",
                titleRes = R.string.activity_level_active,
                descriptionRes = R.string.activity_level_active_desc
            )
        )
    }

    fun onActivityLevelSelected(optionId: String) {
        _state.update { it.copy(activityLevel = optionId) }
        checkFormValidity()
    }


    private fun checkFormValidity() {
        _state.update { currentState ->
            val isValid = currentState.activityLevel.isNotBlank()
            val (text, layout, state) = getDefaultButtonConfig(isValid)
            val border = getDefaultBorderConfig()

            currentState.copy(
                isFormValid = isValid,
                buttonConfigs = ButtonConfigs(text, layout, state, border)
            )
        }
    }

    fun uploadData(navHostController: NavHostController) {

        val user = userRepository.getCurrentUser()

        user?.let { currentUser ->
            viewModelScope.launch {
                try {
                    val updates = mapOf<String, Any>(
                        "activityLevel" to (_state.value.activityLevel ?: "")
                    )
                    userRepository.updateUserFields(currentUser.uid, updates)
                    navHostController.navigate(NavigationRoutes.Goals.route)
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }
}
