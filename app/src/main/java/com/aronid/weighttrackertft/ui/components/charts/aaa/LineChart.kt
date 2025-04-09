package com.aronid.weighttrackertft.ui.components.charts.aaa


import android.graphics.Typeface
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import co.yml.charts.axis.AxisData
import co.yml.charts.common.components.Legends
import co.yml.charts.common.extensions.formatToSinglePrecision
import co.yml.charts.common.model.LegendsConfig
import co.yml.charts.common.model.Point
import co.yml.charts.common.utils.DataUtils
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

@Composable
fun VolumeLineChartWithGridLines(volumeData: Map<String, Int>) {
    if (volumeData.values.all { it == 0 }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay datos de volumen",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        return
    }

    val pointsData = volumeData.entries.mapIndexed { index, entry ->
        Point(index.toFloat(), entry.value.toFloat(), entry.key)
    }
    val maxY = pointsData.maxOfOrNull { it.y }?.toFloat() ?: 100f
    val steps = 5

    val xAxisData = AxisData.Builder()
        .axisStepSize(30.dp)
        .steps(pointsData.size - 1)
        .labelData { index -> pointsData.getOrNull(index)?.description ?: "" }
        .labelAndAxisLinePadding(10.dp)
        .axisLineColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .axisLabelColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(steps)
        .labelData { index -> (maxY * index / steps).toInt().toString() }
        .labelAndAxisLinePadding(10.dp)
        .axisLineColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .axisLabelColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    lineStyle = LineStyle(
                        lineType = LineType.SmoothCurve(isDotted = false),
                        color = MaterialTheme.colorScheme.secondary
                    ),
                    intersectionPoint = IntersectionPoint(),
                    selectionHighlightPoint = SelectionHighlightPoint(),
                    shadowUnderLine = ShadowUnderLine(),
                    selectionHighlightPopUp = SelectionHighlightPopUp()
                )
            )
        ),
        gridLines = GridLines(),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = MaterialTheme.colorScheme.surface
    )

    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        lineChartData = lineChartData
    )
}

@Composable
fun WeightLineChartWithGridLines(weightData: Map<String, Double>) {
    val filteredWeightData = weightData.filterValues { it > 0.0 }

    // Mostrar mensaje si hay menos de 2 datos válidos
    if (filteredWeightData.size < 2) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay suficientes datos de peso",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        return
    }

    val pointsData = filteredWeightData.entries.mapIndexed { index, entry ->
        Point(index.toFloat(), entry.value.toFloat(), entry.key)
    }

    val maxY = pointsData.maxOfOrNull { it.y } ?: 100f
    val minY = pointsData.minOfOrNull { it.y } ?: 0f
    val steps = 5
    val range = maxY - minY

    val xAxisData = AxisData.Builder()
        .axisStepSize(30.dp)
        .steps(pointsData.size - 1)
        .labelData { index -> pointsData.getOrNull(index)?.description ?: "" }
        .labelAndAxisLinePadding(10.dp)
        .axisLineColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .axisLabelColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(steps)
        .labelData { index -> (minY + (range * index / steps)).formatToSinglePrecision() }
        .labelAndAxisLinePadding(10.dp)
        .axisLineColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .axisLabelColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    lineStyle = LineStyle(
                        lineType = LineType.SmoothCurve(isDotted = false),
                        color = MaterialTheme.colorScheme.tertiary
                    ),
                    intersectionPoint = IntersectionPoint(),
                    selectionHighlightPoint = SelectionHighlightPoint(),
                    shadowUnderLine = ShadowUnderLine(),
                    selectionHighlightPopUp = SelectionHighlightPopUp()
                )
            )
        ),
        gridLines = GridLines(),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = MaterialTheme.colorScheme.surface
    )

    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        lineChartData = lineChartData
    )
}


