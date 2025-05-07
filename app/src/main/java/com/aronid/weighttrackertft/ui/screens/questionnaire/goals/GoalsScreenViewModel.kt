package com.aronid.weighttrackertft.ui.screens.questionnaire.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.questionnaire.ButtonConfigs
import com.aronid.weighttrackertft.data.questionnaire.GoalState
import com.aronid.weighttrackertft.data.questionnaire.LifeStyleOption
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
class GoalsScreenViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(GoalState())
    val state: StateFlow<GoalState> = _state

    init {
        checkFormValidity()
    }

    fun getGoalsOptions(): List<LifeStyleOption> {
        return listOf(
            LifeStyleOption(
                id = "lose_weight",
                titleRes = R.string.goal_lose_weight,
                descriptionRes = R.string.goal_lose_weight_desc
            ),
            LifeStyleOption(
                id = "maintain",
                titleRes = R.string.goal_maintain,
                descriptionRes = R.string.goal_maintain_desc
            ),
            LifeStyleOption(
                id = "gain_muscle",
                titleRes = R.string.goal_gain_muscle,
                descriptionRes = R.string.goal_gain_muscle_desc
            )
        )
    }

    fun onGoalSelected(optionId: String) {
        _state.update { it.copy(goal = optionId) }
        checkFormValidity()
    }

    private fun checkFormValidity() {
        _state.update { currentState ->
            val isValid = currentState.goal.isNotBlank()
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
                        "goal" to (_state.value.goal ?: "")
                    )
                    userRepository.updateUserFields(currentUser.uid, updates)
                    navHostController.navigate(NavigationRoutes.PhysicalData.route)
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }

}