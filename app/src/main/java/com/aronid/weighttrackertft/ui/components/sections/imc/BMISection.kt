package com.aronid.weighttrackertft.ui.components.sections.imc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.pow

@Composable
fun BMISection(currentHeight: Double?, currentWeight: Double?) {
    val bmi = remember(currentHeight, currentWeight) {
        currentHeight?.let { h ->
            currentWeight?.let { w ->
                w / ((h / 100).pow(2))
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),

        ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Tu IMC", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = bmi?.let { "%.1f".format(it) } ?: "--",
                style = MaterialTheme.typography.displayMedium
            )

            Text(
                text = when {
                    bmi == null -> "Ingresa peso y altura"
                    bmi < 18.5 -> "Bajo peso"
                    bmi < 25 -> "Normal"
                    bmi < 30 -> "Sobrepeso"
                    else -> "Obesidad"
                },
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Con deporte y constancia verás mejoras",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}