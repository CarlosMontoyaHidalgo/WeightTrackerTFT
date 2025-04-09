package com.aronid.weighttrackertft.ui.screens.routines.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.aronid.weighttrackertft.data.routine.RoutineCustomRepository
import com.aronid.weighttrackertft.data.routine.RoutineModel
import com.aronid.weighttrackertft.data.routine.RoutinePredefinedRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class RoutineDetailsViewModel @Inject constructor(
    private val routinePredefinedRepository: RoutinePredefinedRepository,
    private val customRepository: RoutineCustomRepository,
) : ViewModel() {
    private val _routine = MutableStateFlow<RoutineModel?>(null)
    val routine: StateFlow<RoutineModel?> = _routine.asStateFlow()

    private val _exercises = MutableStateFlow<List<ExerciseModel>>(emptyList())
    val exercises: StateFlow<List<ExerciseModel>> = _exercises.asStateFlow()

    private val _isFavorite = MutableStateFlow<Boolean>(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    init {
        _isFavorite.value = false
    }

    fun toggleFavorite(routineId: String, isPredefined: Boolean) {
        viewModelScope.launch {
            customRepository.toggleFavorite(routineId, isPredefined)
            _isFavorite.value = customRepository.isFavorite(routineId)
        }
    }

    fun loadRoutineDetails(routineId: String, isPredefined: Boolean = false) {
        viewModelScope.launch {
            try {
                println("Loading routine details for ID: $routineId, isPredefined: $isPredefined")
                val loadedRoutine = if (isPredefined) {
                    routinePredefinedRepository.getRoutineById(routineId)?.copy(id = routineId)
                } else {
                    customRepository.getRoutineById(routineId)?.copy(id = routineId)
                }
                _routine.value = loadedRoutine
                println("Routine loaded: ${loadedRoutine?.name}, exercises refs: ${loadedRoutine?.exercises?.size}")

                val loadedExercises = if (isPredefined) {
                    routinePredefinedRepository.getExercisesForRoutine(routineId)
                } else {
                    customRepository.getExercisesForRoutine(routineId)
                }
                _exercises.value = loadedExercises
                println("Exercises loaded: ${loadedExercises.size}, names: ${loadedExercises.map { it.name }}")

                _isFavorite.value = customRepository.isFavorite(routineId)
            } catch (e: Exception) {
                println("Error loading routine details: ${e.message}")
            }
        }
    }
}