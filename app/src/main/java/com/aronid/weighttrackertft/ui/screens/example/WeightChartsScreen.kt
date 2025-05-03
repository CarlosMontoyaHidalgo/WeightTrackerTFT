package com.aronid.weighttrackertft.ui.screens.example

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


@Composable
fun WeightChartsScreen(innerPadding: PaddingValues, navHostController: NavHostController) {
    //WeightChartWithTabs()
}

@Preview
@Composable
fun WeightChartWithTabs() {
    // Estado para la pestaña seleccionada
    var selectedTab by remember { mutableStateOf("Mes") }

    // Datos de ejemplo para cada vista
    val yearlyWeights = listOf(85f, 84f, 86f, 88f, 87f, 85f, 83f, 82f, 84f, 86f, 85f, 84f)
    val yearlyLabels = listOf("E", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D")

    // Datos para "Mes" (por semanas, supongamos abril)
    val monthlyWeights = listOf(88f, 87.5f, 87f, 86.5f)
    val monthlyLabels = listOf("Sem 1", "Sem 2", "Sem 3", "Sem 4")

    // Datos para "Trimestre" (por meses, supongamos Q2: abril, mayo, junio)
    val quarterlyWeights = listOf(88f, 87f, 85f)
    val quarterlyLabels = listOf("Abr", "May", "Jun")

    val minWeight = 70f
    val maxWeight = 95f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Pestañas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Mes", "Trimestre", "Año").forEach { tab ->
                Text(
                    text = tab,
                    fontSize = 16.sp,
                    color = if (selectedTab == tab) Color.Black else Color.Gray,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { selectedTab = tab }
                        .background(
                            if (selectedTab == tab) Color.LightGray else Color.Transparent
                        )
                        .padding(4.dp)
                )
            }
        }

        // Título
        Text(
            text = "Tu Peso (DEMO DATA)",
            fontSize = 20.sp,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Determinar qué datos mostrar según la pestaña seleccionada
        val (weights, labels) = when (selectedTab) {
            "Mes" -> monthlyWeights to monthlyLabels
            "Trimestre" -> quarterlyWeights to quarterlyLabels
            else -> yearlyWeights to yearlyLabels
        }

        // Gráfico principal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val padding = 40.dp.toPx()

                val weightRange = maxWeight - minWeight
                val pixelsPerWeight = (height - padding) / weightRange

                // Líneas horizontales (etiquetas de peso)
                for (weight in minWeight.toInt()..maxWeight.toInt() step 5) {
                    val y = height - ((weight - minWeight) * pixelsPerWeight) - padding
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.3f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 2f
                    )
                }

                // Línea del gráfico
                val path = Path()
                val xStep = width / (weights.size - 1)

                weights.forEachIndexed { index, weight ->
                    val x = index * xStep
                    val y = height - ((weight - minWeight) * pixelsPerWeight) - padding
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }

                    drawCircle(
                        color = Color.Green,
                        radius = 5.dp.toPx(),
                        center = Offset(x, y)
                    )
                }

                drawPath(
                    path = path,
                    color = Color.Green,
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Etiquetas del eje Y (peso)
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(bottom = 20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                for (weight in maxWeight.toInt() downTo minWeight.toInt() step 5) {
                    Text(
                        text = "$weight Kg",
                        fontSize = 12.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(40.dp)
                    )
                }
            }

            // Etiquetas del eje X (meses, semanas o trimestres)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(start = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                labels.forEach { label ->
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Datos adicionales debajo según la pestaña
        when (selectedTab) {
            "Mes" -> {
                // Mostrar datos por días dentro de una semana (supongamos Semana 1)
                val dailyWeights = listOf(88f, 87.8f, 87.6f, 87.5f, 87.4f, 87.3f, 87.2f)
                val dailyLabels = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")

                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = "Detalles de la Semana 1",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        dailyLabels.forEachIndexed { index, day ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = day, fontSize = 12.sp)
                                Text(text = "${dailyWeights[index]} Kg", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            "Trimestre" -> {
                // Mostrar datos por semanas dentro de un mes (supongamos abril)
                val weeklyWeights = listOf(88f, 87.5f, 87f, 86.5f)
                val weeklyLabels = listOf("Sem 1", "Sem 2", "Sem 3", "Sem 4")

                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = "Detalles de Abril",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        weeklyLabels.forEachIndexed { index, week ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = week, fontSize = 12.sp)
                                Text(text = "${weeklyWeights[index]} Kg", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            "Año" -> {
                // No mostramos datos adicionales para "Año"
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Text(
            text = "Tus últimos récords personales y gráficos de progreso se mostrarán aquí después de 5 entrenamientos",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}