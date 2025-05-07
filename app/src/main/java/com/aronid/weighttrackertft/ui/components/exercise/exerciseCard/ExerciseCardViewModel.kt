package com.aronid.weighttrackertft.ui.components.exercise.exerciseCard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.exercises.ExerciseRepository
import com.google.firebase.firestore.DocumentReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ExerciseCardViewModel @Inject constructor(
    private val repository: ExerciseRepository
) : ViewModel() {

    private val _primaryMuscleName = MutableStateFlow("Loading...")
    val primaryMuscleName: StateFlow<String> = _primaryMuscleName.asStateFlow()
    private val _secondaryMuscleNames = MutableStateFlow(listOf<String>())
    val secondaryMuscleNames: StateFlow<List<String>> = _secondaryMuscleNames.asStateFlow()

    fun fetchPrimaryMuscleName(ref: DocumentReference?) {
        viewModelScope.launch {
            val name = async { repository.fetchMuscleName(ref) }.await()
            Log.d("ExerciseCardViewModel", "primaryMuscleName: $name")
            _primaryMuscleName.value = name
        }
    }

    fun fetchSecondaryMuscleNames(refs: List<DocumentReference>) {
        viewModelScope.launch {
            val names = refs.map { ref ->
                async { repository.fetchMuscleName(ref) }
            }.awaitAll()
            _secondaryMuscleNames.value = names
            Log.d(
                "ExerciseCardViewModel",
                "secondaryMuscleNames: ${_secondaryMuscleNames.value.joinToString()}"
            )
        }
    }
}