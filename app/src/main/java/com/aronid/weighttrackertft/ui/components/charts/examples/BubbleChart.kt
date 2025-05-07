package com.aronid.weighttrackertft.ui.components.charts.examples

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.extensions.formatToSinglePrecision
import co.yml.charts.common.model.Point
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.bubblechart.BubbleChart
import co.yml.charts.ui.bubblechart.model.BubbleChartData
import co.yml.charts.ui.linechart.model.GridLines

@Composable
fun BubbleChartExamples() {
    BubbleChartWithGrid(
        pointsData = DataUtils.getRandomPoints(
            200,
            start = 30,
            maxRange = 100
        )
    )

    SolidBubbleChart(
        pointsData = DataUtils.getRandomPoints(
            200,
            start = 30,
            maxRange = 900
        )
    )
}

/**
 * Bubble chart with grid lines
 *
 * @param pointsData
 */
@Composable
private fun BubbleChartWithGrid(pointsData: List<Point>) {
    val steps = 5
    val xAxisData = AxisData.Builder()
        .axisStepSize(30.dp)
        .steps(pointsData.size - 1)
        .labelData { i -> pointsData[i].x.toInt().toString() }
        .labelAndAxisLinePadding(15.dp)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(steps)
        .labelAndAxisLinePadding(15.dp)
        .labelData { i ->
            // Add yMin to get the negative axis values to the scale
            val yMin = pointsData.minOf { it.y }
            val yMax = pointsData.maxOf { it.y }
            val yScale = (yMax - yMin) / steps
            ((i * yScale) + yMin).formatToSinglePrecision()
        }.build()

    val data = BubbleChartData(
        DataUtils.getBubbleChartDataWithGradientStyle(pointsData),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines()
    )

    BubbleChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp),
        bubbleChartData = data
    )

}


/**
 * Bubble chart with grid lines
 *
 * @param pointsData
 */
@Composable
private fun SolidBubbleChart(pointsData: List<Point>) {
    val steps = 5
    val xAxisData = AxisData.Builder()
        .axisStepSize(30.dp)
        .steps(pointsData.size - 1)
        .labelData { i -> pointsData[i].x.toInt().toString() }
        .labelAndAxisLinePadding(15.dp)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(steps)
        .labelAndAxisLinePadding(15.dp)
        .labelData { i ->
            // Add yMin to get the negative axis values to the scale
            val yMin = pointsData.minOf { it.y }
            val yMax = pointsData.maxOf { it.y }
            val yScale = (yMax - yMin) / steps
            ((i * yScale) + yMin).formatToSinglePrecision()
        }.build()

    val data = BubbleChartData(
        DataUtils.getBubbleChartDataWithSolidStyle(pointsData),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines()
    )

    BubbleChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp),
        bubbleChartData = data
    )

}