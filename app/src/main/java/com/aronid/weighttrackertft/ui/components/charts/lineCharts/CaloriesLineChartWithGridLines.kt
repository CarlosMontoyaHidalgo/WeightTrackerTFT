package com.aronid.weighttrackertft.ui.components.charts.lineCharts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.aronid.weighttrackertft.ui.components.charts.GoalViewModel


@Composable
fun CaloriesLineChartWithGridLines(
    caloriesData: Map<String, Int>,
    viewModel: GoalViewModel = hiltViewModel()
) {
    val goalCalories by viewModel.goalCalories.collectAsState()
    val showGoalDialog by viewModel.showGoalDialog.collectAsState()

    if (caloriesData.values.all { it == 0 }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay datos de calorías",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        return
    }

    val pointsData = caloriesData.entries.mapIndexed { index, entry ->
        Point(index.toFloat(), entry.value.toFloat(), entry.key) // Seguimos usando Float para el gráfico
    }
    val maxY = pointsData.maxOfOrNull { it.y }?.toFloat() ?: 100f
    val steps = 5

    val xAxisData = AxisData.Builder()
        .axisStepSize(50.dp)
        .steps(pointsData.size - 1)
        .labelData { index -> pointsData.getOrNull(index)?.description ?: "" }
        .labelAndAxisLinePadding(15.dp)
        .axisLineColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .axisLabelColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(steps)
        .labelData { index -> (maxY * index / steps).toInt().toString() }
        .labelAndAxisLinePadding(15.dp)
        .axisLineColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .axisLabelColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .axisLabelDescription { "cal" }
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    lineStyle = LineStyle(
                        lineType = LineType.SmoothCurve(isDotted = false),
                        color = MaterialTheme.colorScheme.primary,
                        width = 10f
                    ),
                    intersectionPoint = IntersectionPoint(
                        color = MaterialTheme.colorScheme.primary,
                        radius = 10.dp
                    ),
                    selectionHighlightPoint = SelectionHighlightPoint(
                        color = MaterialTheme.colorScheme.secondary,
                        radius = 6.dp
                    ),
                    shadowUnderLine = ShadowUnderLine(
                        alpha = 0.2f,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    ),
                    selectionHighlightPopUp = SelectionHighlightPopUp(
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        backgroundAlpha = 0.9f,
                        labelSize = 12.sp,
                        labelColor = MaterialTheme.colorScheme.onSurface,
                        popUpLabel = { x, y ->
                            val dayLabel = pointsData.getOrNull(x.toInt())?.description ?: "Día ${x.toInt() + 1}"
                            val goalText = goalCalories?.let { " (Meta: $it cal)" } ?: ""
                            "$dayLabel: ${y.toInt()} Cal$goalText"
                        }
                    )
                )
            )
        ),
        gridLines = GridLines(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineWidth = 0.5.dp
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = MaterialTheme.colorScheme.surface
    )

    Column {

        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .shadow(4.dp, RoundedCornerShape(8.dp)),
            lineChartData = lineChartData
        )

        val average = pointsData.map { it.y }.average().toInt()
        val max = pointsData.maxOf { it.y }.toInt()
        val min = pointsData.minOf { it.y }.toInt()
        Text(
            text = "Resumen: Promedio: $average cal | Máx: $max cal | Mín: $min cal",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp)
        )
        goalCalories?.let { goal ->
            val suggestion = if (average > goal) {
                "Mañana intenta mantenerte bajo $goal cal."
            } else {
                "¡Sigue con este ritmo mañana!"
            }
            Text(
                text = suggestion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }

    if (showGoalDialog) {
        var inputText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { viewModel.dismissGoalDialog() },
            title = { Text("Establece tu objetivo") },
            text = {
                Column {
                    Text("¿Cuál es tu objetivo diario de calorías?")
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Calorías") },
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val goal = inputText.toIntOrNull()
                        if (goal != null && goal > 0) {
                            viewModel.setGoalCalories(goal)
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissGoalDialog() }) {
                    Text("Cancelar")
                }
            }
        )
    }
}