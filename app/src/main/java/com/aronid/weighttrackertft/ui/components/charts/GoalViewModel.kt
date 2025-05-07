package com.aronid.weighttrackertft.ui.components.charts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.goals.GoalRepository
import com.aronid.weighttrackertft.data.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // Estado del objetivo de calorías como Int
    private val _goalCalories = MutableStateFlow<Int?>(null)
    val goalCalories: StateFlow<Int?> = _goalCalories

    // Estado para controlar el diálogo de objetivos
    private val _showGoalDialog = MutableStateFlow(false)
    val showGoalDialog: StateFlow<Boolean> = _showGoalDialog

    private val _showWeightDialog = MutableStateFlow(false)
    val showWeightDialog: StateFlow<Boolean> = _showWeightDialog


    init {
        loadGoals()
    }

    private fun loadGoals() {
        viewModelScope.launch {
            val userId = userRepository.getCurrentUserId()
            if (userId == null) {
                println("No hay usuario autenticado")
                return@launch
            }
            val result = goalRepository.getGoalCalories(userId)
            result.onSuccess { goalCalories ->
                _goalCalories.value = goalCalories?.toInt()
                if (goalCalories == null){
                    _showGoalDialog.value = true
                }

            }.onFailure { e ->
                println("Error al cargar objetivo de calorías: $e")
            }
        }
    }

    fun setGoalCalories(goal: Int) {
        viewModelScope.launch {
            val userId = userRepository.getCurrentUserId()
            if (userId == null) {
                println("No hay usuario autenticado")
                return@launch
            }
            goalRepository.updateGoalCalories(userId, goal.toDouble()) // Convertimos a Double para Firebase
                .onSuccess {
                    _goalCalories.value = goal
                    _showGoalDialog.value = false
                    println("Objetivo de calorías actualizado")
                }
                .onFailure { e ->
                    println("Error al actualizar objetivo de calorías: $e")
                }
        }
    }



    fun dismissGoalDialog() {
        _showGoalDialog.value = false
    }

    fun triggerGoalDialog() {
        _showGoalDialog.value = true
    }

    fun checkGoalDialog(caloriesData: Map<String, Int>) {
        if (caloriesData.isNotEmpty() && _goalCalories.value == null) {
            _showGoalDialog.value = true
        }
    }

    fun calculateBMI(weight: Double, height: Double): Double {
        val heightInMeters = height / 100
        return weight / (heightInMeters * heightInMeters)
    }

    fun dismissWeightDialog() {
        _showWeightDialog.value = false
    }

    fun triggerWeightDialog() {
        _showWeightDialog.value = true
    }
}