package com.aronid.weighttrackertft.ui.screens.workout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.aronid.weighttrackertft.data.exercises.ExerciseRepository
import com.aronid.weighttrackertft.data.muscles.MuscleRepository
import com.aronid.weighttrackertft.data.routine.RoutineCustomRepository
import com.aronid.weighttrackertft.data.routine.RoutinePredefinedRepository
import com.aronid.weighttrackertft.data.user.UserRepository
import com.aronid.weighttrackertft.data.workout.ExerciseWithSeries
import com.aronid.weighttrackertft.data.workout.SeriesItem
import com.aronid.weighttrackertft.data.workout.WorkoutModel
import com.aronid.weighttrackertft.data.workout.WorkoutRepository
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
    private val exerciseRepository: ExerciseRepository,
    private val muscleRepository: MuscleRepository
) : ViewModel() {

    private val _routineId = MutableStateFlow<String?>(null)
    private val _exercisesWithSeries = MutableStateFlow<List<ExerciseWithSeries>>(emptyList())
    val exercisesWithSeries: StateFlow<List<ExerciseWithSeries>> =
        _exercisesWithSeries.asStateFlow()

    private val _currentExerciseIndex = MutableStateFlow(0)
    val currentExerciseIndex: StateFlow<Int> = _currentExerciseIndex.asStateFlow()

    private val _saveState = MutableStateFlow<String?>(null)
    val saveState: StateFlow<String?> = _saveState.asStateFlow()

    private val _primaryMuscles = MutableStateFlow<List<String>>(emptyList())
    val primaryMuscles: StateFlow<List<String>> = _primaryMuscles.asStateFlow()

    private val _secondaryMuscles = MutableStateFlow<List<String>>(emptyList())
    val secondaryMuscles: StateFlow<List<String>> = _secondaryMuscles.asStateFlow()

    private val _workoutDuration = MutableStateFlow(0L)
    val workoutDuration: StateFlow<Long> = _workoutDuration.asStateFlow()
    private var isTimerRunning = false
    private var startTime: Long = 0L


    init {
        startTimer()
    }

    private fun startTimer() {
        if (!isTimerRunning) {
            isTimerRunning = true
            startTime = System.currentTimeMillis()
            viewModelScope.launch {
                while (isTimerRunning) {
                    val elapsedTime = System.currentTimeMillis() - startTime
                    _workoutDuration.value = elapsedTime
                    delay(1000)
                }
            }
        }
    }

    fun stopTimer() {
        isTimerRunning = false
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }


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
                try {
                    ref.get().await().toObject(ExerciseModel::class.java)

                } catch (e: Exception) {
                    Log.e("WorkoutViewModel", "Error loading exercise: ${e.message}", e)
                    null
                }
            }
            _exercisesWithSeries.value = exercises.map { exercise ->
                ExerciseWithSeries(
                    exerciseName = exercise.name,
                    description = exercise.description,
                    imageUrl = exercise.imageUrl,
                    series = listOf(
                        SeriesItem(
                            setNumber = 1,
                            weight = "",
                            reps = "",
                            isDone = false
                        )
                    ),
                    requiresWeight = exercise.requiresWeight,
                    primaryMuscleRef = exercise.primaryMuscle,
                    secondaryMusclesRef = exercise.secondaryMuscle

                )
            }
        }
    }

    fun getCurrentExercise(): ExerciseWithSeries? =
        _exercisesWithSeries.value.getOrNull(_currentExerciseIndex.value)

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
                val toggledSeries =
                    updatedSeries[seriesIndex].copy(isDone = !updatedSeries[seriesIndex].isDone)
                updatedSeries[seriesIndex] = toggledSeries
                if (seriesIndex == updatedSeries.lastIndex && toggledSeries.isDone) {
                    updatedSeries.add(
                        SeriesItem(
                            setNumber = updatedSeries.size + 1,
                            weight = "",
                            reps = "",
                            isDone = false
                        )
                    )
                }
                exercise.copy(series = updatedSeries)
            } else exercise
        }
    }

    suspend fun saveWorkoutData(): WorkoutModel {
        _saveState.value = "Guardando..."
        stopTimer()
        try {
            val workout = calculateAndSetWorkoutData()
            val savedWorkout = workoutRepository.saveWorkout(workout)
            val primaryMusclesData =
                muscleRepository.fetchMusclesFromIds(savedWorkout.primaryMuscleIds.toSet())
            _primaryMuscles.value = primaryMusclesData.map { it.name }
            val secondaryMusclesData =
                muscleRepository.fetchMusclesFromIds(savedWorkout.secondaryMuscleIds.toSet())
            _secondaryMuscles.value = secondaryMusclesData.map { it.name }
            _saveState.value = "Entrenamiento guardado"
            return savedWorkout
        } catch (e: Exception) {
            _saveState.value = "Error: ${e.message}"
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


        val primaryMuscleIds = _exercisesWithSeries.value
            .map { it.primaryMuscleRef?.id ?: "No muscle ref" }
            .toSet()
            .toList()

        val secondaryMuscleIds = _exercisesWithSeries.value
            .flatMap { it.secondaryMusclesRef.map { ref -> ref?.id ?: "No muscle ref" } }
            .toSet()
            .toList()

        val workoutType = determineWorkoutType(
            _exercisesWithSeries.value,
            totalVolume,
            _workoutDuration.value / 1000
        )
        val intensity = calculateIntensity(
            _exercisesWithSeries.value,
            totalVolume,
            _workoutDuration.value / 1000,
            userWeight
        )

        return WorkoutModel(
            id = "",
            userId = userRepository.getCurrentUser().uid,
            date = Timestamp.now(),
            exercises = _exercisesWithSeries.value,
            calories = totalCalories.toInt(),
            volume = totalVolume.toInt(),
            intensity = intensity,
            workoutType = workoutType,
            primaryMuscleIds = primaryMuscleIds,
            secondaryMuscleIds = secondaryMuscleIds,
            duration = _workoutDuration.value / 1000
        )
    }
}


