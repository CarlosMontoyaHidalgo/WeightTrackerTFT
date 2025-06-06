package com.aronid.weighttrackertft.ui.components.sections.calories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aronid.weighttrackertft.ui.components.charts.barCharts.CaloriesBarChartWithGridLines
import com.aronid.weighttrackertft.ui.components.charts.barCharts.CaloriesLineChartWithGridLines

@Composable
fun CaloriesSection(
    caloriesData: Map<String, Int>,
    totalCalories: Int?,
    goalCalories: Int?,
    averageCalories: Int?,
    isLoading: Boolean,
    rangeType: String
) {
    // Calculamos el promedio semanal adicional SOLO para el rango anual
    val weeklyAverage = remember(caloriesData, rangeType) {
        if (rangeType == "Anual") {
            calculateWeeklyAverage(caloriesData)
        } else {
            null
        }
    }

    var dataError by remember { mutableStateOf<String?>(null) }

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        item {
            // Primera tarjeta (Total)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when (rangeType) {
                            "Diario" -> "Total Diario"
                            "Mensual" -> "Total Mensual"
                            "Anual" -> "Total Anual"
                            else -> "Total Calorías"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${totalCalories ?: "Cargando..."} kcal",
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Fila de tarjetas inferiores
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tarjeta de promedio (original para todos los casos)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = when (rangeType) {
                                "Diario" -> "Promedio Diario"
                                "Mensual" -> "Promedio Mensual"
                                else -> "Promedio"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "${averageCalories ?: "Cargando..."} kcal",
                            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Tarjeta adicional SOLO para anual (promedio semanal)
                if (rangeType == "Anual" && weeklyAverage != null) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Promedio Semanal",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$weeklyAverage kcal",
                                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                } else {
                    // Tarjeta de objetivo (para los demás casos)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Objetivo diario",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${goalCalories ?: "Establece un objetivo de"} kcal",
                                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                when {
                    rangeType == "Anual" -> {
                        CaloriesLineChartWithGridLines(
                            rangeType = rangeType,
                            caloriesData = caloriesData
                        )
                    }

                    else -> {
                        CaloriesBarChartWithGridLines(caloriesData = caloriesData)
                    }
                }
            }
        }
    }
}

private fun calculateWeeklyAverage(caloriesData: Map<String, Int>): Int? {
    if (caloriesData.isEmpty()) return null

    val weeklySums = mutableMapOf<Int, MutableList<Int>>().apply {
        for (week in 1..52) this[week] = mutableListOf()
    }

    caloriesData.forEach { (dayStr, calories) ->
        val dayNumber = dayStr.removePrefix("Día ").toIntOrNull() ?: return@forEach
        if (dayNumber in 1..366) {
            val weekNumber = ((dayNumber - 1) / 7) + 1
            weeklySums[weekNumber]?.add(calories)
        }
    }

    // Cambio clave: No filtrar semanas con 0, incluir todos los datos
    val weeklyAverages = weeklySums.mapValues { (_, values) ->
        if (values.isEmpty()) 0 else values.average().toInt()
    }.values

    return if (weeklyAverages.isEmpty()) 0 else weeklyAverages.average().toInt()
}