package com.aronid.weighttrackertft.ui.screens.exercises.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.exercises.ExerciseModel
import com.aronid.weighttrackertft.data.exercises.ExerciseRepository
import com.aronid.weighttrackertft.data.muscles.MuscleModel
import com.aronid.weighttrackertft.data.muscles.MuscleRepository
import com.aronid.weighttrackertft.data.routine.RoutineModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseDetailsViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val muscleRepository: MuscleRepository
): ViewModel(){

    private val _exercise = MutableStateFlow<ExerciseModel?>(null)
    val exercise: StateFlow<ExerciseModel?> = _exercise.asStateFlow()

    private val _primaryMuscleName = MutableStateFlow<String?>(null)
    val primaryMuscleName: StateFlow<String?> = _primaryMuscleName.asStateFlow()

    private val _secondaryMuscleNames = MutableStateFlow<List<String>>(emptyList())
    val secondaryMuscleNames: StateFlow<List<String>> = _secondaryMuscleNames.asStateFlow()


    fun loadExerciseDetails(exerciseId: String) {
        viewModelScope.launch {
            val exercise = exerciseRepository.getExerciseById(exerciseId)
            _exercise.value = exercise

            exercise?.let { ex ->
                _primaryMuscleName.value = ex.primaryMuscle?.let { ref ->
                    muscleRepository.fetchMuscleName(ref)
                }

                if (ex.secondaryMuscle.isNotEmpty()) {
                    _secondaryMuscleNames.value = ex.secondaryMuscle.map { ref ->
                        muscleRepository.fetchMuscleName(ref)
                    }
                }
            }
        }
    }
}