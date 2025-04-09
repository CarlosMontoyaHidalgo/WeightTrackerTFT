package com.aronid.weighttrackertft.ui.screens.progress.charts

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.button.BackButton
import com.aronid.weighttrackertft.ui.components.calendar.WorkoutRangeCalendar
import com.aronid.weighttrackertft.ui.components.charts.GoalViewModel
import com.aronid.weighttrackertft.ui.components.charts.aaa.CaloriesLineChartWithGridLines
import com.aronid.weighttrackertft.ui.components.charts.aaa.VolumeLineChartWithGridLines
import com.aronid.weighttrackertft.ui.components.charts.aaa.WeightLineChartWithGridLines
import com.aronid.weighttrackertft.utils.formatShort

@Composable
fun ChartsScreen(innerPadding: PaddingValues, navHostController: NavHostController) {
    val viewModel: ChartsViewModel = hiltViewModel()
    val viewModelGoals: GoalViewModel = hiltViewModel()


    val caloriesData by viewModel.caloriesData.collectAsStateWithLifecycle()
    val totalCalories by viewModel.totalCalories.collectAsStateWithLifecycle()
    val goalCalories by viewModelGoals.goalCalories.collectAsStateWithLifecycle()
    val volumeData by viewModel.volumeData.collectAsStateWithLifecycle()
    val weightData by viewModel.weightData.collectAsStateWithLifecycle()
    Log.d("ChartsScreen", "Calories data: $caloriesData")
    Log.d("ChartsScreen", "Volume data: $volumeData")
    Log.d("ChartsScreen", "Weight data: $weightData")
    val currentWeight by viewModel.currentWeight.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var showDateRangePicker by remember { mutableStateOf(false) }
    var dateRangeText by remember { mutableStateOf(viewModel.getWeekRangeText()) }
    var weightInput by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableStateOf(0) }
    var hasDateRangeSelected by remember { mutableStateOf(false) }


    val tabs = listOf("Calorías", "Volumen", "Peso")

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Tu Progreso",
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

            if (hasDateRangeSelected) {
                IconButton(onClick = {
                    //viewModel.loadData(null, null) // o carga por defecto
                    viewModel.loadCurrentWeek()
                    dateRangeText = viewModel.getWeekRangeText()
                    hasDateRangeSelected = false
                    showDateRangePicker = false
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_x), // usa uno tipo X o reemplaza por Icons.Default.Close si usás Material Icons
                        contentDescription = "Borrar rango de fechas",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            IconButton(onClick = { showDateRangePicker = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "Select date range",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (selectedTabIndex == 0) {
                IconButton(onClick = { viewModelGoals.triggerGoalDialog() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_goal),
                        contentDescription = "Select date range",
                        tint = MaterialTheme.colorScheme.primary
                    )
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
            0 -> CaloriesSection(caloriesData, totalCalories, goalCalories, isLoading)
            1 -> VolumeSection(volumeData, isLoading)
            2 -> WeightSection(
                weightData,
                currentWeight,
                isLoading,
                weightInput,
                onWeightChange = { weightInput = it },
                onSaveWeight = { viewModel.saveWeight(it) }
            )
        }
    }
    BackButton(navHostController)

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

@Composable
fun CaloriesSection(
    caloriesData: Map<String, Int>,
    totalCalories: Int?,
    goalCalories: Int?,
    isLoading: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Total Calorías
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Total Calorías",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${totalCalories ?: "Cargando..."} kcal",
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Objetivo
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Objetivo",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${goalCalories ?: "Cargando..."} kcal",
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            CaloriesLineChartWithGridLines(caloriesData = caloriesData)
        }
    }
}


@Composable
fun VolumeSection(volumeData: Map<String, Int>, isLoading: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Volumen Total",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${volumeData.values.sum()} kg",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            VolumeLineChartWithGridLines(volumeData = volumeData)
        }
    }
}

@Composable
fun WeightSection(
    weightData: Map<String, Double>,
    currentWeight: Double?,
    isLoading: Boolean,
    weightInput: String,
    onWeightChange: (String) -> Unit,
    onSaveWeight: (Double) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Peso Actual",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${currentWeight?.formatToSinglePrecision() ?: "N/A"} kg",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp),
                    color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextField(
                        value = weightInput,
                        onValueChange = onWeightChange,
                        label = { Text("Nuevo peso (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(150.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            weightInput.toDoubleOrNull()?.let { weight ->
                                onSaveWeight(weight)
                                onWeightChange("") // Limpiar el campo tras guardar
                            }
                        },
                        enabled = weightInput.isNotBlank() && weightInput.toDoubleOrNull() != null
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            WeightLineChartWithGridLines(weightData = weightData)
        }
    }
}

fun Double.formatToSinglePrecision(): String {
    return String.format("%.1f", this)
}
