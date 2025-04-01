package com.aronid.weighttrackertft.ui.screens.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.questionnaire.ButtonConfigs
import com.aronid.weighttrackertft.data.questionnaire.ButtonState
import com.aronid.weighttrackertft.data.user.UserRepository
import com.aronid.weighttrackertft.utils.button.getDefaultBorderConfig
import com.aronid.weighttrackertft.utils.button.getDefaultButtonConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _buttonState = MutableStateFlow(ButtonState())
    val buttonState: StateFlow<ButtonState> = _buttonState.asStateFlow()

    private val _userName = MutableStateFlow<String>("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            val user = userRepository.getUserName()
            user.fold(
                onSuccess = { name ->
                    Log.d("HomeViewModel", "Nombre obtenido: $name")
                    _userName.value = name
                },
                onFailure = { exception ->
                    Log.e("HomeViewModel", "Error al obtener nombre: $exception")
                    _userName.value = "Usuario" // Valor por defecto más amigable
                }
            )
        }
    }

    init {
        updateButtonConfigs()
    }

    fun logout(){
        viewModelScope.launch{
            try {
                userRepository.logout()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateButtonConfigs() {
        _buttonState.update {
            val (text, layout, state) = getDefaultButtonConfig(true)
            val border = getDefaultBorderConfig()
            it.copy(
                baseState = it.baseState.copy(
                    isFormValid = true,
                    buttonConfigs = ButtonConfigs(text, layout, state, border)
                )
            )
        }
    }

}