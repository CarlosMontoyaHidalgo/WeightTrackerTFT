package com.aronid.weighttrackertft.ui.screens.progress.charts

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.alertDialog.CustomAlertDialog
import com.aronid.weighttrackertft.ui.components.button.MyElevatedButton
import com.aronid.weighttrackertft.ui.components.calendar.WorkoutRangeCalendar
import com.aronid.weighttrackertft.ui.components.charts.GoalViewModel
import com.aronid.weighttrackertft.ui.components.sections.calories.CaloriesSection
import com.aronid.weighttrackertft.ui.components.sections.imc.BMISection
import com.aronid.weighttrackertft.ui.components.sections.volume.VolumeSection
import com.aronid.weighttrackertft.ui.components.sections.weight.WeightSection
import com.aronid.weighttrackertft.utils.formatShort
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

fun LocalDate.toTimestamp(): Timestamp {
    val instant = this.atStartOfDay(ZoneId.systemDefault()).toInstant()
    return Timestamp(Date.from(instant))
}


fun LocalDate.formatShort(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return this.format(formatter)
}


@Composable
fun ChartsScreen(innerPadding: PaddingValues, navHostController: NavHostController) {
    val viewModel: ChartsViewModel = hiltViewModel()
    val viewModelGoals: GoalViewModel = hiltViewModel()
    val averageCalories by viewModel.averageCalories.collectAsStateWithLifecycle()
    var newWeightInput by remember { mutableStateOf("") }

    val caloriesData by viewModel.caloriesData.collectAsStateWithLifecycle()
    val totalCalories by viewModel.totalCalories.collectAsStateWithLifecycle()
    val goalCalories by viewModelGoals.goalCalories.collectAsStateWithLifecycle()
    val volumeData by viewModel.volumeData.collectAsStateWithLifecycle()
    val weightData by viewModel.weightData.collectAsStateWithLifecycle()
    val currentWeight by viewModel.currentWeight.collectAsStateWithLifecycle()
    val currentHeight by viewModel.currentHeight.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val showGoalDialog by viewModelGoals.showGoalDialog.collectAsStateWithLifecycle()
    val showWeightDialog by viewModelGoals.showWeightDialog.collectAsStateWithLifecycle()

    var showDateRangePicker by remember { mutableStateOf(false) }
    var dateRangeText by remember { mutableStateOf(viewModel.getWeekRangeText()) }
    Log.d("ChartsScreen", "Date range text: $dateRangeText")
    var weightInput by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableStateOf(0) }
    var hasDateRangeSelected by remember { mutableStateOf(false) }
    var goalInput by remember { mutableStateOf("") }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportFormat by remember { mutableStateOf("") }

    var heightInput by remember { mutableStateOf("") }
    var bmiResult by remember { mutableStateOf<Double?>(null) }

    var selectedRangeType by remember { mutableStateOf("Semanal") }


    val context = LocalContext.current
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument(
            if (exportFormat == "csv") "text/csv" else "application/pdf"
        )
    ) { uri ->
        if (uri != null) {
            when (exportFormat) {
                "csv" -> viewModel.exportDataAsCsv(context, uri, dateRangeText) { success ->
                    // Handle success/failure (e.g., show a Toast)
                }

                "pdf" -> viewModel.exportDataAsPdf(context, uri, dateRangeText) { success ->
                    // Handle success/failure (e.g., show a Toast)
                }
            }
        }
    }

    val tabs = listOf(
        stringResource(R.string.tab_calories),
        stringResource(R.string.tab_volume),
        stringResource(R.string.tab_weight),
        stringResource(R.string.tab_bmi)
    )

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.your_progress),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = dateRangeText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(Modifier.weight(1f))
            Row {
                if (hasDateRangeSelected) {
                    IconButton(onClick = {
                        viewModel.loadCurrentWeek()
                        dateRangeText = viewModel.getWeekRangeText()
                        hasDateRangeSelected = false
                        showDateRangePicker = false
                        selectedRangeType = ""
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_x),
                            contentDescription = stringResource(R.string.clear_date_range),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
                IconButton(onClick = { showDateRangePicker = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar),
                        contentDescription = stringResource(R.string.select_date_range),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                if (selectedTabIndex == 0) {
                    IconButton(onClick = { viewModelGoals.triggerGoalDialog() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_goal),
                            contentDescription = stringResource(R.string.set_goal),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                if (selectedTabIndex == 2) {
                    IconButton(onClick = { viewModelGoals.triggerWeightDialog() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_weight),
                            contentDescription = stringResource(R.string.weight),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                IconButton(onClick = { showExportDialog = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_download),
                        contentDescription = stringResource(R.string.export_pdf),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                modifier = Modifier
                    .width(120.dp)
                    .height(48.dp),
                selected = selectedRangeType == "Mensual",
                onClick = {
                    selectedRangeType = "Mensual"
                    val (start, end) = viewModel.getCurrentMonthRange()
                    val startTimestamp = start?.toTimestamp()
                    val endTimestamp = end?.toTimestamp()
                    viewModel.loadData(startTimestamp, endTimestamp)
                    dateRangeText = "${start?.formatShort()} - ${end?.formatShort()}"
                    hasDateRangeSelected = true
                },
                label = {
                    Text(
                        "Mensual",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                leadingIcon = if (selectedRangeType == "Mensual") {
                    {
                        Icon(
                            Icons.Filled.Done,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
            )

            // Botón Anual con tamaño fijo
            FilterChip(
                modifier = Modifier
                    .width(120.dp) // Mismo tamaño que el anterior
                    .height(48.dp), // Misma altura
                selected = selectedRangeType == "Anual",
                onClick = {
                    selectedRangeType = "Anual"
                    val (start, end) = viewModel.getCurrentYearRange()
                    val startTimestamp = start?.toTimestamp()
                    val endTimestamp = end?.toTimestamp()
                    viewModel.loadData(startTimestamp, endTimestamp)
                    dateRangeText = "${start?.formatShort()} - ${end?.formatShort()}"
                    hasDateRangeSelected = true
                },
                label = {
                    Text(
                        "Anual",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center // Texto centrado
                    )
                },
                leadingIcon = if (selectedRangeType == "Anual") {
                    {
                        Icon(
                            Icons.Filled.Done,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
            )
        }

        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
Log.d("ChartsScreen", "Selected tab index: $caloriesData")
        Log.d("ChartsScreen", "Selected tab index: $averageCalories")

        when (selectedTabIndex) {
            0 -> CaloriesSection(
                caloriesData,
                totalCalories,
                goalCalories,
                averageCalories,
                isLoading,
                rangeType = selectedRangeType
            )

            1 -> VolumeSection(volumeData, isLoading, rangeType = selectedRangeType)
            2 -> WeightSection(
                weightData = weightData,
                currentWeight = currentWeight,
                isLoading = isLoading,
                caloriesData = caloriesData,
                rangeType = selectedRangeType
            )

            3 -> BMISection(currentWeight = currentWeight, currentHeight = currentHeight)
        }
    }

    if (showGoalDialog) {
        CustomAlertDialog(
            showDialog = showGoalDialog,
            onDismiss = { viewModelGoals.dismissGoalDialog() },
            onConfirm = {
                val goal = goalInput.toIntOrNull()
                if (goal != null) {
                    viewModelGoals.setGoalCalories(goal)
                }
            },
            title = stringResource(R.string.set_calorie_goal_title),
            text = stringResource(R.string.set_calorie_goal_message),
            confirmButtonText = stringResource(R.string.save),
            dismissButtonText = stringResource(R.string.cancel),
            customContent = {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = goalInput,
                        onValueChange = { goalInput = it },
                        label = { Text(stringResource(R.string.calorie_goal_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = goalInput.toIntOrNull() == null && goalInput.isNotEmpty()
                    )
                }
            }
        )
    }

    if (showWeightDialog) {
        CustomAlertDialog(
            showDialog = showWeightDialog,
            onDismiss = { viewModelGoals.dismissWeightDialog() },
            onConfirm = {
                val weight = newWeightInput.toDoubleOrNull()
                if (weight != null) {
                    viewModel.saveWeight(weight)
                }
            },
            title = stringResource(R.string.set_weight_title),
            text = stringResource(R.string.set_weight_message),
            confirmButtonText = stringResource(R.string.save),
            dismissButtonText = stringResource(R.string.cancel),
            customContent = {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newWeightInput,
                        onValueChange = { newWeightInput = it },
                        label = { Text(stringResource(R.string.weight_label_kg)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = newWeightInput.toDoubleOrNull() == null && newWeightInput.isNotEmpty()
                    )
                }
            }
        )
    }

    if (showDateRangePicker) {
        WorkoutRangeCalendar(
            viewModel = hiltViewModel(),
            onDateRangeSelected = { start, end ->
                viewModel.loadData(start, end)
                hasDateRangeSelected = true
                dateRangeText = if (start == end) {
                    start?.formatShort() ?: "Desconocido"
                } else {
                    "${start?.formatShort()} - ${end?.formatShort()}"
                }
                showDateRangePicker = false
            },
            onDismiss = { showDateRangePicker = false }
        )
    }

    if (showExportDialog) {
        CustomAlertDialog(
            showDialog = showExportDialog,
            onDismiss = { showExportDialog = false },
            onConfirm = {},
            title = stringResource(R.string.export_dialog_title),
            text = stringResource(R.string.export_dialog_message),
            confirmButtonText = "",
            dismissButtonText = "",
            customContent = {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MyElevatedButton(
                        onClick = {
                            exportFormat = "csv"
                            exportLauncher.launch("progress_${System.currentTimeMillis()}.csv")
                            showExportDialog = false
                        },
                        text = stringResource(R.string.export_csv),
                        modifier = Modifier.fillMaxWidth(),
                        textColor = Color(0xFF107c41)
                    )
                    Spacer(Modifier.height(8.dp))
                    MyElevatedButton(
                        onClick = {
                            exportFormat = "pdf"
                            exportLauncher.launch("progress_${System.currentTimeMillis()}.pdf")
                            showExportDialog = false
                        },
                        text = stringResource(R.string.export_pdf),
                        modifier = Modifier.fillMaxWidth(),
                        textColor = Color(0xFFb30b00)
                    )
                    Spacer(Modifier.height(8.dp))
                    MyElevatedButton(
                        onClick = { showExportDialog = false },
                        text = stringResource(R.string.cancel),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }
}