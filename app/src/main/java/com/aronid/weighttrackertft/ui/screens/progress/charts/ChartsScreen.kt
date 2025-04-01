package com.aronid.weighttrackertft.ui.screens.progress.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.PlotType
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData

import com.aronid.weighttrackertft.ui.components.charts.barCharts.CaloriesBarChart


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import com.aronid.weighttrackertft.ui.components.charts.barCharts.CaloriesBarChart

@Composable
fun ChartsScreen(innerPadding: PaddingValues, navHostController: NavHostController) {
    val viewModel: ChartsViewModel = hiltViewModel()

    val caloriesData by viewModel.caloriesData.collectAsStateWithLifecycle()
    val totalCalories by viewModel.totalCalories.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var selectedPeriod by remember { mutableStateOf("weekly") }

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tu Progreso",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Total de calorías quemadas del período seleccionado
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
                    text = "Total Calorías ${selectedPeriod.replaceFirstChar { it.uppercase() }}",
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

        // Selector de período con botones
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("weekly", "monthly", "yearly").forEach { period ->
                Button(
                    onClick = {
                        selectedPeriod = period
                        viewModel.loadCalories(period)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedPeriod == period)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.secondaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = period.replaceFirstChar { it.uppercase() },
                        color = if (selectedPeriod == period)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Gráfico de barras en una tarjeta
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
                    CaloriesBarChart(
                        caloriesData = caloriesData,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        barColor = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}


/*
@Composable
fun ChartsScreen(innerPadding: PaddingValues, navHostController: NavHostController) {
    var selectedPeriod by remember { mutableStateOf("Semanal") }
    var selectedWeek by remember { mutableStateOf(1) }
    var selectedMonth by remember { mutableStateOf(1) }
    var selectedYear by remember { mutableStateOf(2025) }

    val periods = listOf("Semanal", "Mensual", "Anual")
    val weeks = (1..52).toList()
    val months = (1..12).toList()
    val years = (2020..2025).toList()

    // Datos de ejemplo para diferentes períodos y selecciones específicas
    val caloriesData = mapOf(
        "Semanal" to mapOf(
            1 to listOf(500f, 600f, 450f, 700f, 550f, 620f, 580f),
            2 to listOf(520f, 590f, 470f, 680f, 560f, 610f, 570f)
        ),
        "Mensual" to mapOf(
            1 to listOf(2000f, 2200f, 1900f, 2500f),
            2 to listOf(2100f, 2300f, 2000f, 2400f)
        ),
        "Anual" to mapOf(
            2025 to listOf(24000f, 26000f, 23000f, 25000f, 24500f, 25500f, 23500f, 24800f, 24200f, 26000f, 23800f, 25000f),
            2024 to listOf(23000f, 25500f, 22500f, 24500f, 24000f, 25000f, 22800f, 24200f, 23800f, 25500f, 23200f, 24800f)
        )
    )
    val volumeData = mapOf(
        "Semanal" to mapOf(
            1 to listOf(100f, 120f, 90f, 130f, 110f, 115f, 125f),
            2 to listOf(105f, 115f, 95f, 125f, 108f, 112f, 120f)
        ),
        "Mensual" to mapOf(
            1 to listOf(450f, 480f, 420f, 500f),
            2 to listOf(460f, 490f, 430f, 510f)
        ),
        "Anual" to mapOf(
            2025 to listOf(5400f, 5800f, 5200f, 5600f, 5500f, 5700f, 5300f, 5450f, 5550f, 5800f, 5250f, 5650f),
            2024 to listOf(5300f, 5700f, 5100f, 5500f, 5400f, 5600f, 5200f, 5350f, 5450f, 5700f, 5150f, 5550f)
        )
    )
    val weightData = mapOf(
        "Semanal" to mapOf(
            1 to listOf(70f, 69.5f, 69f, 70.2f, 68.8f, 69.1f, 68.5f),
            2 to listOf(69.8f, 69.3f, 68.9f, 70f, 68.7f, 69f, 68.4f)
        ),
        "Mensual" to mapOf(
            1 to listOf(70f, 69.5f, 69.2f, 68.8f),
            2 to listOf(69.8f, 69.4f, 69.1f, 68.7f)
        ),
        "Anual" to mapOf(
            2025 to listOf(70f, 69.8f, 69.5f, 69.3f, 69.1f, 68.9f, 68.7f, 68.6f, 68.5f, 68.4f, 68.3f, 68.2f),
            2024 to listOf(70.5f, 70.3f, 70.1f, 69.9f, 69.7f, 69.5f, 69.3f, 69.2f, 69.1f, 69f, 68.9f, 68.8f)
        )
    )

    val charts = listOf(
        ChartData(
            title = "Calorías Quemadas",
            data = when (selectedPeriod) {
                "Semanal" -> caloriesData["Semanal"]?.get(selectedWeek) ?: emptyList()
                "Mensual" -> caloriesData["Mensual"]?.get(selectedMonth) ?: emptyList()
                "Anual" -> caloriesData["Anual"]?.get(selectedYear) ?: emptyList()
                else -> emptyList()
            },
            unit = "kcal",
            color = MaterialTheme.colorScheme.primary,
            chartType = ChartType.DONUT
        ),
        ChartData(
            title = "Volumen Entrenamiento",
            data = when (selectedPeriod) {
                "Semanal" -> volumeData["Semanal"]?.get(selectedWeek) ?: emptyList()
                "Mensual" -> volumeData["Mensual"]?.get(selectedMonth) ?: emptyList()
                "Anual" -> volumeData["Anual"]?.get(selectedYear) ?: emptyList()
                else -> emptyList()
            },
            unit = "kg",
            color = MaterialTheme.colorScheme.secondary,
            chartType = ChartType.LINE
        ),
        ChartData(
            title = "Peso",
            data = when (selectedPeriod) {
                "Semanal" -> weightData["Semanal"]?.get(selectedWeek) ?: emptyList()
                "Mensual" -> weightData["Mensual"]?.get(selectedMonth) ?: emptyList()
                "Anual" -> weightData["Anual"]?.get(selectedYear) ?: emptyList()
                else -> emptyList()
            },
            unit = "kg",
            color = MaterialTheme.colorScheme.tertiary,
            chartType = ChartType.BAR
        )
    )

    val pagerState = rememberPagerState(pageCount = { charts.size })

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        // Selector de período
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            periods.forEach { period ->
                Button(
                    onClick = { selectedPeriod = period },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedPeriod == period)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surface,
                        contentColor = if (selectedPeriod == period)
                            MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                ) {
                    Text(text = period, fontSize = 14.sp)
                }
            }
        }

        // Selector específico según el período
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (selectedPeriod) {
                "Semanal" -> {
                    val (startDay, endDay, month) = calculateWeekRange(selectedWeek, selectedYear)
                    Text(
                        text = "${monthName(month)} $startDay-$endDay",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { if (selectedWeek > 1) selectedWeek-- }) { Text("<") }
                        Text(
                            text = "Sem $selectedWeek",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.width(60.dp),
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = { if (selectedWeek < weeks.size) selectedWeek++ }) { Text(">") }
                    }
                }
                "Mensual" -> {
                    Text("Mes:", style = MaterialTheme.typography.bodyMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { if (selectedMonth > 1) selectedMonth-- }) { Text("<") }
                        Text(
                            text = monthName(selectedMonth),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.width(80.dp),
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = { if (selectedMonth < months.size) selectedMonth++ }) { Text(">") }
                    }
                }
                "Anual" -> {
                    Text("Año:", style = MaterialTheme.typography.bodyMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { if (selectedYear > years.minOrNull()!!) selectedYear-- }) { Text("<") }
                        Text(
                            text = "$selectedYear",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.width(60.dp),
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = { if (selectedYear < years.maxOrNull()!!) selectedYear++ }) { Text(">") }
                    }
                }
            }
        }

        // Carrusel con gráficos
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            ChartCard(
                title = charts[page].title,
                data = charts[page].data,
                unit = charts[page].unit,
                color = charts[page].color,
                chartType = charts[page].chartType,
                period = selectedPeriod,
                week = if (selectedPeriod == "Semanal") selectedWeek else null,
                month = if (selectedPeriod == "Mensual") selectedMonth else null,
                year = selectedYear
            )
        }

        // Indicadores de puntos
        DotsIndicator(
            totalDots = charts.size,
            selectedIndex = pagerState.currentPage
        )
    }
}

enum class ChartType {
    DONUT, LINE, BAR
}

data class ChartData(
    val title: String,
    val data: List<Float>,
    val unit: String,
    val color: Color,
    val chartType: ChartType
)

@Composable
fun ChartCard(
    title: String,
    data: List<Float>,
    unit: String,
    color: Color,
    chartType: ChartType,
    period: String,
    week: Int?,
    month: Int?,
    year: Int
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (period) {
                    "Semanal" -> {
                        val (startDay, endDay, monthNum) = calculateWeekRange(week ?: 1, year)
                        "$title ${monthName(monthNum)} $startDay-$endDay $year"
                    }
                    "Mensual" -> "$title ${monthName(month ?: 1)} $year"
                    "Anual" -> "$title $year"
                    else -> title
                },
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            when (chartType) {
                ChartType.DONUT -> {
                    val pieChartData = PieChartData(
                        slices = data.mapIndexed { index, value ->
                            PieChartData.Slice(
                                label = when (period) {
                                    "Semanal" -> "Día ${index + 1}"
                                    "Mensual" -> "Sem ${index + 1}"
                                    "Anual" -> "Mes ${index + 1}"
                                    else -> "Punto ${index + 1}"
                                },
                                value = value,
                                color = color.copy(alpha = 0.7f + (index * 0.1f).coerceAtMost(0.3f))
                            )
                        },
                        plotType = PlotType.Donut
                    )
                    val pieChartConfig = PieChartConfig(
                        isAnimationEnable = true,
                        showSliceLabels = true,
                        strokeWidth = 80f,
                        activeSliceAlpha = 1f,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    )
                    DonutPieChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        pieChartData = pieChartData,
                        pieChartConfig = pieChartConfig
                    )
                }
                ChartType.LINE -> {
                    val xAxisData = AxisData.Builder()
                        .steps(data.size - 1)
                        .labelData { index ->
                            when (period) {
                                "Semanal" -> "Día ${index + 1}"
                                "Mensual" -> "Sem ${index + 1}"
                                "Anual" -> "Mes ${index + 1}"
                                else -> "Punto ${index + 1}"
                            }
                        }
                        .axisLineColor(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        .axisLabelColor(MaterialTheme.colorScheme.onSurface)
                        .labelAndAxisLinePadding(8.dp)
                        .build()

                    val yAxisData = AxisData.Builder()
                        .steps(5)
                        .labelData { it.toString() }
                        .axisLineColor(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        .axisLabelColor(MaterialTheme.colorScheme.onSurface)
                        .labelAndAxisLinePadding(8.dp)
                        .build()

                    val lineChartData = LineChartData(
                        linePlotData = LinePlotData(
                            lines = listOf(
                                Line(
                                    dataPoints = data.mapIndexed { index, value -> Point(index.toFloat(), value) },
                                    lineStyle = LineStyle(
                                        color = color,
                                        width = 4f,
                                    ),
                                    intersectionPoint = IntersectionPoint(
                                        color = color,
                                        radius = 6.dp
                                    )
                                )
                            )
                        ),
                        xAxisData = xAxisData,
                        yAxisData = yAxisData,
                        gridLines = GridLines(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    )
                    LineChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        lineChartData = lineChartData
                    )
                }
                ChartType.BAR -> {
                    val xAxisData = AxisData.Builder()
                        .steps(data.size - 1)
                        .labelData { index ->
                            when (period) {
                                "Semanal" -> "Día ${index + 1}"
                                "Mensual" -> "Sem ${index + 1}"
                                "Anual" -> "Mes ${index + 1}"
                                else -> "Punto ${index + 1}"
                            }
                        }
                        .axisLineColor(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        .axisLabelColor(MaterialTheme.colorScheme.onSurface)
                        .labelAndAxisLinePadding(8.dp)
                        .build()

                    val yAxisData = AxisData.Builder()
                        .steps(5)
                        .labelData { it.toString() }
                        .axisLineColor(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        .axisLabelColor(MaterialTheme.colorScheme.onSurface)
                        .labelAndAxisLinePadding(8.dp)
                        .build()

                    val barChartData = BarChartData(
                        chartData = data.mapIndexed { index, value ->
                            BarData(
                                label = when (period) {
                                    "Semanal" -> "Día ${index + 1}"
                                    "Mensual" -> "Sem ${index + 1}"
                                    "Anual" -> "Mes ${index + 1}"
                                    else -> "Punto ${index + 1}"
                                },
                                point = Point(index.toFloat(), value),
                                color = color
                            )
                        },
                        xAxisData = xAxisData,
                        yAxisData = yAxisData
                    )
                    BarChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        barChartData = barChartData
                    )
                }
            }

            Text(
                text = "Último: ${data.lastOrNull() ?: 0f} $unit",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun DotsIndicator(totalDots: Int, selectedIndex: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(totalDots) { index ->
            Canvas(
                modifier = Modifier
                    .size(if (index == selectedIndex) 12.dp else 8.dp)
                    .padding(horizontal = 4.dp)
            ) {
                drawCircle(
                    color = if (index == selectedIndex)
                        Color.Black
                    else Color.Red
                )
            }
        }
    }
}

fun monthName(month: Int): String = when (month) {
    1 -> "Enero"
    2 -> "Febrero"
    3 -> "Marzo"
    4 -> "Abril"
    5 -> "Mayo"
    6 -> "Junio"
    7 -> "Julio"
    8 -> "Agosto"
    9 -> "Septiembre"
    10 -> "Octubre"
    11 -> "Noviembre"
    12 -> "Diciembre"
    else -> "Mes $month"
}

fun calculateWeekRange(week: Int, year: Int): Triple<Int, Int, Int> {
    // Suponiendo que el año comienza el 1 de enero y cada semana tiene 7 días
    val daysInYear = 365 + if (isLeapYear(year)) 1 else 0
    val startDayOfYear = (week - 1) * 7 + 1
    val endDayOfYear = minOf(startDayOfYear + 6, daysInYear)

    // Calcular mes y día basados en días del año (aproximación simple)
    val daysInMonth = listOf(31, if (isLeapYear(year)) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    var remainingDays = startDayOfYear
    var month = 1
    while (remainingDays > daysInMonth[month - 1]) {
        remainingDays -= daysInMonth[month - 1]
        month++
    }
    val startDay = remainingDays
    remainingDays = endDayOfYear
    var endMonth = 1
    while (remainingDays > daysInMonth[endMonth - 1]) {
        remainingDays -= daysInMonth[endMonth - 1]
        endMonth++
    }
    val endDay = remainingDays

    // Si la semana cruza meses, usamos el mes del inicio para simplificar
    return Triple(startDay, endDay, month)
}

fun isLeapYear(year: Int): Boolean {
    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
}


 */