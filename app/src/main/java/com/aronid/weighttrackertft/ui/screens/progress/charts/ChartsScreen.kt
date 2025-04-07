package com.aronid.weighttrackertft.ui.screens.progress.charts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.button.BackButton
import com.aronid.weighttrackertft.ui.components.calendar.WorkoutRangeCalendar
import com.aronid.weighttrackertft.ui.components.charts.aaa.CaloriesLineChartWithGridLines
import com.aronid.weighttrackertft.utils.formatSpanish
import com.google.firebase.Timestamp
import java.time.LocalDate

@Composable
fun ChartsScreen(innerPadding: PaddingValues, navHostController: NavHostController) {
    val viewModel: ChartsViewModel = hiltViewModel()

    val caloriesData by viewModel.caloriesData.collectAsStateWithLifecycle()
    val totalCalories by viewModel.totalCalories.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var showDateRangePicker by remember { mutableStateOf(false) }
    var dateRangeText by remember { mutableStateOf(viewModel.getWeekRangeText()) }

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
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
            IconButton(onClick = { showDateRangePicker = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "Select date range",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    CaloriesLineChartWithGridLines(caloriesData = caloriesData)
                }
            }
        }
    }
    BackButton(navHostController)

    if (showDateRangePicker) {
        WorkoutRangeCalendar(
            viewModel = hiltViewModel(),
            onDateRangeSelected = { start, end ->
                viewModel.loadCalories(start, end)
                dateRangeText = if (start == end) {
                    start?.formatSpanish() ?: "Unknown"
                } else {
                    "${start?.formatSpanish()} - ${end?.formatSpanish()}"
                }
                showDateRangePicker = false
            },
            onDismiss = { showDateRangePicker = false }
        )
    }
}