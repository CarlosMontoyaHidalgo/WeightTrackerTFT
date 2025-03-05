package com.aronid.weighttrackertft.ui.screens.workout.summary

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.workout.WorkoutModel
import com.aronid.weighttrackertft.data.workout.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutSummaryViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _calories = MutableStateFlow<Int?>(null)
    val calories: StateFlow<Int?> = _calories.asStateFlow()

    private val _volume = MutableStateFlow<Int?>(null)
    val volume: StateFlow<Int?> = _volume.asStateFlow()

    private val _saveState = MutableStateFlow<String?>(null)
    val saveState: StateFlow<String?> = _saveState.asStateFlow()

    fun loadWorkoutById(workoutId: String) {
        Log.d("WorkoutSummaryViewModel", "Loading workout with ID: $workoutId")
        viewModelScope.launch {
            try {
                val workout = workoutRepository.getWorkoutById(workoutId)
                workout?.let {
                    _calories.value = it.calories
                    _volume.value = it.volume
                    _saveState.value = "Entrenamiento guardado"
                    Log.d("WorkoutSummaryViewModel", "Fetched workout - Calories: ${it.calories}, Volume: ${it.volume}")
                } ?: run {
                    Log.w("WorkoutSummaryViewModel", "Workout not found for ID: $workoutId")
                }
            } catch (e: Exception) {
                Log.e("WorkoutSummaryViewModel", "Error fetching workout", e)
                _saveState.value = "Error: ${e.message}"
            }
        }
    }
}