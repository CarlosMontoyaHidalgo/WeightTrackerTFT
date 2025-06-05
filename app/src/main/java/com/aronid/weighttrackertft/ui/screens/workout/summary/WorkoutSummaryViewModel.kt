package com.aronid.weighttrackertft.ui.screens.workout.summary

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.questionnaire.ButtonConfigs
import com.aronid.weighttrackertft.data.questionnaire.ButtonState
import com.aronid.weighttrackertft.data.workout.ExerciseWithSeries
import com.aronid.weighttrackertft.data.workout.WorkoutRepository
import com.aronid.weighttrackertft.utils.button.getDefaultBorderConfig
import com.aronid.weighttrackertft.utils.button.getDefaultButtonConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _durationMinutes = MutableStateFlow<Long?>(null)
    val durationMinutes: StateFlow<Long?> = _durationMinutes.asStateFlow()


    private val _saveState = MutableStateFlow<String?>(null)
    val saveState: StateFlow<String?> = _saveState.asStateFlow()

    private val _allMuscles = MutableStateFlow<List<Pair<String, Float>>>(emptyList())
    val allMuscles: StateFlow<List<Pair<String, Float>>> = _allMuscles.asStateFlow()

    private val _buttonState = MutableStateFlow(ButtonState())
    val buttonState: StateFlow<ButtonState> = _buttonState.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _exercises = MutableStateFlow<List<ExerciseWithSeries>>(emptyList())
    val exercises: StateFlow<List<ExerciseWithSeries>> = _exercises.asStateFlow()

    private fun updateButtonConfigs() {
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

    fun formatDuration(minutes: Int): String {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        return when {
            hours > 0 && remainingMinutes > 0 -> "${hours}h ${remainingMinutes}min"
            hours > 0 -> "${hours}h"
            else -> "${remainingMinutes}min"
        }
    }

    fun loadWorkoutById(workoutId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val workout = workoutRepository.getWorkoutById(workoutId)
                Log.d("WorkoutSummaryViewModel", "Workout loaded: $workout")
                workout?.let {
                    _calories.value = it.calories
                    _volume.value = it.volume
                    _durationMinutes.value = it.duration // <- Aquí está la duración real

                    val validExercises = workout.exercises.filter { exercise ->
                        exercise.series.any { seriesItem ->
                            !seriesItem.reps.isNullOrEmpty() && !seriesItem.weight.isNullOrEmpty()
                        }
                    }

                    val primaryMuscles = validExercises.mapNotNull { exercise ->
                        exercise.primaryMuscleRef?.id
                    }.distinct()

                    val secondaryMuscles = validExercises.flatMap { exercise ->
                        exercise.secondaryMusclesRef.mapNotNull { it?.id }
                    }.distinct()

                    val allMusclesMap = mutableMapOf<String, Float>()
                    primaryMuscles.forEach { muscle ->
                        allMusclesMap[muscle] = 100f
                    }
                    secondaryMuscles.forEach { muscle ->
                        allMusclesMap.putIfAbsent(muscle, 50f)
                    }

                    _allMuscles.value = allMusclesMap.toList()
                    _exercises.value = workout.exercises
                    _saveState.value = "Entrenamiento guardado"
                    updateButtonConfigs()
                } ?: run {
                    Log.w("WorkoutSummaryViewModel", "Workout not found for ID: $workoutId")
                }
            } catch (e: Exception) {
                Log.e("WorkoutSummaryViewModel", "Error fetching workout", e)
                _saveState.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
