package com.aronid.weighttrackertft.ui.screens.routines.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.aronid.weighttrackertft.data.exercises.ExerciseRepository
import com.aronid.weighttrackertft.data.routine.RoutineCustomRepository
import com.aronid.weighttrackertft.data.routine.RoutineModel
import com.aronid.weighttrackertft.data.routine.RoutinePredefinedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineDetailsViewModel @Inject constructor(
    private val routinePredefinedRepository: RoutinePredefinedRepository,
    private val customRepository: RoutineCustomRepository
) : ViewModel() {
    private val _routine = MutableStateFlow<RoutineModel?>(null)
    val routine: StateFlow<RoutineModel?> = _routine.asStateFlow()

    private val _exercises = MutableStateFlow<List<ExerciseModel>>(emptyList())
    val exercises: StateFlow<List<ExerciseModel>> = _exercises.asStateFlow()

    fun loadRoutineDetails(routineId: String, isPredefined: Boolean = false) {
        viewModelScope.launch {
            val loadedRoutine = if (isPredefined) {
                routinePredefinedRepository.getRoutineById(routineId)?.copy(id = routineId)
            } else {
                customRepository.getRoutineById(routineId)?.copy(id = routineId)
            }
            _routine.value = loadedRoutine
            val loadedExercises = if (isPredefined) {
                routinePredefinedRepository.getExercisesForRoutine(routineId)
            } else {
                customRepository.getExercisesForRoutine(routineId)
            }
            _exercises.value = loadedExercises
        }
    }
}