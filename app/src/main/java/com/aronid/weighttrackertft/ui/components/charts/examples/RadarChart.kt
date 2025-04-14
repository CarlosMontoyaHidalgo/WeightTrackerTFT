package com.aronid.weighttrackertft.ui.components.charts.examples

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

data class RadarData(
    val labels: List<String>, // Nombres de los músculos (ej. "Pecho", "Espalda")
    val values: List<Float>,  // Valores de progreso o fuerza (ej. 0-100)
    val maxValue: Float       // Valor máximo para escalar (ej. 100)
)

@Composable
fun RadarChart(
    modifier: Modifier = Modifier,
    data: RadarData,
    gridColor: Color = Color.Gray,
    dataColor: Color = Color.Blue,
    fillColor: Color = Color.Blue.copy(alpha = 0.3f),
    labelColor: Color = Color.Black,
    labelDistanceFactor: Float = 1.2f
) {
    val context = LocalContext.current
    Canvas(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { tapOffset ->
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = min(size.width, size.height) / 2f * 0.8f
                val numAxes = data.labels.size
                val angleStep = 2 * Math.PI / numAxes
                val labelRadius = radius * labelDistanceFactor

                for (i in 0 until numAxes) {
                    val angle = i * angleStep
                    val labelX = center.x + labelRadius * cos(angle).toFloat()
                    val labelY = center.y + labelRadius * sin(angle).toFloat()
                    val labelPosition = Offset(labelX, labelY)

                    val distance = sqrt(
                        (tapOffset.x - labelPosition.x) * (tapOffset.x - labelPosition.x) +
                                (tapOffset.y - labelPosition.y) * (tapOffset.y - labelPosition.y)
                    )

                    if (distance < 50f) {
                        val percentage = (data.values[i] / data.maxValue) * 100
                        Toast.makeText(
                            context,
                            "${data.labels[i]}: ${String.format("%.1f", percentage)}%",
                            Toast.LENGTH_SHORT
                        ).show()
                        break
                    }
                }
            }
        }
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = min(size.width, size.height) / 2f * 0.8f
        val numAxes = data.labels.size
        val angleStep = 2 * Math.PI / numAxes

        // Dibujar las líneas de la cuadrícula (niveles concéntricos)
        val numLevels = 5
        for (level in 1..numLevels) {
            val levelRadius = radius * (level.toFloat() / numLevels)
            val gridPath = Path()
            for (i in 0 until numAxes) {
                val angle = i * angleStep
                val x = center.x + levelRadius * cos(angle).toFloat()
                val y = center.y + levelRadius * sin(angle).toFloat()
                if (i == 0) {
                    gridPath.moveTo(x, y)
                } else {
                    gridPath.lineTo(x, y)
                }
            }
            gridPath.close()
            drawPath(
                path = gridPath,
                color = gridColor,
                style = Stroke(width = 2f, cap = StrokeCap.Round)
            )
        }

        // Dibujar los ejes radiales y etiquetas
        for (i in 0 until numAxes) {
            val angle = i * angleStep
            val x = center.x + radius * cos(angle).toFloat()
            val y = center.y + radius * sin(angle).toFloat()
            drawLine(
                color = gridColor,
                start = center,
                end = Offset(x, y),
                strokeWidth = 2f
            )

            // Dibujar etiquetas con distancia configurable
            val labelRadius = radius * labelDistanceFactor
            val labelX = center.x + labelRadius * cos(angle).toFloat()
            val labelY = center.y + labelRadius * sin(angle).toFloat()
            drawIntoCanvas {
                val paint = android.graphics.Paint().apply {
                    color = labelColor.toArgb()
                    textSize = 24f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                it.nativeCanvas.drawText(data.labels[i], labelX, labelY, paint)
            }
        }

        // Dibujar los datos
        val dataPath = Path()
        data.values.forEachIndexed { index, value ->
            val normalizedValue = (value / data.maxValue) * radius
            val angle = index * angleStep
            val x = center.x + normalizedValue * cos(angle).toFloat()
            val y = center.y + normalizedValue * sin(angle).toFloat()
            if (index == 0) {
                dataPath.moveTo(x, y)
            } else {
                dataPath.lineTo(x, y)
            }
        }
        dataPath.close()

        // Relleno de los datos
        drawPath(
            path = dataPath,
            color = fillColor
        )
        // Contorno de los datos
        drawPath(
            path = dataPath,
            color = dataColor,
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun TrainingRadarChartExample() {
    val trainingStats = RadarData(
        labels = listOf("Pecho", "Espalda", "Piernas", "Bíceps", "Tríceps"),
        values = listOf(70f, 65f, 80f, 50f, 55f), // Ejemplo de progreso
        maxValue = 100f
    )

    RadarChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .padding(16.dp),
        data = trainingStats,
        gridColor = Color.LightGray,
        dataColor = Color.Green,
        fillColor = Color.Green.copy(alpha = 0.2f),
        labelColor = Color.DarkGray,
        labelDistanceFactor = 1.3f
    )
}

@Preview(showBackground = true)
@Composable
fun TrainingRadarChartPreview() {
    TrainingRadarChartExample()
}