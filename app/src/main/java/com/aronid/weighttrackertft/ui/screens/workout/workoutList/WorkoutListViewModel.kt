package com.aronid.weighttrackertft.ui.screens.workout.workoutList

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aronid.weighttrackertft.data.user.UserRepository
import com.aronid.weighttrackertft.data.workout.WorkoutModel
import com.aronid.weighttrackertft.data.workout.WorkoutRepository
import com.google.firebase.Timestamp
import com.itextpdf.kernel.colors.ColorConstants
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WorkoutListViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _dateFilter = MutableStateFlow<Pair<Timestamp?, Timestamp?>>(null to null)
    val dateFilter: StateFlow<Pair<Timestamp?, Timestamp?>> = _dateFilter.asStateFlow()

    val workouts: Flow<PagingData<WorkoutModel>> =
        dateFilter.flatMapLatest { (startDate, endDate) ->
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = false,
                    initialLoadSize = 20
                )
            ) {
                val userId = userRepository.getCurrentUser().uid
                Log.d(
                    "WorkoutListViewModel",
                    "Fetching workouts for userId: $userId, startDate: $startDate, endDate: $endDate"
                )
                workoutRepository.getWorkoutsPagingSource(userId, startDate, endDate)
            }.flow
        }.cachedIn(viewModelScope)

    private val _selectedWorkouts = mutableStateMapOf<String, Boolean>()
    val selectedWorkouts: SnapshotStateMap<String, Boolean> = _selectedWorkouts

    fun toggleSelection(workoutId: String, selected: Boolean) {
        if (selected) _selectedWorkouts[workoutId] = true else _selectedWorkouts.remove(workoutId)
    }

    fun deleteSelectedWorkouts(onResult: (Boolean) -> Unit) {
        val idsToDelete = _selectedWorkouts.keys.toList()
        viewModelScope.launch {
            try {
                workoutRepository.deleteWorkouts(idsToDelete)
                _selectedWorkouts.clear()
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun setDateFilter(startDate: Timestamp?, endDate: Timestamp?) {
        val adjustedEndDate = endDate?.let {
            val endLocalDate = it.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val endOfDay = endLocalDate.atTime(23, 59, 59, 999999999)
            Timestamp(Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant()))
        } ?: startDate?.let {
            val startLocalDate =
                it.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val endOfDay = startLocalDate.atTime(23, 59, 59, 999999999)
            Timestamp(Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant()))
        }

        _dateFilter.value = startDate to adjustedEndDate
    }

    fun clearDateFilter() {
        _dateFilter.value = null to null
    }

    fun exportSelectedWorkoutsAsCsv(
        context: Context,
        uri: Uri,
        workoutIds: List<String>,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val workouts = workoutRepository.getWorkoutsByIds(
                    userRepository.getCurrentUser().uid,
                    workoutIds
                )
                val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    CSVWriter(OutputStreamWriter(outputStream)).use { writer ->
                        // Headers
                        writer.writeNext(
                            arrayOf(
                                "Fecha",
                                "Tipo",
                                "Duración (min)",
                                "Músculos Principales",
                                "Músculos Secundarios",
                                "Calorías",
                                "Volumen",
                                "Notas"
                            )
                        )
                        // Data
                        workouts.forEach { workout ->
                            writer.writeNext(
                                arrayOf(
                                    formatter.format(workout.date?.toDate()),
                                    workout.workoutType,
                                    workout.duration.toString(),
                                    workout.primaryMuscleIds.joinToString("; "),
                                    workout.secondaryMuscleIds.joinToString("; "),
                                    workout.calories.toString(),
                                    workout.volume.toString(),
                                )
                            )
                        }
                    }
                    onResult(true)
                } ?: throw IOException("Error al crear archivo")
            } catch (e: Exception) {
                Log.e("CSV Export", "Error: ${e.message}")
                onResult(false)
            }
        }
    }

    fun exportSelectedWorkoutsAsPdf(
        context: Context,
        uri: Uri,
        workoutIds: List<String>,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            var document: Document? = null
            try {
                Log.d("PDF Export", "Exporting selected workouts: $workoutIds")
                val workouts = workoutRepository.getWorkoutsByIds(
                    userRepository.getCurrentUser().uid,
                    workoutIds
                )
                Log.d("PDF Export", "Fetched workouts: $workouts")
                val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val reportDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    PdfWriter(outputStream).use { pdfWriter ->
                        PdfDocument(pdfWriter).use { pdfDocument ->
                            document = Document(pdfDocument, PageSize.A4).apply {
                                // Title
                                add(
                                    Paragraph("Reporte de Entrenamientos")
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
                                summaryTable.addCell("Total de entrenamientos")
                                    .addCell(workouts.size.toString())
                                summaryTable.addCell("Calorías totales")
                                    .addCell(workouts.sumOf { it.calories }.toString())
                                summaryTable.addCell("Volumen total")
                                    .addCell(workouts.sumOf { it.volume }.toString())
                                add(summaryTable.setMarginBottom(20f))

                                // Workout Details
                                workouts.forEachIndexed { index, workout ->
                                    // Workout Header
                                    add(
                                        Paragraph("Entrenamiento ${index + 1}: ${workout.workoutType}")
                                            .setFontSize(14f)
                                            .setBold()
                                            .setMarginTop(15f)
                                            .setMarginBottom(10f)
                                            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                            .setPadding(5f)
                                    )

                                    // Workout Details Table
                                    val table =
                                        Table(UnitValue.createPercentArray(floatArrayOf(40f, 60f)))
                                            .useAllAvailableWidth()
                                    table.addCell("Fecha")
                                        .addCell(formatter.format(workout.date?.toDate()))
                                    table.addCell("Tipo").addCell(workout.workoutType)
                                    table.addCell("Duración").addCell("${workout.duration} minutos")
                                    table.addCell("Calorías").addCell("${workout.calories} kcal")
                                    table.addCell("Volumen").addCell("${workout.volume} kg")
                                    table.addCell("Músculos Principales")
                                        .addCell(workout.primaryMuscleIds.joinToString(", "))
                                    table.addCell("Músculos Secundarios")
                                        .addCell(workout.secondaryMuscleIds.joinToString(", "))

                                    add(table.setMarginBottom(15f))
                                }
                            }
                        }
                    }
                    onResult(true)
                } ?: throw IOException("Error al crear PDF")
            } catch (e: Exception) {
                Log.e("PDF Export", "Error: ${e.message}")
                onResult(false)
            } finally {
                document?.close()
            }
        }
    }
}