package com.aronid.weighttrackertft.ui.components.charts.barCharts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import co.yml.charts.ui.barchart.models.SelectionHighlightData
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
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
    viewModel: GoalViewModel = hiltViewModel(),
    rangeType: String
) {
    // 1. Procesamiento de datos
    val weeklyCalories = remember(caloriesData) {
        runCatching {
            aggregateCaloriesByWeekAverageSimple(caloriesData)
        }.getOrElse { emptyMap() }
    }

    val goalCalories by viewModel.goalCalories.collectAsState()
    val showGoalDialog by viewModel.showGoalDialog.collectAsState()

    // 2. Manejo de estado sin datos
    if (weeklyCalories.values.all { it == 0 }) {
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

    // 3. Preparación de puntos para el gráfico
    val pointsData = remember(weeklyCalories) {
        (1..52).map { week ->
            Point(
                x = (week - 1).toFloat(),
                y = weeklyCalories["Semana $week"]?.toFloat() ?: 0f,
                description = "Semana $week"
            )
        }
    }

    val maxY = (pointsData.maxOfOrNull { it.y } ?: 100f) * 1.2f // 20% más de espacio
    val ySteps = 5

    // 4. Configuración del eje X (mostrar todas las semanas)
    val xAxisData = AxisData.Builder()
        .axisStepSize(20.dp) // Más compacto para 52 semanas
        .steps(pointsData.size - 1)
        .labelData { index ->
            val weekNum = index + 1
            when {
                weekNum % 4 == 0 -> "S$weekNum" // Mostrar cada 4 semanas (S4, S8...)
                else -> "" // Espacios intermedios vacíos
            }
        }
        .axisLabelAngle(60f) // Rotación para mejor ajuste
        .labelAndAxisLinePadding(4.dp)
        .axisLineColor(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
        .axisLabelColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .build()

    // 5. Configuración del eje Y
    val yAxisData = AxisData.Builder()
        .steps(ySteps)
        .labelData { index -> "${(maxY * index / ySteps).toInt()}" }
        .labelAndAxisLinePadding(8.dp)
        .axisLineColor(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
        .axisLabelColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .build()

    // 6. Configuración del gráfico
    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    lineStyle = LineStyle(
                        lineType = LineType.SmoothCurve(false),
                        color = MaterialTheme.colorScheme.primary,
                        width = 4f
                    ),
                    intersectionPoint = null, // Sin puntos de intersección
                    selectionHighlightPoint = SelectionHighlightPoint(
                        color = MaterialTheme.colorScheme.secondary,
                        radius = 6.dp
                    ),
                    shadowUnderLine = ShadowUnderLine(
                        alpha = 0.15f,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    ),
                    selectionHighlightPopUp = SelectionHighlightPopUp(
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        backgroundAlpha = 0.95f,
                        labelSize = 12.sp,
                        labelColor = MaterialTheme.colorScheme.onSurface,
                        popUpLabel = { x, y ->
                            val week = x.toInt() + 1
                            val goalText = goalCalories?.let { " (Meta: $it kcal)" } ?: ""
                            "Semana $week: ${y.toInt()} kcal$goalText"
                        }
                    )
                )
            )
        ),
        gridLines = GridLines(
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
            lineWidth = 1.dp
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = MaterialTheme.colorScheme.surface,
        paddingTop = 12.dp,
        paddingRight = 0.dp,
    )

    // 7. UI del gráfico
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            LineChart(
                modifier = Modifier.fillMaxSize(),
                lineChartData = lineChartData
            )
        }
    }
}

// Función para agregar datos por semana (sin cambios)
private fun aggregateCaloriesByWeekAverageSimple(caloriesData: Map<String, Int>): Map<String, Int> {
    val weeklySums = mutableMapOf<Int, MutableList<Int>>().apply {
        for (week in 1..52) this[week] = mutableListOf()
    }

    caloriesData.forEach { (dayStr, calories) ->
        val dayNumber = dayStr.removePrefix("Día ").toIntOrNull() ?: return@forEach
        if (dayNumber in 1..366) {
            val weekNumber = ((dayNumber - 1) / 7) + 1
            weeklySums[weekNumber]?.add(calories)
        }
    }

    return weeklySums.mapValues { (_, values) ->
        if (values.isEmpty()) 0 else values.average().toInt()
    }.mapKeys { (week, _) -> "Semana $week" }
}

