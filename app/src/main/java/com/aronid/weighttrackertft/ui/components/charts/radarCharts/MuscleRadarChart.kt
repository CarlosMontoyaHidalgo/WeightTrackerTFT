package com.aronid.weighttrackertft.ui.components.charts.radarCharts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.ui.components.charts.examples.RadarChart
import com.aronid.weighttrackertft.ui.components.charts.examples.RadarData


@Composable
fun MuscleRadarChart(muscles: List<Pair<String, Float>>) {
    val labels = muscles.map { it.first }
    val values = muscles.map { it.second }
    val adjustedLabels = if (labels.size < 3) labels + List(3 - labels.size) { "Otros" } else labels
    val adjustedValues = if (values.size < 3) values + List(3 - values.size) { 0f } else values

    RadarChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .padding(16.dp),
        data = RadarData(
            labels = adjustedLabels,
            values = adjustedValues,
            maxValue = 100f
        ),
        gridColor = Color.LightGray,
        dataColor = Color.Green,
        fillColor = Color.Green.copy(alpha = 0.2f),
        labelColor = Color.DarkGray,
        labelDistanceFactor = 1.3f
    )
}