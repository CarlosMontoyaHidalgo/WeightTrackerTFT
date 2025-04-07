package com.aronid.weighttrackertft.ui.screens.progress.charts

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
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ChartsViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _caloriesData = MutableStateFlow<Map<String, Int>>(emptyMap())
    val caloriesData: StateFlow<Map<String, Int>> = _caloriesData.asStateFlow()

    private val _totalCalories = MutableStateFlow<Int?>(null)
    val totalCalories: StateFlow<Int?> = _totalCalories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentWeek = LocalDate.now().get(WeekFields.of(Locale.getDefault()).weekOfYear())
    private var currentYear = LocalDate.now().year

    init {
        loadCaloriesByWeek(currentWeek, currentYear)
    }

    fun loadCalories(startTimestamp: Timestamp?, endTimestamp: Timestamp?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val start = startTimestamp ?: Timestamp.now()
                val end = endTimestamp ?: start
                val workouts = workoutRepository.getWorkoutsInDateRange(start, end)
                val caloriesByDay = groupByDay(workouts, start, end)
                _caloriesData.value = caloriesByDay
                _totalCalories.value = caloriesByDay.values.sum()
            } catch (e: Exception) {
                _caloriesData.value = emptyMap()
                _totalCalories.value = 0
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadCaloriesByWeek(week: Int, year: Int) {
        val startOfWeek = LocalDate.of(year, 1, 1)
            .with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1) // Monday
            .plusWeeks((week - 1).toLong())
        val endOfWeek = startOfWeek.plusDays(6)

        val startTimestamp = Timestamp(startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val endTimestamp = Timestamp(endOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant())

        currentWeek = week
        currentYear = year
        loadCalories(startTimestamp, endTimestamp)
    }

    private fun groupByDay(workouts: List<WorkoutModel>, startDate: Timestamp, endDate: Timestamp): Map<String, Int> {
        val result = mutableMapOf<String, Int>()
        val calendar = Calendar.getInstance().apply { time = startDate.toDate() }
        val endCalendar = Calendar.getInstance().apply { time = endDate.toDate() }
        var dayIndex = 1

        while (calendar.time <= endCalendar.time) {
            val dayStart = Timestamp(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val dayEnd = Timestamp(calendar.time)
            val dayCalories = workouts.filter {
                val workoutTime = it.date ?: Timestamp.now()
                workoutTime >= dayStart && workoutTime < dayEnd
            }.sumOf { it.calories }
            result["Día $dayIndex"] = dayCalories
            dayIndex++
        }
        return result
    }

    fun getWeekRangeText(): String {
        val spanishLocale = Locale("es", "ES")
        val weekFields = WeekFields.of(spanishLocale)
        val startOfWeek = LocalDate.of(currentYear, 1, 1)
            .with(weekFields.dayOfWeek(), 1)
            .plusWeeks((currentWeek - 1).toLong())
        val endOfWeek = startOfWeek.plusDays(6)
        val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM", spanishLocale)
        return "${startOfWeek.format(formatter)} - ${endOfWeek.format(formatter)}"
    }
}

// Utility extension from your utils package
fun Timestamp.formatSpanish(): String {
    val date = this.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es", "ES"))
    return date.format(formatter)
}