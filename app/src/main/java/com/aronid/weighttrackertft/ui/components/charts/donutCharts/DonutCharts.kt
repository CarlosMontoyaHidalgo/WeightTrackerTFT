package com.aronid.weighttrackertft.ui.components.charts.donutCharts

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import co.yml.charts.common.model.PlotType
import kotlin.math.atan2
import kotlin.math.PI

@Composable
fun SimpleDonutChart(content: List<String>) {
    if (content.isEmpty()) {
        Text(text = "No hay datos disponibles", modifier = Modifier.padding(16.dp))
        return
    }

    val colors = listOf(Color.Blue, Color.Green, Color.Red, Color.Yellow, Color.Magenta)
    var selectedSlice by remember { mutableStateOf<Pair<String, Color>?>(null) }

    val slices = content.mapIndexed { index, value ->
        PieChartData.Slice(label = value, value = 100f, color = colors[index % colors.size])
    }

    val pieChartData = PieChartData(slices = slices, plotType = PlotType.Donut)

    val pieChartConfig = PieChartConfig(
        labelVisible = true,
        strokeWidth = 40f,
        labelColor = Color.Black,
        isAnimationEnable = true,
        backgroundColor = Color.Transparent,
        showSliceLabels = true
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            slices.forEach { slice ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier.size(16.dp).background(slice.color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = slice.label)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // **Gráfico de Donut con detección de selección**
//        Box(
//            modifier = Modifier
//                .size(300.dp)
//                .pointerInput(Unit) {
//                    detectTapGestures { offset ->
//                        val angle = calculateAngle(Offset(150f, 150f), offset)
//                        val selectedIndex = ((angle / (2 * PI) * slices.size).toInt()) % slices.size
//                        selectedSlice = slices[selectedIndex].label to slices[selectedIndex].color
//                    }
//                }
//        ) {
            DonutPieChart(modifier = Modifier.fillMaxSize().background(Color.Transparent), pieChartData = pieChartData, pieChartConfig = pieChartConfig)
//        }

        // **Mostrar músculo seleccionado**
        selectedSlice?.let { (label, color) ->
            Card(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(20.dp).background(color))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = label, color = color)
                }
            }
        }
    }
}

//fun calculateAngle(center: Offset, point: Offset): Double {
//    val dx = point.x - center.x
//    val dy = point.y - center.y
//    return (atan2(dy, dx).let { if (it < 0) it + 2 * PI else it }) as Double
//}

//@Preview(showBackground = true)
//@Composable
//fun PreviewDonutChart() {
//    SimpleDonutChart(content = listOf("Chest", "Back", "Shoulders", "Legs"))
//}
