package com.aronid.weighttrackertft.ui.components.charts.examples

import android.graphics.Typeface
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.extensions.formatToSinglePrecision
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import co.yml.charts.ui.wavechart.WaveChart
import co.yml.charts.ui.wavechart.model.Wave
import co.yml.charts.ui.wavechart.model.WaveChartData
import co.yml.charts.ui.wavechart.model.WaveFillColor
import co.yml.charts.ui.wavechart.model.WavePlotData
import kotlin.math.sin

@Composable
fun WaveChartExample() {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                modifier = Modifier.padding(12.dp),
                text = "Simple Wave Chart",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            WaveGraph1(generateWaveData(points = 50, amplitude = 15.0, frequency = 0.1))
        }
        item {
            Text(
                modifier = Modifier.padding(12.dp),
                text = "Sine Wave Chart",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            WaveGraph2(generateWaveData(points = 50, amplitude = 15.0, frequency = 0.2))
        }
        item {
            Text(
                modifier = Modifier.padding(12.dp),
                text = "Amplitude Wave Chart",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            WaveGraph3(generateWaveData(points = 50, amplitude = 20.0, frequency = 0.05))
        }
    }
}

/**
 * Genera datos de onda usando una función seno.
 * @param points Número de puntos en la onda.
 * @param amplitude Amplitud de la onda.
 * @param frequency Frecuencia de la onda.
 * @return Lista de puntos para el WaveChart.
 */
private fun generateWaveData(points: Int, amplitude: Double, frequency: Double): List<Point> {
    return (0 until points).map { x ->
        val y = amplitude * sin(frequency * x)
        Point(x.toFloat(), y.toFloat())
    }
}

@Composable
private fun WaveGraph1(pointsData: List<Point>) {
    val steps = 5
    val xAxisData = AxisData.Builder()
        .axisStepSize(30.dp)
        .startDrawPadding(48.dp)
        .steps(pointsData.size - 1)
        .labelData { i -> i.toString() }
        .labelAndAxisLinePadding(15.dp)
        .build()
    val yAxisData = AxisData.Builder()
        .steps(steps)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i ->
            val yMin = pointsData.minOf { it.y }
            val yMax = pointsData.maxOf { it.y }
            val yScale = (yMax - yMin) / steps
            ((i * yScale) + yMin).formatToSinglePrecision()
        }.build()
    val data = WaveChartData(
        wavePlotData = WavePlotData(
            lines = listOf(
                Wave(
                    dataPoints = pointsData,
                    waveStyle = LineStyle(color = Color.Black),
                    selectionHighlightPoint = SelectionHighlightPoint(),
                    shadowUnderLine = ShadowUnderLine(),
                    selectionHighlightPopUp = SelectionHighlightPopUp(),
                    waveFillColor = WaveFillColor(topColor = Color.Green, bottomColor = Color.Red)
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines()
    )
    WaveChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        waveChartData = data
    )
}

@Composable
private fun WaveGraph2(pointsData: List<Point>) {
    val steps = 10
    val xAxisData = AxisData.Builder()
        .axisStepSize(40.dp)
        .startDrawPadding(48.dp)
        .steps(pointsData.size - 1)
        .labelData { i -> i.toString() }
        .axisLabelAngle(20f)
        .labelAndAxisLinePadding(15.dp)
        .axisLabelColor(Color.Blue)
        .axisLineColor(Color.DarkGray)
        .typeFace(Typeface.DEFAULT_BOLD)
        .build()
    val yAxisData = AxisData.Builder()
        .steps(steps)
        .labelData { i ->
            val yMin = pointsData.minOf { it.y }
            val yMax = pointsData.maxOf { it.y }
            val yScale = (yMax - yMin) / steps
            ((i * yScale) + yMin).formatToSinglePrecision()
        }
        .labelAndAxisLinePadding(30.dp)
        .axisLabelColor(Color.Blue)
        .axisLineColor(Color.DarkGray)
        .typeFace(Typeface.DEFAULT_BOLD)
        .build()
    val data = WaveChartData(
        wavePlotData = WavePlotData(
            lines = listOf(
                Wave(
                    dataPoints = pointsData,
                    waveStyle = LineStyle(lineType = LineType.SmoothCurve(), color = Color.Blue),
                    shadowUnderLine = ShadowUnderLine(alpha = 0.5f),
                    waveFillColor = WaveFillColor(
                        topColor = Color.Yellow,
                        bottomColor = Color.Magenta
                    ),
                    selectionHighlightPopUp = SelectionHighlightPopUp(popUpLabel = { x, y ->
                        val xLabel = "x: ${x.toInt()}"
                        val yLabel = "y: ${String.format("%.2f", y)}"
                        "$xLabel $yLabel"
                    })
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData
    )
    WaveChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        waveChartData = data
    )
}

@Composable
private fun WaveGraph3(pointsData: List<Point>) {
    val steps = 10
    val xAxisData = AxisData.Builder()
        .axisStepSize(40.dp)
        .startDrawPadding(48.dp)
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
        .bottomPadding(15.dp)
        .build()
    val data = WaveChartData(
        wavePlotData = WavePlotData(
            lines = listOf(
                Wave(
                    dataPoints = pointsData,
                    waveStyle = LineStyle(
                        lineType = LineType.Straight(isDotted = true),
                        color = Color.Green
                    ),
                    shadowUnderLine = ShadowUnderLine(
                        brush = Brush.verticalGradient(
                            listOf(Color.Green, Color.Transparent)
                        ),
                        alpha = 0.3f
                    ),
                    selectionHighlightPoint = SelectionHighlightPoint(color = Color.Green),
                    selectionHighlightPopUp = SelectionHighlightPopUp(
                        backgroundColor = Color.Black,
                        backgroundStyle = Stroke(2f),
                        labelColor = Color.Red,
                        labelTypeface = Typeface.DEFAULT_BOLD
                    ),
                    waveFillColor = WaveFillColor(
                        topColor = Color.Green,
                        bottomColor = Color.Red,
                        topBrush = Brush.verticalGradient(listOf(Color.Green, Color.Blue)),
                        bottomBrush = Brush.verticalGradient(listOf(Color.Green, Color.Red))
                    )
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData
    )
    WaveChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        waveChartData = data
    )
}
