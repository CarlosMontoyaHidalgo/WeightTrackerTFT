package com.aronid.weighttrackertft.ui.screens.questionnaire.physicalData

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.data.questionnaire.PhysicalDataState
import com.aronid.weighttrackertft.data.user.UserRepository
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhysicalDataViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {


    private val _state = MutableStateFlow(PhysicalDataState())
    val state: StateFlow<PhysicalDataState> = _state

    fun onHeightChanged(newHeight: String) {
        val height = newHeight.toIntOrNull() ?: 0
        _state.update { it.copy(height = height) }
    }

    fun onWeightChanged(newWeight: String) {
        val weight = newWeight.toDoubleOrNull() ?: 0.0
        _state.update { it.copy(weight = weight) }
    }

    fun uploadData(navHostController: NavHostController) {
        val user = userRepository.getCurrentUser()
        user?.let { currentUser ->
            viewModelScope.launch {
                try {
                    val updates = mapOf<String, Any>(
                        "height" to (_state.value.height ?: 0),
                        "weight" to (_state.value.weight ?: 0.0)
                    )
                    userRepository.updateUserFields(currentUser.uid, updates)
                    navHostController.navigate(NavigationRoutes.Home.route)
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }
}