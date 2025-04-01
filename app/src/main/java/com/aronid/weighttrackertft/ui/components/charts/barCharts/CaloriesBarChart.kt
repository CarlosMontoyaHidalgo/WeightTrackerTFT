package com.aronid.weighttrackertft.ui.components.charts.barCharts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData

@Composable
fun CaloriesBarChart(
    caloriesData: Map<String, Int>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary
) {
    if (caloriesData.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay datos disponibles",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        return
    }

    val maxValue = caloriesData.values.maxOf { it }
    val barData = caloriesData.entries.mapIndexed { index, (label, value) ->
        BarData(
            point = Point(
                x = index.toFloat(),
                y = value.toFloat()
            ),
            label = label,
            color = barColor
        )
    }

    val yStepSize = when {
        maxValue <= 500 -> 100
        maxValue <= 1000 -> 200
        else -> 500
    }

    val yAxisData = AxisData.Builder()
        .axisStepSize(yStepSize.toFloat().dp)
        .steps(maxValue / yStepSize)
        .labelData { value -> "$value kcal" }
        .labelAndAxisLinePadding(16.dp)
        .axisLabelColor(MaterialTheme.colorScheme.onSurface)
        .axisLineColor(MaterialTheme.colorScheme.outline)
        .build()

    val xAxisData = AxisData.Builder()
        .steps(barData.size - 1)
        .labelData { index -> barData[index].label }
        .labelAndAxisLinePadding(16.dp)
        .axisLabelColor(MaterialTheme.colorScheme.onSurface)
        .axisLineColor(MaterialTheme.colorScheme.outline)
        .build()

    val barChartData = BarChartData(
        chartData = barData,
        xAxisData = xAxisData,
        yAxisData = yAxisData
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Calorías Quemadas",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        BarChart(
            modifier = Modifier.fillMaxWidth(),
            barChartData = barChartData
        )
    }
}