package com.aronid.weighttrackertft.ui.components.charts.lineCharts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun VolumeLineChartWithGridLines(
    volumeData: Map<String, Int>,
    rangeType: String
) {
    // 1. Procesamiento de datos semanales
    val weeklyVolume = remember(volumeData) {
        runCatching {
            aggregateVolumeByWeek(volumeData)
        }.getOrElse { emptyMap() }
    }

    // 2. Manejo de estado sin datos
    if (weeklyVolume.values.all { it == 0 }) {
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

    // 3. Preparación de puntos para el gráfico
    val pointsData = remember(weeklyVolume) {
        (1..52).map { week ->
            Point(
                x = (week - 1).toFloat(),
                y = weeklyVolume["Semana $week"]?.toFloat() ?: 0f,
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
                        color = MaterialTheme.colorScheme.secondary, // Color diferente para volumen
                        width = 4f
                    ),
                    intersectionPoint = null, // Sin puntos de intersección
                    selectionHighlightPoint = SelectionHighlightPoint(
                        color = MaterialTheme.colorScheme.tertiary,
                        radius = 6.dp
                    ),
                    shadowUnderLine = ShadowUnderLine(
                        alpha = 0.15f,
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
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
                            "Semana $week: ${y.toInt()} kg"
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
        paddingRight = 0.dp, // Eliminar espacio blanco derecho
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

// Función para agregar datos de volumen por semana
private fun aggregateVolumeByWeek(volumeData: Map<String, Int>): Map<String, Int> {
    val weeklySums = mutableMapOf<Int, Int>().apply {
        for (week in 1..52) this[week] = 0
    }

    volumeData.forEach { (dayStr, volume) ->
        val dayNumber = dayStr.removePrefix("Día ").toIntOrNull() ?: return@forEach
        if (dayNumber in 1..366) {
            val weekNumber = ((dayNumber - 1) / 7) + 1
            weeklySums[weekNumber] = weeklySums.getOrDefault(weekNumber, 0) + volume
        }
    }

    return weeklySums.mapKeys { (week, _) -> "Semana $week" }
}


@Composable
fun VolumeBarChartWithGridLines(volumeData: Map<String, Int>) {
    // Handle empty data case
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

    // Map data to BarChart-compatible format
    val barData = volumeData.entries.mapIndexed { index, entry ->
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
            "$value kg"
        }
        .labelAndAxisLinePadding(25.dp)
        .axisLineColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .axisLabelColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .axisLabelDescription { "kg" }
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
                    "$dayLabel: ${y.toInt()} kg"
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
            text = "Resumen: Promedio: $average kg | Máx: $max kg | Mín: $min kg",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}