@Composable
fun LineChartExample() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                modifier = Modifier.padding(12.dp),
                text = "Line Chart Default Style",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            SingleLineChartWithGridLines(
                DataUtils.getLineChartData(
                    listSize = 100,
                    start = 50,
                    maxRange = 100
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            )
        }
        item {
            Text(
                modifier = Modifier.padding(12.dp),
                text = "Straight Line Chart Style",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            StraightLinechart(DataUtils.getLineChartData(50, maxRange = 200))
            Spacer(modifier = Modifier.height(12.dp))
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            )
        }
        item {
            Text(
                modifier = Modifier.padding(12.dp),
                text = "Dotted Line Chart Style",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            DottedLinechart(
                DataUtils.getLineChartData(
                    listSize = 200,
                    start = -50,
                    maxRange = 50
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            )
        }
        item {
            Text(
                modifier = Modifier.padding(12.dp),
                text = "Multiple Tone Line Chart",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            MultipleToneLinechart(
                DataUtils.getLineChartData(
                    listSize = 200,
                    start = -50,
                    maxRange = 50
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            )
        }
        item {
            Text(
                modifier = Modifier.padding(12.dp),
                text = "Combined Line Chart",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            CombinedLinechart(
                DataUtils.getLineChartData(
                    listSize = 200,
                    start = -50,
                    maxRange = 50
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            )
        }
        item {
            Text(
                modifier = Modifier.padding(12.dp),
                text = "Combined Line Chart with Background",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            CombinedLinechartWithBackground(
                DataUtils.getLineChartData(
                    listSize = 200,
                    start = -50,
                    maxRange = 50
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            )
        }
    }
}


@Composable
private fun SingleLineChartWithGridLines(pointsData: List<Point>) {
    val steps = 5
    val xAxisData = AxisData.Builder()
        .axisStepSize(30.dp)
        .topPadding(105.dp)
        .steps(pointsData.size - 1)
        .labelData { i -> pointsData[i].x.toInt().toString() }
        .labelAndAxisLinePadding(15.dp)
        .build()
    val yAxisData = AxisData.Builder()
        .steps(steps)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i ->
            // Add yMin to get the negative axis values to the scale
            val yMin = pointsData.minOf { it.y }
            val yMax = pointsData.maxOf { it.y }
            val yScale = (yMax - yMin) / steps
            ((i * yScale) + yMin).formatToSinglePrecision()
        }.build()
    val data = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    LineStyle(),
                    IntersectionPoint(),
                    SelectionHighlightPoint(),
                    ShadowUnderLine(),
                    SelectionHighlightPopUp()
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines()
    )
    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        lineChartData = data
    )
}


/**
 * Straight linechart
 *
 * @param pointsData
 */
@Composable
private fun StraightLinechart(pointsData: List<Point>) {
    val xAxisData = AxisData.Builder()
        .axisStepSize(40.dp)
        .steps(pointsData.size - 1)
        .labelData { i -> (1900 + i).toString() }
        .axisLabelAngle(20f)
        .labelAndAxisLinePadding(15.dp)
        .axisLabelColor(Color.Blue)
        .axisLineColor(Color.DarkGray)
        .typeFace(Typeface.DEFAULT_BOLD)
        .build()
    val yAxisData = AxisData.Builder()
        .steps(10)
        .labelData { i -> "${(i * 20)}k" }
        .labelAndAxisLinePadding(30.dp)
        .axisLabelColor(Color.Blue)
        .axisLineColor(Color.DarkGray)
        .typeFace(Typeface.DEFAULT_BOLD)
        .build()
    val data = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    lineStyle = LineStyle(lineType = LineType.Straight(), color = Color.Blue),
                    intersectionPoint = IntersectionPoint(color = Color.Red),
                    selectionHighlightPopUp = SelectionHighlightPopUp(popUpLabel = { x, y ->
                        val xLabel = "x : ${(1900 + x).toInt()} "
                        val yLabel = "y : ${String.format("%.2f", y)}"
                        "$xLabel $yLabel"
                    })
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData
    )
    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        lineChartData = data
    )
}

/**
 * Multiple tone linechart
 *
 * @param pointsData
 */
@Composable
private fun MultipleToneLinechart(pointsData: List<Point>) {
    val xAxisData = AxisData.Builder()
        .axisStepSize(40.dp)
        .steps(pointsData.size - 1)
        .labelData { i -> (1900 + i).toString() }
        .axisLabelAngle(20f)
        .labelAndAxisLinePadding(15.dp)
        .axisLabelColor(Color.Blue)
        .axisLineColor(Color.DarkGray)
        .typeFace(Typeface.DEFAULT_BOLD)
        .build()
    val yAxisData = AxisData.Builder()
        .steps(10)
        .labelData { i -> "${(i * 20)}k" }
        .labelAndAxisLinePadding(30.dp)
        .axisLabelColor(Color.Blue)
        .axisLineColor(Color.DarkGray)
        .typeFace(Typeface.DEFAULT_BOLD)
        .build()
    val data = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    lineStyle = LineStyle(lineType = LineType.Straight(), color = Color.Blue),
                    intersectionPoint = IntersectionPoint(color = Color.Red),
                    selectionHighlightPopUp = SelectionHighlightPopUp(popUpLabel = { x, y ->
                        val xLabel = "x : ${(1900 + x).toInt()} "
                        val yLabel = "y : ${String.format("%.2f", y)}"
                        "$xLabel $yLabel"
                    })
                ), Line(
                    dataPoints = pointsData.subList(0, 10),
                    lineStyle = LineStyle(lineType = LineType.Straight(), color = Color.Magenta),
                    intersectionPoint = IntersectionPoint(color = Color.Red),
                    selectionHighlightPopUp = SelectionHighlightPopUp(popUpLabel = { x, y ->
                        val xLabel = "x : ${(1900 + x).toInt()} "
                        val yLabel = "y : ${String.format("%.2f", y)}"
                        "$xLabel $yLabel"
                    })
                ), Line(
                    dataPoints = pointsData.subList(15, 30),
                    lineStyle = LineStyle(lineType = LineType.Straight(), color = Color.Yellow),
                    intersectionPoint = IntersectionPoint(color = Color.Red),
                    selectionHighlightPopUp = SelectionHighlightPopUp(popUpLabel = { x, y ->
                        val xLabel = "x : ${(1900 + x).toInt()} "
                        val yLabel = "y : ${String.format("%.2f", y)}"
                        "$xLabel $yLabel"
                    })
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData
    )
    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        lineChartData = data
    )
}

