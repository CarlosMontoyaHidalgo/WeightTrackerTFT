package com.aronid.weighttrackertft.ui.components.charts.lineCharts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        .axisLabelDescription { "kg" }
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
                            "$dayLabel: ${y.toInt()} kg"
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
            text = "Resumen: Promedio: $average kg | Máx: $max kg | Mín: $min kg",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}