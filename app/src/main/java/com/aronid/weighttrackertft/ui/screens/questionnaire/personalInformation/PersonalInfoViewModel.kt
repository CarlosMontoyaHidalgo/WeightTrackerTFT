package com.aronid.weighttrackertft.ui.screens.questionnaire.personalInformation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.data.questionnaire.ButtonConfigs
import com.aronid.weighttrackertft.data.questionnaire.PersonalInfoState
import com.aronid.weighttrackertft.data.user.UserRepository
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.utils.button.getDefaultBorderConfig
import com.aronid.weighttrackertft.utils.button.getDefaultButtonConfig
import com.aronid.weighttrackertft.utils.formatDate
import com.aronid.weighttrackertft.utils.isValidDateFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonalInfoViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(PersonalInfoState())
    val state: StateFlow<PersonalInfoState> = _state

    init {
        checkFormValidity()
    }

    fun onNameChanged(newName: String) {
        _state.value = _state.value.copy(name = newName, nameTouched = true)
        checkFormValidity()
    }

    fun onBirthdateChanged(newBirthdate: String) {
        _state.value = _state.value.copy(birthdate = newBirthdate, birthdateTouched = true)
        checkFormValidity()
    }

    fun onGenderChanged(newGender: String) {
        _state.value = _state.value.copy(gender = newGender)
    }

    private fun checkFormValidity() {
        _state.update { currentState ->
            val correctDate = formatDate(currentState.birthdate)
            val isNameValid = currentState.name.isNotBlank()
            val isBirthdateValid =
                correctDate.isNotBlank() && isValidDateFormat(correctDate)
            val isFormValid = isNameValid && isBirthdateValid

            val (text, layout, state) = getDefaultButtonConfig(isFormValid)
            val border = getDefaultBorderConfig()

            currentState.copy(
                isNameValid = isNameValid,
                isBirthdateValid = isBirthdateValid,
                isFormValid = isFormValid,
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
                        "name" to (_state.value.name ?: ""),
                        "birthdate" to (_state.value.birthdate ?: ""),
                        "gender" to (_state.value.gender ?: "")
                    )
                    userRepository.updateUserFields(currentUser.uid, updates)
                    navHostController.navigate(NavigationRoutes.LifeStyle.route)

                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }
}