/**
 * Dotted linechart
 *
 * @param pointsData
 */
@Composable
private fun DottedLinechart(pointsData: List<Point>) {
    val steps = 10
    val xAxisData = AxisData.Builder()
        .axisStepSize(40.dp)
        .steps(pointsData.size - 1)
        .labelData { i -> i.toString() }
        .labelAndAxisLinePadding(15.dp)
        .axisLineColor(Color.Red)
        .build()
    val yAxisData = AxisData.Builder()
        .steps(steps)
        .labelData { i ->
            val yMin = pointsData.minOf { it.y }
            val yMax = pointsData.maxOf { it.y }
            val yScale = (yMax - yMin) / steps
            ((i * yScale) + yMin).formatToSinglePrecision()
        }
        .axisLineColor(Color.Red)
        .labelAndAxisLinePadding(20.dp)
        .build()
    val data = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    lineStyle = LineStyle(
                        lineType = LineType.SmoothCurve(isDotted = true),
                        color = Color.Green
                    ),
                    shadowUnderLine = ShadowUnderLine(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.Green,
                                Color.Transparent
                            )
                        ), alpha = 0.3f
                    ),
                    selectionHighlightPoint = SelectionHighlightPoint(
                        color = Color.Green
                    ),
                    selectionHighlightPopUp = SelectionHighlightPopUp(
                        backgroundColor = Color.Black,
                        backgroundStyle = Stroke(2f),
                        labelColor = Color.Red,
                        labelTypeface = Typeface.DEFAULT_BOLD
                    )
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData
    )
    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        lineChartData = data
    )
}

/**
 * Combined linechart
 *
 * @param pointsData
 */
@Composable
private fun CombinedLinechart(pointsData: List<Point>) {
    val steps = 5
    val xAxisData = AxisData.Builder()
        .axisStepSize(30.dp)
        .steps(pointsData.size - 1)
        .labelData { i -> i.toString() }
        .labelAndAxisLinePadding(15.dp)
        .build()
    val yAxisData = AxisData.Builder()
        .steps(steps)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i ->
            // Add yMin to get the negative axis values to the scale
            val yMin = pointsData.minOf { it.y }
            val yMax = pointsData.maxOf { it.y }
            val yScale = (yMax - yMin) / steps
            ((i * yScale) + yMin).formatToSinglePrecision()
        }.build()
    val colorPaletteList = listOf<Color>(Color.Blue, Color.Yellow, Color.Magenta, Color.DarkGray)
    val legendsConfig = LegendsConfig(
        legendLabelList = DataUtils.getLegendsLabelData(colorPaletteList),
        gridColumnCount = 4
    )
    val data = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    lineStyle = LineStyle(
                        lineType = LineType.SmoothCurve(isDotted = true),
                        color = colorPaletteList.first()
                    ),
                    shadowUnderLine = ShadowUnderLine(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.Green,
                                Color.Transparent
                            )
                        ), alpha = 0.3f
                    ),
                    selectionHighlightPoint = SelectionHighlightPoint(
                        color = Color.Green
                    ),
                    selectionHighlightPopUp = SelectionHighlightPopUp(
                        backgroundColor = Color.Black,
                        backgroundStyle = Stroke(2f),
                        labelColor = Color.Red,
                        labelTypeface = Typeface.DEFAULT_BOLD
                    )
                ),
                Line(
                    dataPoints = pointsData.subList(10, 20),
                    lineStyle = LineStyle(
                        lineType = LineType.SmoothCurve(),
                        color = colorPaletteList[1]
                    ),
                    intersectionPoint = IntersectionPoint(color = Color.Red),
                    selectionHighlightPopUp = SelectionHighlightPopUp(popUpLabel = { x, y ->
                        val xLabel = "x : ${(1900 + x).toInt()} "
                        val yLabel = "y : ${String.format("%.2f", y)}"
                        "$xLabel $yLabel"
                    })
                ),
                Line(
                    dataPoints = DataUtils.getLineChartData(
                        20,
                        start = 0,
                        maxRange = 50
                    ),
                    LineStyle(color = colorPaletteList[2]),
                    IntersectionPoint(),
                    SelectionHighlightPoint(),
                    shadowUnderLine = ShadowUnderLine(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.Cyan,
                                Color.Blue
                            )
                        ), alpha = 0.5f
                    ),
                    SelectionHighlightPopUp()
                ),
                Line(
                    dataPoints = pointsData.subList(10, 20),
                    LineStyle(color = colorPaletteList[3]),
                    IntersectionPoint(),
                    SelectionHighlightPoint(),
                    ShadowUnderLine(),
                    SelectionHighlightPopUp()
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines()
    )

    Column(modifier = Modifier.height(400.dp)) {
        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            lineChartData = data
        )
        Legends(
            legendsConfig = legendsConfig
        )
    }
}

