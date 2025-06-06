package com.aronid.weighttrackertft.ui.components.charts.lineCharts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
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
import com.aronid.weighttrackertft.utils.formatToSinglePrecision
import androidx.compose.runtime.getValue

@Composable
fun CombinedWeightCaloriesChart(
    weightData: Map<String, Double>,
    caloriesData: Map<String, Int>,
    isLoading: Boolean = false
) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        item {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Filter valid data
                val validWeightDays = weightData.filterValues { it > 0 }
                val validCaloriesDays = caloriesData.filterValues { it > 0 }

                // Combine and sort all days
                val allDays = (validWeightDays.keys + validCaloriesDays.keys).distinct().sorted()

                if (allDays.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(16.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay datos suficientes",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                } else {
                    // Stat Cards
                    StatCards(
                        validWeightDays = validWeightDays,
                        validCaloriesDays = validCaloriesDays
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Line Chart
                    WeightCaloriesLineChart(
                        validWeightDays = validWeightDays,
                        validCaloriesDays = validCaloriesDays,
                        allDays = allDays
                    )
                }
            }
        }
    }
}

@Composable
fun StatCards(
    validWeightDays: Map<String, Double>,
    validCaloriesDays: Map<String, Int>
) {
    // Calculate metrics
    val currentWeight = validWeightDays.entries.maxByOrNull { it.key }?.value
    val averageWeight = if (validWeightDays.size > 1) {
        validWeightDays.values.average().formatToSinglePrecision()
    } else {
        null
    }
    val totalCalories = validCaloriesDays.values.sum()
    val averageCalories = if (validCaloriesDays.size > 1) {
        validCaloriesDays.values.average().toInt()
    } else {
        null
    }

    // First Row: Current Weight, Average Weight
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        // Average Weight
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
                    text = "Peso promedio",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = averageWeight?.let { "$it kg" } ?: "No disponible",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                    color = MaterialTheme.colorScheme.primary
                )
            }


        }

        // Average Calories
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
                    text = "Calorías promedio",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = averageCalories?.let { "$it kcal" } ?: "No disponible",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }


}

