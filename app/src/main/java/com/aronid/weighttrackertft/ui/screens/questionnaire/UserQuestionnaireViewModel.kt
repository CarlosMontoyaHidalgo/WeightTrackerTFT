package com.aronid.weighttrackertft.ui.screens.questionnaire

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.questionnaire.QuestionnaireRepository
import com.aronid.weighttrackertft.data.user.UserRepository
import com.aronid.weighttrackertft.data.user.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserQuestionnaireViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val questionnaireRepository: QuestionnaireRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UserQuestionnaireState())
    val state: StateFlow<UserQuestionnaireState> = _state

    fun updatePersonalInfo(name: String, birthdate: String, gender: String) {
        _state.update {
            it.copy(
                personalInfo = it.personalInfo.copy(
                    name = name,
                    birthdate = birthdate,
                    gender = gender
                )
            )
        }
    }

    fun updatePhysicalData(height: Int, weight: Double) {
        _state.update {
            it.copy(physicalData = it.physicalData.copy(height = height, weight = weight))
        }
    }

    fun updateLifestyle(activityLevel: String, goal: String) {
        _state.update {
            it.copy(lifestyle = it.lifestyle.copy(activityLevel = activityLevel, goal = goal))
        }
    }

    fun submitQuestionnaire() {
        val user = userRepository.getCurrentUser()

        val userModel = user?.let {
            UserModel(
                id = it.uid,
                name = _state.value.personalInfo.name,
                email = user.email ?: "",
                birthdate = _state.value.personalInfo.birthdate,
                gender = _state.value.personalInfo.gender,
                height = _state.value.physicalData.height,
                weight = _state.value.physicalData.weight,
                activityLevel = _state.value.lifestyle.activityLevel,
                goal = _state.value.lifestyle.goal,
                hasCompletedQuestionnaire = true
            )
        }

        viewModelScope.launch {
            try {
                userModel?.let { userRepository.saveUser(it) }
                // Aquí puedes hacer algo después de guardar, como notificar al usuario o navegar a otra pantalla
                // Por ejemplo:
                // _state.update { it.copy(isQuestionnaireSubmitted = true) }
            } catch (e: Exception) {
                Log.e("UserQuestionnaire", "Error al guardar los datos del cuestionario", e)
            }
        }
    }

}

data class UserQuestionnaireState(
    val currentScreen: Int = 1,
    val personalInfo: PersonalInfo = PersonalInfo(),
    val physicalData: PhysicalData = PhysicalData(),
    val lifestyle: Lifestyle = Lifestyle()
)

data class PersonalInfo(
    val name: String = "",
    val birthdate: String = "",
    val gender: String = ""
)

data class PhysicalData(
    val height: Int? = null,
    val weight: Double? = null
)

data class Lifestyle(
    val activityLevel: String = "",
    val goal: String = ""
)