/**
 * Combined linechart with background
 *
 * @param pointsData
 */
@Composable
private fun CombinedLinechartWithBackground(pointsData: List<Point>) {
    val steps = 5
    val xAxisData = AxisData.Builder()
        .axisStepSize(30.dp)
        .steps(pointsData.size - 1)
        .backgroundColor(Color.Yellow)
        .labelData { i -> i.toString() }
        .labelAndAxisLinePadding(15.dp)
        .build()
    val yAxisData = AxisData.Builder()
        .steps(steps)
        .backgroundColor(Color.Yellow)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i ->
            // Add yMin to get the negative axis values to the scale
            val yMin = pointsData.minOf { it.y }
            val yMax = pointsData.maxOf { it.y }
            val yScale = (yMax - yMin) / steps
            ((i * yScale) + yMin).formatToSinglePrecision()
        }.build()
    val colorPaletteList = listOf<Color>(Color.Blue, Color.Yellow, Color.Magenta, Color.DarkGray)
    val legendsConfig = LegendsConfig(
        legendLabelList = DataUtils.getLegendsLabelData(colorPaletteList),
        gridColumnCount = 4
    )
    val data = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    lineStyle = LineStyle(
                        lineType = LineType.SmoothCurve(isDotted = true),
                        color = colorPaletteList.first()
                    ),
                    shadowUnderLine = ShadowUnderLine(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.Green,
                                Color.Transparent
                            )
                        ), alpha = 0.3f
                    ),
                    selectionHighlightPoint = SelectionHighlightPoint(
                        color = Color.Green
                    ),
                    selectionHighlightPopUp = SelectionHighlightPopUp(
                        backgroundColor = Color.Black,
                        backgroundStyle = Stroke(2f),
                        labelColor = Color.Red,
                        labelTypeface = Typeface.DEFAULT_BOLD
                    )
                ),
                Line(
                    dataPoints = pointsData.subList(10, 20),
                    lineStyle = LineStyle(
                        lineType = LineType.SmoothCurve(),
                        color = colorPaletteList[1]
                    ),
                    intersectionPoint = IntersectionPoint(color = Color.Red),
                    selectionHighlightPopUp = SelectionHighlightPopUp(popUpLabel = { x, y ->
                        val xLabel = "x : ${(1900 + x).toInt()} "
                        val yLabel = "y : ${String.format("%.2f", y)}"
                        "$xLabel $yLabel"
                    })
                ),
                Line(
                    dataPoints = DataUtils.getLineChartData(
                        20,
                        start = 0,
                        maxRange = 50
                    ),
                    LineStyle(color = colorPaletteList[2]),
                    IntersectionPoint(),
                    SelectionHighlightPoint(),
                    shadowUnderLine = ShadowUnderLine(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.Cyan,
                                Color.Blue
                            )
                        ), alpha = 0.5f
                    ),
                    SelectionHighlightPopUp()
                ),
                Line(
                    dataPoints = pointsData.subList(10, 20),
                    LineStyle(color = colorPaletteList[3]),
                    IntersectionPoint(),
                    SelectionHighlightPoint(),
                    ShadowUnderLine(),
                    SelectionHighlightPopUp()
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(),
        backgroundColor = Color.Yellow
    )

    Column(
        modifier = Modifier
            .height(400.dp)
    ) {
        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            lineChartData = data
        )
        Legends(
            legendsConfig = legendsConfig
        )
    }
}