@Composable
fun WeightCaloriesLineChart(
    validWeightDays: Map<String, Double>,
    validCaloriesDays: Map<String, Int>,
    allDays: List<String>
) {
    // Prepare data points
    val weightPoints = allDays.mapIndexed { index, day ->
        val weightValue = validWeightDays[day] ?: 0.0
        Point(index.toFloat(), weightValue.toFloat())
    }
    val caloriesPoints = allDays.mapIndexed { index, day ->
        val caloriesValue = validCaloriesDays[day]?.toFloat() ?: 0f
        Point(index.toFloat(), caloriesValue)
    }

    // Store labels
    val weightLabels = allDays.map { day ->
        val weightValue = validWeightDays[day] ?: 0.0
        if (weightValue > 0) "${weightValue.formatToSinglePrecision()} kg" else ""
    }
    val caloriesLabels = allDays.map { day ->
        val caloriesValue = validCaloriesDays[day] ?: 0
        if (caloriesValue > 0) "$caloriesValue kcal" else ""
    }

    // Calculate max values
    val maxWeight = validWeightDays.values.maxOrNull() ?: 123.0
    val minWeight = validWeightDays.values.minOrNull() ?: 50.0
    val yAxisMaxWeight = (maxWeight * 1.1).coerceAtLeast(minWeight + 10.0) // 10% buffer
    val maxCalories = validCaloriesDays.values.maxOrNull()?.toDouble() ?: 3000.0
    val yAxisMaxCalories = maxCalories * 1.1 // 10% buffer

    // X Axis
    val xAxisData = AxisData.Builder()
        .steps(allDays.size - 1)
        .labelData { index ->
            allDays.getOrNull(index)?.let { day ->
                if (day.startsWith("Día ")) day.substringAfter("Día ") else day
            } ?: ""
        }
        .axisStepSize(60.dp)
        .labelAndAxisLinePadding(16.dp)
        .axisLabelColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .axisLineColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .axisLabelFontSize(14.sp)
        .build()

    // Left Y Axis (Weight)
    val yAxisLeftData = AxisData.Builder()
        .steps(5)
        .labelData { index ->
            val value =
                (minWeight + (yAxisMaxWeight - minWeight) * index / 5).formatToSinglePrecision()
            "$value kg"
        }
        .labelAndAxisLinePadding(16.dp)
        .axisLabelColor(Color(0xFF1976D2))
        .axisLineColor(Color(0xFF1976D2).copy(alpha = 0.5f))
        .axisLabelFontSize(14.sp)
        .build()

    // Right Y Axis (Calories)
    val yAxisRightData = AxisData.Builder()
        .steps(5)
        .labelData { index ->
            val value = (yAxisMaxCalories * index / 5).toInt()
            "$value kcal"
        }
        .labelAndAxisLinePadding(16.dp)
        .axisLabelColor(Color(0xFFE64A19))
        .axisLineColor(Color(0xFFE64A19).copy(alpha = 0.5f))
        .axisLabelFontSize(14.sp)
        .build()

    // Line Plot Data
    val weightLine = Line(
        dataPoints = weightPoints,
        lineStyle = LineStyle(
            color = Color(0xFF1976D2),
        ),
        intersectionPoint = IntersectionPoint(
            color = Color(0xFF1976D2),
            radius = 4.dp
        )
    )
    val caloriesLine = Line(
        dataPoints = caloriesPoints,
        lineStyle = LineStyle(
            color = Color(0xFFE64A19),
        ),
        intersectionPoint = IntersectionPoint(
            color = Color(0xFFE64A19),
            radius = 4.dp
        )
    )

    val linePlotData = LinePlotData(
        lines = listOf(weightLine, caloriesLine)
    )

    // Line Chart Data
    val lineChartData = LineChartData(
        linePlotData = linePlotData,
        xAxisData = xAxisData,
        yAxisData = yAxisLeftData,
        gridLines = GridLines(
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
            lineWidth = 1.dp
        )
    )

    // Chart with Custom Labels
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Legend
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF1976D2))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Peso (kg)",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFFE64A19))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Calorías (kcal)",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Box {
                LineChart(
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)),
                    lineChartData = lineChartData
                )
                Canvas(
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth()
                        .matchParentSize()
                ) {
                    drawCustomLabels(
                        weightPoints = weightPoints,
                        caloriesPoints = caloriesPoints,
                        weightLabels = weightLabels,
                        caloriesLabels = caloriesLabels,
                        chartWidth = size.width,
                        chartHeight = size.height,
                        maxWeight = yAxisMaxWeight.toFloat(),
                        maxCalories = yAxisMaxCalories.toFloat()
                    )
                }
            }
        }
    }
}

fun DrawScope.drawCustomLabels(
    weightPoints: List<Point>,
    caloriesPoints: List<Point>,
    weightLabels: List<String>,
    caloriesLabels: List<String>,
    chartWidth: Float,
    chartHeight: Float,
    maxWeight: Float,
    maxCalories: Float
) {
    val totalPoints = weightPoints.size
    val pointSpacingPx = chartWidth / (totalPoints + 1)

    weightPoints.forEachIndexed { index, point ->
        val x = pointSpacingPx * (index + 1)
        val yValue = point.y
        val y = chartHeight * (1 - (yValue / maxWeight)) - 24.dp.toPx()
        val label = weightLabels[index]
        if (label.isNotEmpty()) {
            drawIntoCanvas { canvas ->
                val paint = Paint().asFrameworkPaint().apply {
                    color = Color(0xFF1976D2).toArgb()
                    textSize = 12.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = android.graphics.Typeface.create(
                        android.graphics.Typeface.DEFAULT,
                        android.graphics.Typeface.BOLD
                    )
                }
                canvas.nativeCanvas.drawText(label, x, y, paint)
            }
        }
    }

    caloriesPoints.forEachIndexed { index, point ->
        val x = pointSpacingPx * (index + 1)
        val yValue = point.y
        val y =
            chartHeight * (1 - (yValue / maxCalories)) - 12.dp.toPx() // Slightly offset to avoid overlap
        val label = caloriesLabels[index]
        if (label.isNotEmpty()) {
            drawIntoCanvas { canvas ->
                val paint = Paint().asFrameworkPaint().apply {
                    color = Color(0xFFE64A19).toArgb()
                    textSize = 12.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = android.graphics.Typeface.create(
                        android.graphics.Typeface.DEFAULT,
                        android.graphics.Typeface.BOLD
                    )
                }
                canvas.nativeCanvas.drawText(label, x, y, paint)
            }
        }
    }
}


