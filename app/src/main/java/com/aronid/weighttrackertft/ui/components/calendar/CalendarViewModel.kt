package com.aronid.weighttrackertft.ui.components.calendar

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
import java.time.ZoneOffset
import javax.inject.Inject
import android.util.Log
import java.time.YearMonth

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _workouts = MutableStateFlow<List<WorkoutModel>>(emptyList())
    val workouts: StateFlow<List<WorkoutModel>> = _workouts.asStateFlow()



    private val _accountCreationDate = MutableStateFlow<Timestamp?>(null)
    val accountCreationDate: StateFlow<Timestamp?> = _accountCreationDate.asStateFlow()

    init {
        fetchAccountCreationDate()
        fetchAllWorkouts() // Cargar todos los entrenamientos desde accountCreationDate
    }

    fun fetchAllWorkouts() {
    Log.d("CalendarViewModel", "CalendarViewModel initialized")
    Log.d("CalendarViewModel", "Account creation date: ${_accountCreationDate.value}")
        Log.d("CalendarViewModel", "workouts: ${_workouts.value}")
        viewModelScope.launch {
            val currentDate = LocalDate.now()
            val creationDate = _accountCreationDate.value?.let { Timestamp(it.seconds, it.nanoseconds) }
                ?: Timestamp(currentDate.minusYears(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), 0)

            val startTimestamp = creationDate
            val endTimestamp = Timestamp(currentDate.plusYears(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), 0)

            val workouts = workoutRepository.getWorkoutsInDateRange(startTimestamp, endTimestamp)
            _workouts.value = workouts
            Log.d("CalendarViewModel", "Fetched all workouts: ${workouts.size}")
        }
    }

    fun fetchWorkoutsForMonth(yearMonth: YearMonth) {
        viewModelScope.launch {
            val startDate = yearMonth.atDay(1)
            val endDate = yearMonth.atEndOfMonth()
            val startTimestamp = Timestamp(startDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC), 0)
            val endTimestamp = Timestamp(endDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC), 0)

            val creationDate = _accountCreationDate.value
            val filteredStart = if (creationDate != null) maxOf(startTimestamp, creationDate) else startTimestamp

            val workouts = workoutRepository.getWorkoutsInDateRange(filteredStart, endTimestamp)
            _workouts.value = workouts
            Log.d("CalendarViewModel", "Fetched workouts for month ${yearMonth}: ${workouts.size}")
        }
    }

    fun fetchWorkoutsForWeek(startDate: LocalDate, endDate: LocalDate) {
        viewModelScope.launch {
            val startTimestamp = Timestamp(startDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC), 0)
            val endTimestamp = Timestamp(endDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC), 0)

            val creationDate = _accountCreationDate.value
            val filteredStart = if (creationDate != null) maxOf(startTimestamp, creationDate) else startTimestamp

            val workouts = workoutRepository.getWorkoutsInDateRange(filteredStart, endTimestamp)
            _workouts.value = workouts
            Log.d("CalendarViewModel", "Fetched workouts for week ${startDate} to ${endDate}: ${workouts.size}")
        }
    }

    private fun fetchAccountCreationDate() {
        viewModelScope.launch {
            val result = userRepository.getAccountCreationDate()
            result.fold(
                onSuccess = { date ->
                    Log.d("CalendarViewModel", "Fecha de creación obtenida: $date")
                    _accountCreationDate.value = date
                    fetchAllWorkouts() // Volver a cargar todos los workouts con la fecha de creación
                },
                onFailure = { exception ->
                    Log.e("CalendarViewModel", "Error al obtener fecha de creación: $exception")
                    _accountCreationDate.value = null
                }
            )
        }
    }

    // Helper function
    private fun maxOf(timestamp1: Timestamp, timestamp2: Timestamp): Timestamp {
        return if (timestamp1.seconds > timestamp2.seconds) timestamp1 else timestamp2
    }
}