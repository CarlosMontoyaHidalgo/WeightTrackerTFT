package com.aronid.weighttrackertft.ui.screens.user.personalInformation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.goals.getGoalOptions
import com.aronid.weighttrackertft.data.questionnaire.ButtonConfigs
import com.aronid.weighttrackertft.data.questionnaire.ButtonState
import com.aronid.weighttrackertft.data.user.UserModel
import com.aronid.weighttrackertft.data.user.UserProgressModel
import com.aronid.weighttrackertft.data.user.UserProgressRepository
import com.aronid.weighttrackertft.data.user.UserRepository
import com.aronid.weighttrackertft.data.weight.WeightRepository
import com.aronid.weighttrackertft.utils.button.getDefaultBorderConfig
import com.aronid.weighttrackertft.utils.button.getDefaultButtonConfig
import com.aronid.weighttrackertft.utils.toTitleCase
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDataViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userProgressRepository: UserProgressRepository,
    private val weightRepository: WeightRepository // Conservamos WeightRepository
) : ViewModel() {
    private val _state = MutableStateFlow(UserModel())
    val state: StateFlow<UserModel> = _state.asStateFlow()

    private val _buttonState = MutableStateFlow(ButtonState())
    val buttonState: StateFlow<ButtonState> = _buttonState.asStateFlow()

    private var originalData = UserModel()

    private val _selectedGoal = MutableStateFlow("")
    val selectedGoal: StateFlow<String> = _selectedGoal.asStateFlow()

    val weightUnit: StateFlow<String> = weightRepository.weightUnit // Exponemos el weightUnit del repositorio

    private val _showNameError = MutableStateFlow(false)
    val showNameError: StateFlow<Boolean> = _showNameError

    private val _showBirthdateError = MutableStateFlow(false)
    val showBirthdateError: StateFlow<Boolean> = _showBirthdateError

    private val _showEmailError = MutableStateFlow(false)
    val showEmailError: StateFlow<Boolean> = _showEmailError

    private val _showHeightError = MutableStateFlow(false)
    val showHeightError: StateFlow<Boolean> = _showHeightError

    private val _showWeightError = MutableStateFlow(false)
    val showWeightError: StateFlow<Boolean> = _showWeightError

    fun onNameChanged(newName: String) {
        val formattedName = newName.toTitleCase()
        _state.update { it.copy(name = formattedName) }
        _showNameError.value = validateName(formattedName) != null
        checkFormValidity()
    }

    fun validateName(name: String): String? {
        return if (name.isBlank()) "El nombre no puede estar vacío" else null
    }

    fun onBirthdateChanged(newBirthdate: String) {
        _state.update { it.copy(birthdate = newBirthdate) }
        _showBirthdateError.value = validateBirthdate(newBirthdate) != null
        checkFormValidity()
    }

    fun validateBirthdate(birthdate: String): String? {
        return if (birthdate.isBlank()) "La fecha de nacimiento no puede estar vacía" else null
    }

    fun onEmailChanged(newEmail: String) {
        _state.update { it.copy(email = newEmail) }
        _showEmailError.value = validateEmail(newEmail) != null
        checkFormValidity()
    }

    fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "El email no puede estar vacío"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "El email no es válido"
            else -> null
        }
    }

    fun onHeightChanged(newHeight: String) {
        _state.update { it.copy(height = newHeight.toIntOrNull()) }
        _showHeightError.value = validateHeight(newHeight) != null
        checkFormValidity()
    }

    fun validateHeight(height: String): String? {
        val heightValue = height.toIntOrNull()
        return when {
            height.isBlank() -> "La altura no puede estar vacía"
            heightValue == null -> "La altura debe ser un número válido"
            heightValue <= 0 -> "La altura debe ser mayor a 0"
            heightValue > 300 -> "La altura debe ser válida"
            else -> null
        }
    }

    fun onWeightChanged(newWeight: String) {
        val weightValue = newWeight.toDoubleOrNull()
        if (weightValue != null) {
            // Convertimos a kg según la unidad seleccionada
            val weightInKg = if (weightUnit.value == "Libras") weightValue * 0.453592 else weightValue
            _state.update { it.copy(weight = weightInKg) }
        } else {
            _state.update { it.copy(weight = null) }
        }
        _showWeightError.value = validateWeight(newWeight) != null
        checkFormValidity()
    }

    fun validateWeight(weight: String): String? {
        val weightValue = weight.toDoubleOrNull()
        return when {
            weight.isBlank() -> "El peso no puede estar vacío"
            weightValue == null -> "El peso debe ser un número válido"
            weightValue <= 0 -> "El peso debe ser mayor a 0"
            else -> null
        }
    }

    suspend fun loadUserData() {
        val user = userRepository.getCurrentUser()
        viewModelScope.launch {
            try {
                val userData = userRepository.getUserData(user.uid)
                userData?.let {
                    _state.value = it
                    originalData = it.copy()
                    _selectedGoal.value = it.goal ?: ""
                    Log.d("UserDataViewModel", "User data loaded: $it")
                    checkFormValidity()
                } ?: Log.w("UserDataViewModel", "No user data found for ${user.uid}")
            } catch (e: Exception) {
                Log.e("UserDataViewModel", "Error loading user data", e)
            }
        }
    }

    private fun checkFormValidity() {
        val current = _state.value
        val original = originalData
        val isValid = (current.name.isNotBlank() && current.name != original.name) ||
                (current.birthdate.isNotBlank() && current.birthdate != original.birthdate) ||
                (current.email.isNotBlank() && current.email != original.email) ||
                (current.gender != original.gender) ||
                (current.goal != original.goal) ||
                (current.height != original.height) ||
                (current.weight != original.weight)

        updateButtonConfigs(isValid)
    }

    private fun updateButtonConfigs(isValid: Boolean) {
        _buttonState.update {
            val (text, layout, state) = getDefaultButtonConfig(isValid)
            val border = getDefaultBorderConfig()
            it.copy(
                baseState = it.baseState.copy(
                    isFormValid = isValid,
                    buttonConfigs = ButtonConfigs(text, layout, state, border)
                )
            )
        }
    }

    fun onGenderChanged(newGender: String) {
        _state.update { it.copy(gender = newGender) }
        checkFormValidity()
    }

    fun onGoalChanged(newGoal: String) {
        _state.update { it.copy(goal = newGoal) }
        checkFormValidity()
    }

    fun getGoalDisplay(goalId: String?): String {
        return getGoalOptions().find { it.id == goalId }?.title ?: ""
    }

    fun saveUserData() {
        val user = userRepository.getCurrentUser()
        viewModelScope.launch {
            try {
                val currentState = _state.value
                val updates = mutableMapOf<String, Any>()

                if (currentState.name != originalData.name) updates["name"] = currentState.name
                if (currentState.birthdate != originalData.birthdate) updates["birthdate"] = currentState.birthdate
                if (currentState.email != originalData.email) updates["email"] = currentState.email
                if (currentState.gender != originalData.gender) updates["gender"] = currentState.gender ?: ""
                if (currentState.goal != originalData.goal) updates["goal"] = currentState.goal ?: ""
                if (currentState.height != originalData.height) updates["height"] = currentState.height ?: 0
                if (currentState.weight != originalData.weight) updates["weight"] = currentState.weight ?: 0.0

                if (updates.isNotEmpty()) {
                    userRepository.updateUserFields(user.uid, updates)
                    Log.d("UserDataViewModel", "User data saved successfully: $updates")

                    // Guardar el peso en user_progress si cambió
                    if (currentState.weight != originalData.weight && currentState.weight != null) {
                        val progress = UserProgressModel(
                            userId = "", // El repositorio lo llenará
                            weight = currentState.weight, // Siempre en kg
                            timestamp = Timestamp.now(),
                            caloriesConsumed = null,
                            activityLevel = null,
                            note = "Peso actualizado desde perfil"
                        )
                        userProgressRepository.saveProgress(progress).fold(
                            onSuccess = { Log.d("UserDataViewModel", "Weight saved to user_progress") },
                            onFailure = { e -> Log.e("UserDataViewModel", "Error saving weight to user_progress", e) }
                        )
                    }

                    originalData = currentState.copy()
                }
                checkFormValidity()
            } catch (e: Exception) {
                Log.e("UserDataViewModel", "Error saving user data", e)
            }
        }
    }
}