private fun determineWorkoutType(
    exercises: List<ExerciseWithSeries>,
    totalVolume: Double,
    durationSeconds: Long
): String {
    val totalSeries = exercises.sumOf { it.series.size }
    val avgRepsPerSeries = exercises.flatMap { it.series }
        .filter { it.isDone }
        .mapNotNull { it.reps?.toIntOrNull() }
        .average()
        .takeIf { !it.isNaN() } ?: 0.0
    val avgWeightPerSeries = exercises.flatMap { it.series }
        .filter { it.isDone }
        .mapNotNull { it.weight?.toDoubleOrNull() }
        .average()
        .takeIf { !it.isNaN() } ?: 0.0
    val hasWeights = exercises.any { it.requiresWeight }
    val durationMinutes = durationSeconds / 60

    return when {
        // Fuerza: Alto peso, pocas repeticiones
        hasWeights && avgWeightPerSeries > 20.0 && avgRepsPerSeries <= 8.0 -> "Fuerza"
        // Resistencia: Muchas repeticiones, bajo peso
        avgRepsPerSeries > 12.0 && (!hasWeights || avgWeightPerSeries < 15.0) -> "Resistencia"
        // Cardio: Sin pesos, larga duración
        !hasWeights && durationMinutes > 30 -> "Cardio"
        // Mixto: Combinación de factores o no encaja claramente en otra categoría
        else -> "Mixto"
    }
}

private fun calculateIntensity(
    exercises: List<ExerciseWithSeries>,
    totalVolume: Double,
    durationSeconds: Long,
    userWeight: Double
): Double {
    val totalCompletedSeries = exercises.flatMap { it.series }.count { it.isDone }
    val totalReps = exercises.flatMap { it.series }
        .filter { it.isDone }
        .sumOf { it.reps?.toIntOrNull() ?: 0 }
    val durationMinutes = durationSeconds / 60.0

    // Normalizar volumen relativo al peso del usuario
    val volumeIntensity = (totalVolume / userWeight).coerceIn(0.0, 50.0) // Máx 50% de contribución

    // Intensidad por repeticiones y series completadas
    val effortIntensity =
        (totalCompletedSeries * 2 + totalReps * 0.5).coerceIn(0.0, 30.0) // Máx 30%

    // Intensidad por duración (inversa: menos tiempo con más volumen = más intenso)
    val timeIntensity = if (durationMinutes > 0) {
        (totalVolume / durationMinutes).coerceIn(0.0, 20.0) // Máx 20%
    } else 0.0

    // Suma ponderada para un rango de 0 a 100
    val intensity = (volumeIntensity + effortIntensity + timeIntensity).coerceIn(0.0, 100.0)
    return if (intensity > 0) intensity else 10.0
}