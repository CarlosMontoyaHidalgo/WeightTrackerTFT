package com.aronid.weighttrackertft.ui.screens.example

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.ui.components.charts.aaa.BarChartsExamples
import com.aronid.weighttrackertft.ui.components.charts.aaa.BubbleChartExamples
import com.aronid.weighttrackertft.ui.components.charts.aaa.DonutChartExample
import com.aronid.weighttrackertft.ui.components.charts.aaa.LineAndBarChartExample
import com.aronid.weighttrackertft.ui.components.charts.aaa.LineChartExample
import com.aronid.weighttrackertft.ui.components.charts.aaa.PieChartExample
import com.aronid.weighttrackertft.ui.components.charts.aaa.TrainingRadarChartExample
import com.aronid.weighttrackertft.ui.components.charts.aaa.WaveChartExample

@Composable
fun ExampleScreen(innerPadding: PaddingValues, navHostController: NavHostController) {
    // Lista de nombres de pestañas
    val tabs = listOf(
        "Bar Charts",
        "Bubble Charts",
        "Line & Bar",
        "Donut Chart",
        "Line Chart",
        "Pie Chart",
        "Wave Chart",
        "Training Radar Chart"
    )

    // Estado para rastrear la pestaña seleccionada
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        // TabRow para las pestañas
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        // Contenido dinámico basado en la pestaña seleccionada
        when (selectedTab) {
            0 -> BarChartsExamples()
            1 -> BubbleChartExamples()
            2 -> LineAndBarChartExample()
            3 -> DonutChartExample()
            4 -> LineChartExample()
            5 -> PieChartExample()
            6 -> WaveChartExample()
            7 -> TrainingRadarChartExample()
        }
    }
}