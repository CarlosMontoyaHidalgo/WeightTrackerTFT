package com.aronid.weighttrackertft.ui.screens.progress.charts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.alertDialog.CustomAlertDialog
import com.aronid.weighttrackertft.ui.components.calendar.WorkoutRangeCalendar
import com.aronid.weighttrackertft.ui.components.charts.GoalViewModel
import com.aronid.weighttrackertft.ui.components.sections.calories.CaloriesSection
import com.aronid.weighttrackertft.ui.components.sections.imc.BMISection
import com.aronid.weighttrackertft.ui.components.sections.volume.VolumeSection
import com.aronid.weighttrackertft.ui.components.sections.weight.WeightSection
import com.aronid.weighttrackertft.utils.formatShort

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
    var weightInput by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableStateOf(0) }
    var hasDateRangeSelected by remember { mutableStateOf(false) }
    var goalInput by remember { mutableStateOf("") }

    var heightInput by remember { mutableStateOf("") }
    var bmiResult by remember { mutableStateOf<Double?>(null) }

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
                            contentDescription = stringResource(R.string.select_date_range),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                if (selectedTabIndex == 2) {
                    IconButton(onClick = { viewModelGoals.triggerWeightDialog() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_weight),
                            contentDescription = stringResource(R.string.select_date_range),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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

        when (selectedTabIndex) {
            0 -> CaloriesSection(caloriesData, totalCalories, goalCalories, averageCalories ,isLoading)
            1 -> VolumeSection(volumeData, isLoading)
            2 -> WeightSection(
                weightData,
                currentWeight,
                isLoading,
                weightInput,
                onWeightChange = { weightInput = it },
                onSaveWeight = { viewModel.saveWeight(it) }
            )
            3 -> BMISection(
                currentWeight = currentWeight,
                currentHeight = currentHeight,
            )


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
                    viewModel.saveWeight(weight) // Use ChartsViewModel's saveWeight
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
}





