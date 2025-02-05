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

    // Avanza a la siguiente pantalla
    fun goToNextScreen() {
        when (_state.value.currentScreen) {
            1 -> _state.update { it.copy(currentScreen = 2) }
            2 -> _state.update { it.copy(currentScreen = 3) }
            3 -> submitQuestionnaire()  // Cuando se completa el cuestionario
        }
    }

    // Retrocede a la pantalla anterior
    fun goToPreviousScreen() {
        when (_state.value.currentScreen) {
            2 -> _state.update { it.copy(currentScreen = 1) }
            3 -> _state.update { it.copy(currentScreen = 2) }
        }
    }

    // Actualiza la información personal del usuario
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

    // Actualiza los datos físicos del usuario
    fun updatePhysicalData(height: Int, weight: Double) {
        _state.update {
            it.copy(physicalData = it.physicalData.copy(height = height, weight = weight))
        }
    }

    // Actualiza el nivel de actividad y los objetivos del usuario
    fun updateLifestyle(activityLevel: String, goal: String) {
        _state.update {
            it.copy(lifestyle = it.lifestyle.copy(activityLevel = activityLevel, goal = goal))
        }
    }

    // Guarda la información del cuestionario al finalizar
    fun submitQuestionnaire() {
        // Obtener el usuario actual desde el repositorio
        val user = userRepository.getCurrentUser()

        // Crear el modelo de usuario con los datos del cuestionario
        val userModel = user?.let {
            UserModel(
                id = it.uid,
                name = _state.value.personalInfo.name,
                email = user.email ?: "",  // Asegúrate de manejar el caso nulo correctamente
                birthdate = _state.value.personalInfo.birthdate,
                gender = _state.value.personalInfo.gender,
                height = _state.value.physicalData.height,
                weight = _state.value.physicalData.weight,
                activityLevel = _state.value.lifestyle.activityLevel,
                goal = _state.value.lifestyle.goal,
                hasCompletedQuestionnaire = true  // Indicamos que el cuestionario ha sido completado
            )
        }

        // Guardar los datos del cuestionario de forma asincrónica
        viewModelScope.launch {
            try {
                // Guardar o actualizar el usuario en la base de datos
                userModel?.let { userRepository.saveUser(it) }

                // Aquí puedes hacer algo después de guardar, como notificar al usuario o navegar a otra pantalla
                // Por ejemplo:
                // _state.update { it.copy(isQuestionnaireSubmitted = true) }
            } catch (e: Exception) {
                // Manejo de errores (por ejemplo, si ocurre un error al guardar)
                // Puedes usar un log, un mensaje de error en la UI, etc.
                Log.e("UserQuestionnaire", "Error al guardar los datos del cuestionario", e)
            }
        }
    }

}

data class UserQuestionnaireState(
    val currentScreen: Int = 1,  // La pantalla actual (1, 2, 3, ...)
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
