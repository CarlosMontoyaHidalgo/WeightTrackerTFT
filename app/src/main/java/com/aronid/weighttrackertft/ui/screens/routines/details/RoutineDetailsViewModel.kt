package com.aronid.weighttrackertft.ui.screens.routines.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.aronid.weighttrackertft.data.exercises.ExerciseRepository
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
    private val exerciseRepository: ExerciseRepository,
) : ViewModel() {
    private val _routine = MutableStateFlow<RoutineModel?>(null)
    val routine: StateFlow<RoutineModel?> = _routine.asStateFlow()

    private val _exercises = MutableStateFlow<List<ExerciseModel>>(emptyList())
    val exercises: StateFlow<List<ExerciseModel>> = _exercises.asStateFlow()

    fun loadRoutineDetails(routineId: String) {
        viewModelScope.launch {
            val getRoutine = routinePredefinedRepository.getRoutineById(routineId)
            _routine.value = getRoutine

            if (getRoutine != null) {
                val getExercises = routinePredefinedRepository.getExercisesByRoutineId(routineId)
                _exercises.value = getExercises
            }

        }
    }
}