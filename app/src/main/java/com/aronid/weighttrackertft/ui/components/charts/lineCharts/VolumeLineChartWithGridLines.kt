package com.aronid.weighttrackertft.ui.components.charts.lineCharts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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