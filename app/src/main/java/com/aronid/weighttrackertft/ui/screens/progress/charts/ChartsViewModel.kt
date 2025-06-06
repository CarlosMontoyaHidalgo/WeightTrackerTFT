package com.aronid.weighttrackertft.ui.screens.progress.charts

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.user.UserProgressModel
import com.aronid.weighttrackertft.data.user.UserProgressRepository
import com.aronid.weighttrackertft.data.user.UserRepository
import com.aronid.weighttrackertft.data.workout.WorkoutModel
import com.aronid.weighttrackertft.data.workout.WorkoutRepository
import com.google.firebase.Timestamp
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
import com.opencsv.CSVWriter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Date
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

    private val _averageCalories = MutableStateFlow<Int?>(null)
    val averageCalories: StateFlow<Int?> = _averageCalories.asStateFlow()

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

    fun getCurrentMonthRange(): Pair<LocalDate?, LocalDate?> {
        val today = LocalDate.now()
        val start = today.withDayOfMonth(1)
        val end = today.withDayOfMonth(today.lengthOfMonth())
        return Pair(start, end)
    }

    fun getCurrentYearRange(): Pair<LocalDate?, LocalDate?> {
        val today = LocalDate.now()
        val start = today.withDayOfYear(1)
        val end = today.withDayOfYear(today.lengthOfYear())
        return Pair(start, end)
    }

    fun getCurrentWeekRange(): Pair<LocalDate, LocalDate> {
        val end = LocalDate.now()
        val start = end.minusDays(6)
        return start to end
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
                    _isLoading.value = false
                    return@launch
                }

                val daysCount = _caloriesData.value.size //cuenta todos los dias registrados incluso los que tienen valor 0
                val total = _totalCalories.value ?: 0
                val nonZeroDays = _caloriesData.value.values.count { it > 0 }
                _averageCalories.value = if (nonZeroDays > 0) total / nonZeroDays else 0

                val workouts = workoutRepository.getWorkoutsInDateRange(start, end)
                _caloriesData.value = groupByDayForCalories(workouts, start, end)
                _totalCalories.value = caloriesData.value.values.sum()
                _volumeData.value = groupByDayForVolume(workouts, start, end)

                val progressResult =
                    userProgressRepository.getProgressInDateRange(start, end, userId)
                progressResult.onSuccess { progressEntries ->

                    _weightData.value = groupByDayForWeight(progressEntries, start, end)
                }.onFailure {
                    _weightData.value = emptyMap()
                }
            } catch (e: Exception) {
                _caloriesData.value = emptyMap()
                _totalCalories.value = 0
                _volumeData.value = emptyMap()
                _weightData.value = emptyMap()
                _averageCalories.value = null
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

    fun exportDataAsCsv(
        context: Context,
        uri: Uri,
        dateRangeText: String,
        onResult: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    CSVWriter(OutputStreamWriter(outputStream)).use { writer ->
                        writer.writeNext(arrayOf("# Reporte de Progreso"))
                        writer.writeNext(arrayOf("# Período: $dateRangeText"))
                        writer.writeNext(
                            arrayOf(
                                "# Generado el: ${
                                    SimpleDateFormat(
                                        "dd/MM/yyyy",
                                        Locale.getDefault()
                                    ).format(Date())
                                }"
                            )
                        )
                        writer.writeNext(emptyArray())

                        writer.writeNext(
                            arrayOf(
                                "Día",
                                "Peso (kg)",
                                "Calorías (kcal)",
                                "Volumen (kg)"
                            )
                        )

                        // Datos
                        _weightData.value.forEach { (day, weight) ->
                            val calories = _caloriesData.value[day] ?: 0
                            val volume = _volumeData.value[day] ?: 0
                            writer.writeNext(
                                arrayOf(
                                    day,
                                    weight.toString(),
                                    calories.toString(),
                                    volume.toString()
                                )
                            )
                        }

                        // Añadir resumen al final
                        writer.writeNext(emptyArray())
                        writer.writeNext(arrayOf("# Resumen"))
                        writer.writeNext(
                            arrayOf(
                                "# Total días:",
                                _weightData.value.size.toString()
                            )
                        )
                        writer.writeNext(
                            arrayOf(
                                "# Calorías totales:",
                                _totalCalories.value?.toString() ?: "0"
                            )
                        )
                        writer.writeNext(
                            arrayOf(
                                "# Peso máximo:",
                                _weightData.value.values.maxOrNull()?.toString() ?: "0.0"
                            )
                        )
                        writer.writeNext(
                            arrayOf(
                                "# Volumen total:",
                                _volumeData.value.values.sum().toString()
                            )
                        )
                    }
                    onResult(true)
                } ?: throw IOException("Error al crear archivo CSV")
            } catch (e: Exception) {
                Log.e("CSV Export", "Error: ${e.message}")
                onResult(false)
            }
        }
    }

    fun exportDataAsPdf(
        context: Context,
        uri: Uri,
        dateRangeText: String,
        onResult: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            var document: Document? = null
            try {
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val reportDate = formatter.format(Date())

                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    PdfWriter(outputStream).use { pdfWriter ->
                        PdfDocument(pdfWriter).use { pdfDocument ->
                            document = Document(pdfDocument, PageSize.A4).apply {
                                // Title
                                add(
                                    Paragraph("Reporte de Progreso")
                                        .setFontSize(20f)
                                        .setBold()
                                        .setTextAlignment(TextAlignment.CENTER)
                                        .setMarginBottom(10f)
                                )
                                // Report Date
                                add(
                                    Paragraph("Generado el: $reportDate")
                                        .setFontSize(12f)
                                        .setTextAlignment(TextAlignment.CENTER)
                                        .setMarginBottom(20f)
                                )

                                add(
                                    Paragraph("Período: $dateRangeText")
                                        .setFontSize(12f)
                                        .setTextAlignment(TextAlignment.CENTER)
                                        .setMarginBottom(5f)
                                )

                                // Summary
                                add(
                                    Paragraph("Resumen")
                                        .setFontSize(14f)
                                        .setBold()
                                        .setMarginBottom(10f)
                                )
                                val summaryTable =
                                    Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f)))
                                        .useAllAvailableWidth()
                                summaryTable.addCell("Total de días registrados")
                                    .addCell(_weightData.value.size.toString())
                                summaryTable.addCell("Calorías totales")
                                    .addCell(_totalCalories.value.toString())
                                summaryTable.addCell("Peso máximo")
                                    .addCell("${_weightData.value.values.maxOrNull() ?: 0.0} kg")
                                summaryTable.addCell("Volumen total")
                                    .addCell("${_volumeData.value.values.sum()} kg")
                                add(summaryTable.setMarginBottom(20f))

                                // Details
                                add(
                                    Paragraph("Detalles por Día")
                                        .setFontSize(14f)
                                        .setBold()
                                        .setMarginBottom(10f)
                                )
                                val table =
                                    Table(
                                        UnitValue.createPercentArray(
                                            floatArrayOf(
                                                25f,
                                                25f,
                                                25f,
                                                25f
                                            )
                                        )
                                    )
                                        .useAllAvailableWidth()
                                table.addCell("Día")
                                    .addCell("Peso (kg)")
                                    .addCell("Calorías (kcal)")
                                    .addCell("Volumen (kg)")
                                _weightData.value.forEach { (day, weight) ->
                                    val calories = _caloriesData.value[day] ?: 0
                                    val volume = _volumeData.value[day] ?: 0
                                    table.addCell(day)
                                        .addCell(weight.toString())
                                        .addCell(calories.toString())
                                        .addCell(volume.toString())
                                }
                                add(table.setMarginBottom(15f))
                            }
                        }
                    }
                    onResult(true)
                } ?: throw IOException("Error al crear archivo PDF")
            } catch (e: Exception) {
                Log.e("PDF Export", "Error: ${e.message}")
                onResult(false)
            } finally {
                document?.close()
            }
        }
    }

}
