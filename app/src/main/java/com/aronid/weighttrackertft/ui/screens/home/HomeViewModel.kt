package com.aronid.weighttrackertft.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.user.UserRepository
import com.aronid.weighttrackertft.data.workout.WorkoutModel
import com.aronid.weighttrackertft.data.workout.WorkoutRepository
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.DayOfWeek
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _weeklyWorkouts = MutableStateFlow<List<WorkoutModel>>(emptyList())
    val weeklyWorkouts: StateFlow <List<WorkoutModel>> = _weeklyWorkouts.asStateFlow()

    private val _userName = MutableStateFlow<String>("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    init {
        fetchWeeklyWorkouts()
        fetchUserData()
    }

    private fun fetchWeeklyWorkouts() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val startOfWeek = today.with(DayOfWeek.MONDAY)
            val endOfWeek = startOfWeek.plusDays(6)

            val startDate = Timestamp(startOfWeek.atStartOfDay().toEpochSecond(ZoneOffset.UTC), 0)
            val endDate = Timestamp(endOfWeek.atStartOfDay().toEpochSecond(ZoneOffset.UTC), 0)

            val workouts = workoutRepository.getWorkoutsInDateRange(startDate, endDate)
            _weeklyWorkouts.value = workouts
        }
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            val user = userRepository.getUserName()
            user.fold(
                onSuccess = { name ->
                    Log.d("HomeViewModel", "Nombre obtenido: $name")
                    _userName.value = name
                },
                onFailure = { exception ->
                    Log.e("HomeViewModel", "Error al obtener nombre: $exception")
                    _userName.value = "Usuario"
                }
            )
        }
    }

}