@Composable
fun CombinedWeightCaloriesWeeklyChart(
    weightData: Map<String, Double>,
    caloriesData: Map<String, Int>,
    isLoading: Boolean = false,
    viewModel: GoalViewModel = hiltViewModel()
) {
    val goalCalories by viewModel.goalCalories.collectAsState()

    // Process data into weekly averages
    val weeklyWeight = remember(weightData) {
        runCatching {
            aggregateWeightByWeekAverage(weightData)
        }.getOrElse { emptyMap() }
    }

    val weeklyCalories = remember(caloriesData) {
        runCatching {
            aggregateCaloriesByWeekAverageSimple(caloriesData)
        }.getOrElse { emptyMap() }
    }

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        item {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Combine all weeks
                val allWeeks = (weeklyWeight.keys + weeklyCalories.keys).distinct().sortedBy {
                    it.removePrefix("Semana ").toIntOrNull() ?: 0
                }

                if (allWeeks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(16.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay datos suficientes",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                } else {
                    // Stat Cards with weekly data
                    WeeklyStatCards(
                        weeklyWeight = weeklyWeight,
                        weeklyCalories = weeklyCalories
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Weekly Line Chart
                    WeeklyWeightCaloriesLineChart(
                        weeklyWeight = weeklyWeight,
                        weeklyCalories = weeklyCalories,
                        allWeeks = allWeeks,
                        goalCalories = goalCalories
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyStatCards(
    weeklyWeight: Map<String, Double>,
    weeklyCalories: Map<String, Int>
) {
    // Calculate weekly metrics
    val currentWeight = weeklyWeight.entries.maxByOrNull { it.key }?.value
    val averageWeight = if (weeklyWeight.size > 1) {
        weeklyWeight.values.average().formatToSinglePrecision()
    } else {
        null
    }
    val totalCalories = weeklyCalories.values.sum()
    val averageCalories = if (weeklyCalories.size > 1) {
        weeklyCalories.values.average().toInt()
    } else {
        null
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Average Weight
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
                    text = "Peso semanal",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = averageWeight?.let { "$it kg" } ?: "No disponible",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Average Calories
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
                    text = "Calorías semanales",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = averageCalories?.let { "$it kcal" } ?: "No disponible",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun WeeklyWeightCaloriesLineChart(
    weeklyWeight: Map<String, Double>,
    weeklyCalories: Map<String, Int>,
    allWeeks: List<String>,
    goalCalories: Int?
) {
    // Prepare data points
    val weightPoints = allWeeks.mapIndexed { index, week ->
        val weightValue = weeklyWeight[week] ?: 0.0
        Point(index.toFloat(), weightValue.toFloat())
    }

    val caloriesPoints = allWeeks.mapIndexed { index, week ->
        val caloriesValue = weeklyCalories[week]?.toFloat() ?: 0f
        Point(index.toFloat(), caloriesValue)
    }

    // Calculate max values with buffer
    val maxWeight = weeklyWeight.values.maxOrNull() ?: 100.0
    val minWeight = weeklyWeight.values.minOrNull() ?: 50.0
    val yAxisMaxWeight = (maxWeight * 1.1).coerceAtLeast(minWeight + 5.0)
    val maxCalories = weeklyCalories.values.maxOrNull()?.toDouble() ?: 3000.0
    val yAxisMaxCalories = (maxCalories * 1.2).coerceAtLeast(goalCalories?.toDouble() ?: 0.0 * 1.1)

    // X Axis configuration (show every 4 weeks)
    val xAxisData = AxisData.Builder()
        .axisStepSize(20.dp)
        .steps(allWeeks.size - 1)
        .labelData { index ->
            val weekNum = index + 1
            when {
                weekNum % 4 == 0 -> "S$weekNum" // Show every 4 weeks
                else -> ""
            }
        }
        .axisLabelAngle(60f)
        .labelAndAxisLinePadding(4.dp)
        .axisLineColor(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
        .axisLabelColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .build()

    // Left Y Axis (Weight)
    val yAxisLeftData = AxisData.Builder()
        .steps(5)
        .labelData { index ->
            val value = (minWeight + (yAxisMaxWeight - minWeight) * index / 5).formatToSinglePrecision()
            "$value kg"
        }
        .labelAndAxisLinePadding(8.dp)
        .axisLineColor(Color(0xFF1976D2).copy(alpha = 0.5f))
        .axisLabelColor(Color(0xFF1976D2))
        .build()

    // Right Y Axis (Calories)
    val yAxisRightData = AxisData.Builder()
        .steps(5)
        .labelData { index ->
            "${(yAxisMaxCalories * index / 5).toInt()} kcal"
        }
        .labelAndAxisLinePadding(8.dp)
        .axisLineColor(Color(0xFFE64A19).copy(alpha = 0.5f))
        .axisLabelColor(Color(0xFFE64A19))
        .build()

    // Line configuration
    val linePlotData = LinePlotData(
        lines = listOf(
            Line(
                dataPoints = weightPoints,
                lineStyle = LineStyle(
                    lineType = LineType.SmoothCurve(false),
                    color = Color(0xFF1976D2),
                    width = 4f
                ),
                intersectionPoint = null,
                selectionHighlightPoint = SelectionHighlightPoint(
                    color = Color(0xFF1976D2),
                    radius = 6.dp
                ),
                shadowUnderLine = ShadowUnderLine(
                    alpha = 0.15f,
                    color = Color(0xFF1976D2).copy(alpha = 0.2f),
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1976D2).copy(alpha = 0.3f),
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
                        val week = allWeeks.getOrNull(x.toInt()) ?: "Semana ${x.toInt() + 1}"
                        "$week: ${y.toDouble().formatToSinglePrecision()} kg"
                    }
                )
            ),
            Line(
                dataPoints = caloriesPoints,
                lineStyle = LineStyle(
                    lineType = LineType.SmoothCurve(false),
                    color = Color(0xFFE64A19),
                    width = 4f
                ),
                intersectionPoint = null,
                selectionHighlightPoint = SelectionHighlightPoint(
                    color = Color(0xFFE64A19),
                    radius = 6.dp
                ),
                shadowUnderLine = ShadowUnderLine(
                    alpha = 0.15f,
                    color = Color(0xFFE64A19).copy(alpha = 0.2f),
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE64A19).copy(alpha = 0.3f),
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
                        val week = allWeeks.getOrNull(x.toInt()) ?: "Semana ${x.toInt() + 1}"
                        val goalText = goalCalories?.let { " (Meta: $it kcal)" } ?: ""
                        "$week: ${y.toInt()} kcal$goalText"
                    }
                )
            )
        )
    )

    val lineChartData = LineChartData(
        linePlotData = linePlotData,
        xAxisData = xAxisData,
        yAxisData = yAxisLeftData,
        gridLines = GridLines(
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
            lineWidth = 1.dp
        ),
        backgroundColor = MaterialTheme.colorScheme.surface,
        paddingTop = 12.dp,
        paddingRight = 0.dp,
    )

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
            Column {
                // Legend
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF1976D2))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Peso (kg)",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFFE64A19))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Calorías (kcal)",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Chart
                LineChart(
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth(),
                    lineChartData = lineChartData
                )
            }
        }
    }
}

// Helper functions for weekly aggregation
private fun aggregateWeightByWeekAverage(weightData: Map<String, Double>): Map<String, Double> {
    val weeklySums = mutableMapOf<Int, MutableList<Double>>().apply {
        for (week in 1..52) this[week] = mutableListOf()
    }

    weightData.forEach { (dayStr, weight) ->
        val dayNumber = dayStr.removePrefix("Día ").toIntOrNull() ?: return@forEach
        if (dayNumber in 1..366) {
            val weekNumber = ((dayNumber - 1) / 7) + 1
            weeklySums[weekNumber]?.add(weight)
        }
    }

    return weeklySums.mapValues { (_, values) ->
        if (values.isEmpty()) 0.0 else values.average()
    }.mapKeys { (week, _) -> "Semana $week" }
}

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