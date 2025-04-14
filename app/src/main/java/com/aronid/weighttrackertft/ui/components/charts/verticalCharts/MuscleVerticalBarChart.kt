package com.aronid.weighttrackertft.ui.components.charts.verticalCharts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle


@Composable
fun MuscleVerticalBarChart(muscles: List<Pair<String, Float>>) {
    val maxRange = 100
    val barData = muscles.mapIndexed { index, (label, value) ->
        BarData(
            point = co.yml.charts.common.model.Point(x = index.toFloat(), y = value),
            label = label,
            color = if (value == 100f) Color.Green else Color.Blue
        )
    }
    val yStepSize = 5

    val xAxisData = AxisData.Builder()
        .axisStepSize(30.dp)
        .steps(barData.size - 1)
        .bottomPadding(12.dp)
        .axisLabelAngle(20f)
        .startDrawPadding(48.dp)
        .labelData { index -> barData[index].label }
        .build()

    val yAxisData = AxisData.Builder()
        .steps(yStepSize)
        .labelAndAxisLinePadding(20.dp)
        .axisOffset(20.dp)
        .labelData { index -> (index * (maxRange / yStepSize)).toString() }
        .build()

    val barChartData = BarChartData(
        chartData = barData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        barStyle = BarStyle(
            paddingBetweenBars = 20.dp,
            barWidth = 25.dp
        ),
        showYAxis = true,
        showXAxis = true,
        horizontalExtraSpace = 10.dp
    )

    BarChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .padding(16.dp),
        barChartData = barChartData
    )
}