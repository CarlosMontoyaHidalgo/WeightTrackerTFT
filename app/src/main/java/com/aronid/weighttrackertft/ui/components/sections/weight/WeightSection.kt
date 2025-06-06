package com.aronid.weighttrackertft.ui.components.sections.weight

//import com.aronid.weighttrackertft.ui.components.charts.lineCharts.CombinedWeightCaloriesBMIChart
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aronid.weighttrackertft.ui.components.charts.lineCharts.CombinedWeightCaloriesChart
import com.aronid.weighttrackertft.ui.components.charts.lineCharts.CombinedWeightCaloriesWeeklyChart
import com.aronid.weighttrackertft.utils.formatToSinglePrecision

@Composable
fun WeightSection(
    weightData: Map<String, Double>,
    caloriesData: Map<String, Int>,
    currentWeight: Double?,
    isLoading: Boolean,
    rangeType: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                    text = "Peso Actual",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${currentWeight?.formatToSinglePrecision() ?: "N/A"} kg",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            //CombinedWeightCaloriesBMIChart(weightData = weightData, caloriesData = caloriesData, currentWeight = currentWeight, currentHeight = currentHeight, isLoading = isLoading)
            when {
                rangeType == "Anual" -> {
                    CombinedWeightCaloriesWeeklyChart(
                        weightData = weightData,
                        caloriesData = caloriesData,
                        isLoading = isLoading
                    )
                }

                else -> {
                    CombinedWeightCaloriesChart(
                        weightData = weightData,
                        caloriesData = caloriesData,
                        isLoading = isLoading
                    )

                }
            }
        }
    }
}