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
    val exercisesWithSeries: StateFlow<List<ExerciseWithSeries>> =
        _exercisesWithSeries.asStateFlow()

    private val _currentExerciseIndex = MutableStateFlow(0)
    val currentExerciseIndex: StateFlow<Int> = _currentExerciseIndex.asStateFlow()

    private val _workoutId = MutableStateFlow<String?>(null)
    val workoutId: StateFlow<String?> = _workoutId.asStateFlow()

    private val _saveState = MutableStateFlow<String?>(null)
    val saveState: StateFlow<String?> = _saveState.asStateFlow()

    private val _calories = MutableStateFlow<Int?>(null)
    val calories: StateFlow<Int?> = _calories.asStateFlow()

    private val _volume = MutableStateFlow<Int?>(null)
    val volume: StateFlow<Int?> = _volume.asStateFlow()

    fun loadRoutineExercises(routineId: String, isPredefined: Boolean = false) {
        Log.d("WorkoutViewModel", "loadRoutineExercises called with id: $routineId")
        viewModelScope.launch {

            val loadedRoutine = if (isPredefined) {
                routinePredefinedRepository.getRoutineById(routineId)
            } else {
                routineCustomRepository.getRoutineById(routineId)
            }
            _routineId.value = routineId
            //val routine = routineCustomRepository.getRoutineById(routineId)
            val exerciseRefs = loadedRoutine?.exercises ?: emptyList()
            val exercises = exerciseRefs.mapNotNull { ref ->
                ref.get().await().toObject(ExerciseModel::class.java)
            }
            // Initialize each exercise with one default series
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
                    requiresWeight = exercise.requiresWeight ?: true
                )
            }
        }
    }

    fun getCurrentExercise(): ExerciseWithSeries? {
        return _exercisesWithSeries.value.getOrNull(_currentExerciseIndex.value)
    }

    fun navigateToNextExercise() {
        if (_currentExerciseIndex.value < _exercisesWithSeries.value.size - 1) {
            _currentExerciseIndex.value++
        }
    }

    fun navigateToPreviousExercise() {
        if (_currentExerciseIndex.value > 0) {
            _currentExerciseIndex.value--
        }
    }

    fun updateSeriesWeight(seriesIndex: Int, newValue: String) {
        _exercisesWithSeries.value =
            _exercisesWithSeries.value.mapIndexed { exIndex, exerciseWithSeries ->
                if (exIndex == _currentExerciseIndex.value) {
                    val updatedSeries = exerciseWithSeries.series.toMutableList()
                    updatedSeries[seriesIndex] = updatedSeries[seriesIndex].copy(weight = newValue)
                    exerciseWithSeries.copy(series = updatedSeries)
                } else {
                    exerciseWithSeries
                }
            }
    }

    fun updateSeriesReps(seriesIndex: Int, newValue: String) {
        _exercisesWithSeries.value =
            _exercisesWithSeries.value.mapIndexed { exIndex, exerciseWithSeries ->
                if (exIndex == _currentExerciseIndex.value) {
                    val updatedSeries = exerciseWithSeries.series.toMutableList()
                    updatedSeries[seriesIndex] = updatedSeries[seriesIndex].copy(reps = newValue)
                    exerciseWithSeries.copy(series = updatedSeries)
                } else {
                    exerciseWithSeries
                }
            }
    }

    fun toggleSeriesCompletion(seriesIndex: Int) {
        val currentExercise = getCurrentExercise() ?: return
        val currentSeries = currentExercise.series.getOrNull(seriesIndex) ?: return
        if (currentSeries.weight.isNullOrEmpty() || currentSeries.reps.isNullOrEmpty()) {
            return
        }

        _exercisesWithSeries.value =
            _exercisesWithSeries.value.mapIndexed { exIndex, exerciseWithSeries ->
                if (exIndex == _currentExerciseIndex.value) {
                    val updatedSeries = exerciseWithSeries.series.toMutableList()
                    val toggledSeries = updatedSeries[seriesIndex].copy(
                        isDone = !updatedSeries[seriesIndex].isDone
                    )
                    updatedSeries[seriesIndex] = toggledSeries

                    if (seriesIndex == updatedSeries.lastIndex && toggledSeries.isDone) {
                        val newSetNumber = updatedSeries.size + 1
                        updatedSeries.add(
                            SeriesItem(
                                setNumber = newSetNumber,
                                weight = "",
                                reps = "",
                                isDone = false
                            )
                        )
                    }
                    exerciseWithSeries.copy(series = updatedSeries)
                } else {
                    exerciseWithSeries
                }
            }
    }


    fun saveWorkout(): String? {
        val workoutId: String? = null
        viewModelScope.launch {
            try {
                val workout = workoutData()
                workoutRepository.saveWorkout(workout)
                _workoutId.value = workoutId
                _saveState.value = "Entrenamiento guardado con exito"
            } catch (e: Exception) {
                _saveState.value = "Error al guardar el entrenamiento ${e.message}"
            }
        }
        return workoutId
    }

    private suspend fun workoutData(): WorkoutModel {
        val routineId = _routineId.value ?: throw IllegalStateException("Routine ID not available")
        val exercises = _exercisesWithSeries.value
        val date = Timestamp.now()
        val userId = userRepository.getCurrentUser().uid
        val calories = calculateCalories()
        val volume = calculateWorkoutVolume()
        val intensity = calculateWorkoutIntensity()
        val workoutType = calculateWorkoutType()
        return WorkoutModel(
            routineId,
            userId,
            date,
            exercises,
            calories,
            volume,
            intensity,
            workoutType
        )
    }

    suspend fun loadCalories(): Int {
        val calories = calculateWeightBasedCalories()
        _saveState.value = calories.toString()
        return calories
    }

    suspend fun calculateCalories(): Int {
        val timeStamp = calculateTimeBasedCalories()
        val weightBased = calculateWeightBasedCalories()
        return weightBased
    }

    private suspend fun calculateTimeBasedCalories(): Int {
        val userWeight = userRepository.getCurrentUserWeight() ?: 70.0 // Peso default 70kg
        var totalCalories = 0.0

        _exercisesWithSeries.value.forEach { exercise ->
            val met = exerciseRepository.getMETExercise(exercise.exerciseName ?: "")
            val durationHours = exercise.series.size * 1.5 / 60.0 // 1.5 min por serie
            totalCalories += met * userWeight * durationHours
        }

        return totalCalories.toInt()
    }

    private fun calculateWeightBasedCalories(): Int {
        var totalCalories = 0.0

        _exercisesWithSeries.value.forEach { exercise ->
            exercise.series.forEach { serie ->
                if (exercise.requiresWeight) {
                    val weight = serie.weight?.toDoubleOrNull() ?: 0.0
                    val reps = serie.reps?.toIntOrNull() ?: 0
                    // Fórmula: (peso * reps) * 0.025 (coeficiente para fuerza)
                    totalCalories += (weight * reps) * 0.025
                } else {
                    val reps = serie.reps?.toIntOrNull() ?: 0
                    // Fórmula: (reps) * 0.4 //0.4 calorias por repeticion
                    totalCalories += reps * 0.025
                }

            }
        }

        return totalCalories.toInt()
    }

    private suspend fun calculateMETCalories(): Int {
        val userWeight = userRepository.getCurrentUserWeight() ?: 70.0
        var totalCalories = 0.0

        _exercisesWithSeries.value.forEach { exercise ->
            if (!exercise.requiresWeight) {
                val met = exerciseRepository.getMETExercise(exercise.exerciseName ?: "")
                //suponiendo que cada rep dura aprox 1 min y medio
                val durationHours = exercise.series.size * 1.5 / 60.0
                totalCalories += met * userWeight * durationHours
            }
        }

        return totalCalories.toInt()
    }

    suspend fun calculateWorkoutVolume(): Double {
        var totalVolume = 0.0
        val userWeight = userRepository.getCurrentUserWeight() ?: 70.0 // Peso corporal base

        _exercisesWithSeries.value.forEach { exercise ->
            exercise.series.forEach { serie ->
                val weight = when {
                    exercise.requiresWeight -> serie.weight?.toDoubleOrNull() ?: 0.0
                    else -> userWeight * 0.6 // Usar % del peso corporal para ejercicios sin peso
                }
                val reps = serie.reps?.toIntOrNull() ?: 0
                totalVolume += weight * reps
            }
        }
        return totalVolume
    }

    private fun calculateWorkoutIntensity(): Double {
        var totalIntensity = 0.0
        var exerciseCount = 0

//        _exercisesWithSeries.value.forEach { exercise ->
//            exercise.series.forEach { serie ->
//                val maxWeight = getExerciseMaxWeight(exercise.exerciseName)
//                val currentWeight = serie.weight?.toDoubleOrNull() ?: 0.0
//                if (maxWeight > 0) {
//                    totalIntensity += (currentWeight / maxWeight) * 100
//                    exerciseCount++
//                }
//            }
//        }
        return if (exerciseCount > 0) totalIntensity / exerciseCount else 0.0
    }

    private fun calculateWorkoutType(): String {
        val repRanges = hashMapOf<Int, Int>()

        _exercisesWithSeries.value.flatMap { it.series }
            .forEach { serie ->
                val reps = serie.reps?.toIntOrNull() ?: 0
                repRanges[reps] = repRanges.getOrDefault(reps, 0) + 1
            }

        return when {
            repRanges.keys.any { it in 1..5 } -> "Fuerza Máxima"
            repRanges.keys.any { it in 6..12 } -> "Hipertrofia"
            repRanges.keys.any { it > 12 } -> "Resistencia"
            else -> "Mixto"
        }
    }
}