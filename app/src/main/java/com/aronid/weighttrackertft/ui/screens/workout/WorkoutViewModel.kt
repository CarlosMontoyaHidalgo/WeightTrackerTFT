package com.aronid.weighttrackertft.ui.screens.workout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.aronid.weighttrackertft.data.exercises.ExerciseRepository
import com.aronid.weighttrackertft.data.routine.RoutineCustomRepository
import com.aronid.weighttrackertft.data.routine.RoutinePredefinedRepository
import com.aronid.weighttrackertft.data.user.UserRepository
import com.aronid.weighttrackertft.data.workout.ExerciseWithSeries
import com.aronid.weighttrackertft.data.workout.SeriesItem
import com.aronid.weighttrackertft.data.workout.WorkoutModel
import com.aronid.weighttrackertft.data.workout.WorkoutRepository
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val routineCustomRepository: RoutineCustomRepository,
    private val routinePredefinedRepository: RoutinePredefinedRepository,
    private val workoutRepository: WorkoutRepository,
    private val userRepository: UserRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _routineId = MutableStateFlow<String?>(null)
    private val _exercisesWithSeries = MutableStateFlow<List<ExerciseWithSeries>>(emptyList())
    val exercisesWithSeries: StateFlow<List<ExerciseWithSeries>> = _exercisesWithSeries.asStateFlow()

    private val _currentExerciseIndex = MutableStateFlow(0)
    val currentExerciseIndex: StateFlow<Int> = _currentExerciseIndex.asStateFlow()

    private val _saveState = MutableStateFlow<String?>(null)
    val saveState: StateFlow<String?> = _saveState.asStateFlow()

    fun loadRoutineExercises(routineId: String, isPredefined: Boolean = false) {
        viewModelScope.launch {
            val loadedRoutine = if (isPredefined) {
                routinePredefinedRepository.getRoutineById(routineId)
            } else {
                routineCustomRepository.getRoutineById(routineId)
            }
            _routineId.value = routineId
            val exerciseRefs = loadedRoutine?.exercises ?: emptyList()
            val exercises = exerciseRefs.mapNotNull { ref ->
                ref.get().await().toObject(ExerciseModel::class.java)
            }
            _exercisesWithSeries.value = exercises.map { exercise ->
                ExerciseWithSeries(
                    exerciseName = exercise.name,
                    description = exercise.description,
                    imageUrl = exercise.imageUrl,
                    series = listOf(SeriesItem(setNumber = 1, weight = "", reps = "", isDone = false)),
                    requiresWeight = exercise.requiresWeight ?: true
                )
            }
            Log.d("WorkoutViewModel", "Loaded exercises: ${_exercisesWithSeries.value}")
        }
    }

    fun getCurrentExercise(): ExerciseWithSeries? = _exercisesWithSeries.value.getOrNull(_currentExerciseIndex.value)

    fun navigateToNextExercise() {
        if (_currentExerciseIndex.value < _exercisesWithSeries.value.size - 1) _currentExerciseIndex.value++
    }

    fun navigateToPreviousExercise() {
        if (_currentExerciseIndex.value > 0) _currentExerciseIndex.value--
    }

    fun updateSeriesWeight(seriesIndex: Int, newValue: String) {
        _exercisesWithSeries.value = _exercisesWithSeries.value.mapIndexed { exIndex, exercise ->
            if (exIndex == _currentExerciseIndex.value) {
                val updatedSeries = exercise.series.toMutableList()
                updatedSeries[seriesIndex] = updatedSeries[seriesIndex].copy(weight = newValue)
                exercise.copy(series = updatedSeries)
            } else exercise
        }
    }

    fun updateSeriesReps(seriesIndex: Int, newValue: String) {
        _exercisesWithSeries.value = _exercisesWithSeries.value.mapIndexed { exIndex, exercise ->
            if (exIndex == _currentExerciseIndex.value) {
                val updatedSeries = exercise.series.toMutableList()
                updatedSeries[seriesIndex] = updatedSeries[seriesIndex].copy(reps = newValue)
                exercise.copy(series = updatedSeries)
            } else exercise
        }
    }

    fun toggleSeriesCompletion(seriesIndex: Int) {
        val currentExercise = getCurrentExercise() ?: return
        val currentSeries = currentExercise.series.getOrNull(seriesIndex) ?: return
        if (currentSeries.weight.isNullOrEmpty() || currentSeries.reps.isNullOrEmpty()) return

        _exercisesWithSeries.value = _exercisesWithSeries.value.mapIndexed { exIndex, exercise ->
            if (exIndex == _currentExerciseIndex.value) {
                val updatedSeries = exercise.series.toMutableList()
                val toggledSeries = updatedSeries[seriesIndex].copy(isDone = !updatedSeries[seriesIndex].isDone)
                updatedSeries[seriesIndex] = toggledSeries
                if (seriesIndex == updatedSeries.lastIndex && toggledSeries.isDone) {
                    updatedSeries.add(SeriesItem(setNumber = updatedSeries.size + 1, weight = "", reps = "", isDone = false))
                }
                exercise.copy(series = updatedSeries)
            } else exercise
        }
    }

    suspend fun saveWorkoutData(): WorkoutModel {
        _saveState.value = "Guardando..."
        try {
            val workout = calculateAndSetWorkoutData()
            // Guardar el workout y obtener el WorkoutModel con el ID generado
            val savedWorkout = workoutRepository.saveWorkout(workout)
            _saveState.value = "Entrenamiento guardado"
            Log.d("WorkoutViewModel", "Workout saved with ID: ${savedWorkout.id}, Calories: ${savedWorkout.calories}, Volume: ${savedWorkout.volume}")
            return savedWorkout // Devolver el WorkoutModel con el ID
        } catch (e: Exception) {
            _saveState.value = "Error: ${e.message}"
            Log.e("WorkoutViewModel", "Error saving workout: ${e.message}", e)
            throw e
        }
    }

    private suspend fun calculateAndSetWorkoutData(): WorkoutModel {
        var totalCalories = 0.0
        var totalVolume = 0.0
        val userWeight = userRepository.getCurrentUserWeight() ?: 70.0

        _exercisesWithSeries.value.forEach { exercise ->
            exercise.series.forEach { serie ->
                if (exercise.requiresWeight) {
                    val weight = serie.weight?.toDoubleOrNull() ?: 0.0
                    val reps = serie.reps?.toIntOrNull() ?: 0
                    totalCalories += (weight * reps) * 0.025
                    totalVolume += weight * reps
                } else {
                    val reps = serie.reps?.toIntOrNull() ?: 0
                    totalCalories += reps * 0.4
                    totalVolume += (userWeight * 0.6) * reps
                }
            }
        }

        return WorkoutModel(
            id = "",
            userId = userRepository.getCurrentUser().uid,
            date = Timestamp.now(),
            exercises = _exercisesWithSeries.value,
            calories = totalCalories.toInt(),
            volume = totalVolume.toInt(),
            intensity = 100.0,
            workoutType = "Mixto"
        )
    }
}