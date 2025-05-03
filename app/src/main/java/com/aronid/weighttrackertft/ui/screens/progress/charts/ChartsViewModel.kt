package com.aronid.weighttrackertft.ui.screens.progress.charts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.user.UserProgressModel
import com.aronid.weighttrackertft.data.user.UserProgressRepository
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
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ChartsViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val userProgressRepository: UserProgressRepository,
    private val userRepository: UserRepository // Repositorio para el perfil del usuario
) : ViewModel() {

    private val _caloriesData = MutableStateFlow<Map<String, Int>>(emptyMap())
    val caloriesData: StateFlow<Map<String, Int>> = _caloriesData.asStateFlow()

    private val _totalCalories = MutableStateFlow<Int?>(null)
    val totalCalories: StateFlow<Int?> = _totalCalories.asStateFlow()

    private val _volumeData = MutableStateFlow<Map<String, Int>>(emptyMap())
    val volumeData: StateFlow<Map<String, Int>> = _volumeData.asStateFlow()

    private val _weightData =
        MutableStateFlow<Map<String, Double>>(emptyMap()) // Historial de pesos como Double
    val weightData: StateFlow<Map<String, Double>> = _weightData.asStateFlow()

    private val _currentWeight =
        MutableStateFlow<Double?>(null) // Peso actual del perfil como Double
    val currentWeight: StateFlow<Double?> = _currentWeight.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentWeek = LocalDate.now().get(WeekFields.of(Locale.getDefault()).weekOfYear())
    private var currentYear = LocalDate.now().year

    private val _currentHeight = MutableStateFlow<Double?>(null)
    val currentHeight: StateFlow<Double?> = _currentHeight.asStateFlow()

    init {
        loadDataByWeek(currentWeek, currentYear)
        loadInitialData() // Cargar el peso actual al iniciar
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val result = userRepository.getUserProfile()
            result.onSuccess { user ->
                _currentWeight.value = user.weight
                _currentHeight.value = userRepository.getCurrentUserHeight()
            }.onFailure {
                _currentWeight.value = null
                _currentHeight.value = null
            }
        }
    }

    fun updateUserHeight(newHeight: Double) {
        viewModelScope.launch {
            userRepository.updateUserHeight(newHeight).let { success ->
                if (success) _currentHeight.value = newHeight
            }
        }
    }

    fun loadData(startTimestamp: Timestamp?, endTimestamp: Timestamp?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val start = startTimestamp ?: Timestamp.now()
                val end = endTimestamp ?: start

                val userId = try {
                    userRepository.getCurrentUser().uid
                } catch (e: Exception) {
                    Log.e("LoadData", "Error al obtener UID", e)
                    _isLoading.value = false
                    return@launch
                }

                val workouts = workoutRepository.getWorkoutsInDateRange(start, end)
                _caloriesData.value = groupByDayForCalories(workouts, start, end)
                _totalCalories.value = caloriesData.value.values.sum()
                _volumeData.value = groupByDayForVolume(workouts, start, end)

                val progressResult =
                    userProgressRepository.getProgressInDateRange(start, end, userId)
                progressResult.onSuccess { progressEntries ->
                    // Log para verificar los datos de peso recuperados
                    progressEntries.forEach {
                        Log.d(
                            "PesosDebug",
                            "Peso: ${it.weight} - Fecha: ${it.timestamp.toDate()} - UID: ${it.userId}"
                        )
                    }
                    _weightData.value = groupByDayForWeight(progressEntries, start, end)
                }.onFailure {
                    _weightData.value = emptyMap()
                    Log.e("PesosDebug", "Fallo al cargar pesos", it)
                }
            } catch (e: Exception) {
                _caloriesData.value = emptyMap()
                _totalCalories.value = 0
                _volumeData.value = emptyMap()
                _weightData.value = emptyMap()
                Log.e("LoadData", "Error inesperado", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadDataByWeek(week: Int, year: Int) {
        val startOfWeek = LocalDate.of(year, 1, 1)
            .with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1)
            .plusWeeks((week - 1).toLong())
        val endOfWeek = startOfWeek.plusDays(6)

        val startTimestamp = Timestamp(startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val endTimestamp = Timestamp(endOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant())

        currentWeek = week
        currentYear = year
        loadData(startTimestamp, endTimestamp)
    }

    // ChartsViewModel.kt
    fun loadCurrentWeek() {
        val currentWeek = LocalDate.now().get(WeekFields.of(Locale.getDefault()).weekOfYear())
        val currentYear = LocalDate.now().year
        loadDataByWeek(currentWeek, currentYear)
    }

    fun saveWeight(weight: Double, timestamp: Timestamp = Timestamp.now()) {
        viewModelScope.launch {
            val userId = try {
                userRepository.getCurrentUser().uid
            } catch (e: Exception) {
                return@launch // No guardamos si no hay usuario
            }

            val progress = UserProgressModel(
                userId = userId,
                weight = weight,
                timestamp = timestamp,
                caloriesConsumed = null,
                activityLevel = null,
                note = null
            )

            userProgressRepository.saveProgress(progress).onSuccess {
                loadDataByWeek(currentWeek, currentYear) // Actualizar historial
                _currentWeight.value = weight // Actualizar peso actual
                userRepository.updateUserWeight(weight) // Actualizar perfil del usuario
            }
        }
    }

    private fun groupByDayForCalories(
        workouts: List<WorkoutModel>,
        startDate: Timestamp,
        endDate: Timestamp
    ): Map<String, Int> {
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

    private fun groupByDayForVolume(
        workouts: List<WorkoutModel>,
        startDate: Timestamp,
        endDate: Timestamp
    ): Map<String, Int> {
        val result = mutableMapOf<String, Int>()
        val calendar = Calendar.getInstance().apply { time = startDate.toDate() }
        val endCalendar = Calendar.getInstance().apply { time = endDate.toDate() }
        var dayIndex = 1

        while (calendar.time <= endCalendar.time) {
            val dayStart = Timestamp(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val dayEnd = Timestamp(calendar.time)
            val dayVolume = workouts.filter {
                val workoutTime = it.date ?: Timestamp.now()
                workoutTime >= dayStart && workoutTime < dayEnd
            }.sumOf { it.volume }
            result["Día $dayIndex"] = dayVolume
            dayIndex++
        }
        return result
    }

    private fun groupByDayForWeight(
        progressEntries: List<UserProgressModel>,
        startDate: Timestamp,
        endDate: Timestamp
    ): Map<String, Double> {
        val result = mutableMapOf<String, Double>()
        val calendar = Calendar.getInstance().apply { time = startDate.toDate() }
        val endCalendar = Calendar.getInstance().apply { time = endDate.toDate() }
        var dayIndex = 1

        while (calendar.time <= endCalendar.time) {
            val dayStart = Timestamp(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val dayEnd = Timestamp(calendar.time)

            val dayWeights = progressEntries.filter {
                it.timestamp >= dayStart && it.timestamp < dayEnd
            }

            // Agregar log para verificar los registros de peso por día
            Log.d("PesosDebug", "Día $dayIndex: $dayWeights")

            val dayWeight = dayWeights.map { it.weight }.average().takeIf { it.isFinite() } ?: 0.0
            result["Día $dayIndex"] = dayWeight
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