@Composable
fun CaloriesBarChartWithGridLines(
    caloriesData: Map<String, Int>,
    viewModel: GoalViewModel = hiltViewModel()
) {
    val goalCalories by viewModel.goalCalories.collectAsState()
    val showGoalDialog by viewModel.showGoalDialog.collectAsState()

    // Handle empty data case
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

    // Map data to BarChart-compatible format
    val barData = caloriesData.entries.mapIndexed { index, entry ->
        BarData(
            point = Point(index.toFloat(), entry.value.toFloat(), entry.key),
            color = MaterialTheme.colorScheme.primary,
            label = entry.key
        )
    }
    val maxY = barData.maxOfOrNull { it.point.y }?.toFloat() ?: 100f
    val steps = 5

    // X-axis configuration
    val xAxisData = AxisData.Builder()
        .axisStepSize(50.dp)
        .steps(barData.size - 1)
        .labelData { index -> barData.getOrNull(index)?.label ?: "" }
        .labelAndAxisLinePadding(15.dp)
        .axisLineColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .axisLabelColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .build()

    // Y-axis configuration
    val yAxisData = AxisData.Builder()
        .steps(steps)
        .labelData { index ->
            val value = (maxY * index / steps).toInt()
            "$value kcal"
        }
        .labelAndAxisLinePadding(15.dp)
        .axisLineColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .axisLabelColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .axisLabelDescription { "kcal" }
        .build()

    // Bar chart data configuration
    val barChartData = BarChartData(
        chartData = barData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = MaterialTheme.colorScheme.surface,
        barStyle = BarStyle(
            barWidth = 30.dp,
            selectionHighlightData = SelectionHighlightData(
                highlightBarColor = MaterialTheme.colorScheme.secondary,
                popUpLabel = { x, y ->
                    val dayLabel = barData.getOrNull(x.toInt())?.label ?: "Día ${x.toInt() + 1}"
                    val goalText = goalCalories?.let { " (Meta: $it kcal)" } ?: ""
                    "$dayLabel: ${y.toInt()} kcal$goalText"
                },
            )
        )
    )

    Column {
        // Bar chart
        BarChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .shadow(4.dp, RoundedCornerShape(8.dp)),
            barChartData = barChartData
        )

        // Summary statistics
        val average = barData.map { it.point.y }.average().toInt()
        val max = barData.maxOf { it.point.y }.toInt()
        val min = barData.minOf { it.point.y }.toInt()
        Text(
            text = "Resumen: Promedio: $average kcal | Máx: $max kcal | Mín: $min kcal",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp)
        )
        goalCalories?.let { goal ->
            val suggestion = if (average > goal) {
                "Mañana intenta mantenerte bajo $goal kcal."
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

    // Goal setting dialog
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
                            viewModel.dismissGoalDialog()
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


/*
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
        Point(index.toFloat(), entry.value.toFloat(), entry.key)
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
        .labelData { index ->
            val value = (maxY * index / steps).toInt()
            "$value kcal"
        }
        .labelAndAxisLinePadding(15.dp)
        .axisLineColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .axisLabelColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .axisLabelDescription { "kcal" }
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
                            val goalText = goalCalories?.let { " (Meta: $it kcal)" } ?: ""
                            "$dayLabel: ${y.toInt()} kcal$goalText"
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
            text = "Resumen: Promedio: $average kcal | Máx: $max kcal | Mín: $min kcal",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp)
        )
        goalCalories?.let { goal ->
            val suggestion = if (average > goal) {
                "Mañana intenta mantenerte bajo $goal kcal."
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
                            viewModel.dismissGoalDialog()
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
*/