package com.aronid.weighttrackertft.ui.screens.progress.charts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.workout.WorkoutModel
import com.aronid.weighttrackertft.data.workout.WorkoutRepository
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ChartsViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
): ViewModel() {

    private val _caloriesData = MutableStateFlow<Map<String, Int>>(emptyMap())
    val caloriesData: StateFlow<Map<String, Int>> = _caloriesData.asStateFlow()

    private val _totalCalories = MutableStateFlow<Int?>(null)
    val totalCalories: StateFlow<Int?> = _totalCalories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadCalories(period: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (startDate, endDate) = getDateRange(period)
                val workouts = workoutRepository.getWorkoutsInDateRange(startDate, endDate)
                val caloriesByUnit = when (period.lowercase()) {
                    "weekly" -> groupByDay(workouts)
                    "monthly" -> groupByWeek(workouts, startDate)
                    "yearly" -> groupByMonth(workouts)
                    else -> mapOf(period to workouts.sumOf { it.calories })
                }
                _caloriesData.value = caloriesByUnit
                _totalCalories.value = caloriesByUnit.values.sum()
            } catch (e: Exception) {
                _caloriesData.value = emptyMap()
                _totalCalories.value = 0
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getDateRange(period: String, referenceDate: Timestamp? = null): Pair<Timestamp, Timestamp> {
        val calendar = Calendar.getInstance()
        referenceDate?.let { calendar.time = it.toDate() }

        when (period.lowercase()) {
            "weekly" -> calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            "monthly" -> calendar.set(Calendar.DAY_OF_MONTH, 1)
            "yearly" -> {
                calendar.set(Calendar.MONTH, Calendar.JANUARY)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
            }
            else -> throw IllegalArgumentException("Invalid period: $period")
        }

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = Timestamp(calendar.time)

        when (period.lowercase()) {
            "weekly" -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            "monthly" -> calendar.add(Calendar.MONTH, 1)
            "yearly" -> calendar.add(Calendar.YEAR, 1)
        }

        val endDate = Timestamp(calendar.time)
        return startDate to endDate
    }

    private fun groupByDay(workouts: List<WorkoutModel>): Map<String, Int> {
        val result = mutableMapOf<String, Int>()
        val calendar = Calendar.getInstance()
        for (i in 1..7) {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            calendar.add(Calendar.DAY_OF_YEAR, i - 1)
            val dayStart = Timestamp(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val dayEnd = Timestamp(calendar.time)
            val dayCalories = workouts.filter {
                val workoutTime = it.date ?: Timestamp.now()
                workoutTime >= dayStart && workoutTime < dayEnd
            }.sumOf { it.calories }
            result["Día $i"] = dayCalories
        }
        return result
    }

    private fun groupByWeek(workouts: List<WorkoutModel>, startDate: Timestamp): Map<String, Int> {
        val result = mutableMapOf<String, Int>()
        val calendar = Calendar.getInstance().apply { time = startDate.toDate() }
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val weeksInMonth = (daysInMonth + 6) / 7 // Redondea hacia arriba
        for (i in 1..weeksInMonth) {
            val weekStart = Timestamp(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 7)
            val weekEnd = Timestamp(calendar.time)
            val weekCalories = workouts.filter {
                val workoutTime = it.date ?: Timestamp.now()
                workoutTime >= weekStart && workoutTime < weekEnd
            }.sumOf { it.calories }
            result["Sem $i"] = weekCalories
            if (calendar.get(Calendar.DAY_OF_MONTH) > daysInMonth) break
        }
        return result
    }

    private fun groupByMonth(workouts: List<WorkoutModel>): Map<String, Int> {
        val result = mutableMapOf<String, Int>()
        val calendar = Calendar.getInstance()
        for (i in 1..12) {
            calendar.set(Calendar.MONTH, i - 1)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val monthStart = Timestamp(calendar.time)
            calendar.add(Calendar.MONTH, 1)
            val monthEnd = Timestamp(calendar.time)
            val monthCalories = workouts.filter {
                val workoutTime = it.date ?: Timestamp.now()
                workoutTime >= monthStart && workoutTime < monthEnd
            }.sumOf { it.calories }
            result["Mes $i"] = monthCalories
        }
        return result
    }

    init {
        loadCalories("weekly")
    }

}