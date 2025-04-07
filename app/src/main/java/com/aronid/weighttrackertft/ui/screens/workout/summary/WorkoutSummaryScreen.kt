package com.aronid.weighttrackertft.ui.screens.workout.summary

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import co.yml.charts.axis.AxisData
import co.yml.charts.common.components.Legends
import co.yml.charts.common.model.LegendLabel
import co.yml.charts.common.model.LegendsConfig
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.components.button.NewCustomButton
import com.aronid.weighttrackertft.ui.components.cards.CustomElevatedCard.CustomElevatedCard
import com.aronid.weighttrackertft.ui.components.charts.aaa.RadarChart
import com.aronid.weighttrackertft.ui.components.charts.aaa.RadarData
import com.aronid.weighttrackertft.ui.components.formScreen.FormScreen
import com.aronid.weighttrackertft.ui.components.konfetti.KonfettiComponent
import com.aronid.weighttrackertft.utils.button.ButtonType

@Composable
fun WorkoutSummaryScreen(
    innerPadding: PaddingValues,
    workoutId: String?,
    viewModel: WorkoutSummaryViewModel,
    navHostController: NavHostController
) {
    val calories by viewModel.calories.collectAsState()
    val volume by viewModel.volume.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    val triggerKonfetti = remember { mutableStateOf(false) }
    val allMuscles by viewModel.allMuscles.collectAsState(initial = emptyList())
    val buttonState by viewModel.buttonState.collectAsState()
    val buttonConfigs = buttonState.baseState.buttonConfigs
    val isLoading by viewModel.isLoading.collectAsState(initial = true)

    LaunchedEffect(workoutId) {
        workoutId?.let {
            viewModel.loadWorkoutById(it)
            Log.d("WorkoutSummaryScreen", "Loading workout with ID: $it")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 6.dp
                )
            }
        } else {
            KonfettiComponent(
                modifier = Modifier.fillMaxSize(),
                isActive = triggerKonfetti.value,
                durationMs = 2000L,
                onFinish = { triggerKonfetti.value = false }
            )

            FormScreen(
                modifier = Modifier,
                innerPadding = innerPadding,
                isContentScrolleable = false,
                formContent = {
                    Text(text = saveState.toString())
                    CustomElevatedCard(
                        title = stringResource(R.string.calories_burned),
                        result = calories,
                        unitLabel = stringResource(R.string.kcal_unit),
                        contentColor = Color.Red
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    CustomElevatedCard(
                        iconResource = Icons.Filled.FitnessCenter,
                        title = stringResource(R.string.volume),
                        result = volume,
                        unitLabel = stringResource(R.string.kg_unit),
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (allMuscles.isNotEmpty()) {
                            val pagerState = rememberPagerState(pageCount = { 3 })
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Músculos trabajados",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 16.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                // Leyenda para primarios y secundarios
                                Legends(
                                    legendsConfig = LegendsConfig(
                                        legendLabelList = listOf(
                                            LegendLabel(Color.Green, "Primarios"),
                                            LegendLabel(Color.Blue, "Secundarios")
                                        ),
                                        gridColumnCount = 2,
                                        textSize = 14.sp,
                                        colorBoxSize = 20.dp
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier.fillMaxSize()
                                ) { page ->
                                    when (page) {
                                        0 -> MuscleBarList(allMuscles)
                                        1 -> MuscleRadarChart(allMuscles)
                                        2 -> MuscleVerticalBarChart(allMuscles)
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = stringResource(R.string.no_data_available),
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                },
                formButton = {
                    NewCustomButton(
                        text = stringResource(R.string.close),
                        onClick = { navHostController.navigate(NavigationRoutes.Home.route) },
                        buttonType = ButtonType.FILLED,
                        containerColor = Color.Black,
                        textConfig = buttonConfigs.textConfig,
                        layoutConfig = buttonConfigs.layoutConfig,
                        stateConfig = buttonConfigs.stateConfig,
                        borderConfig = buttonConfigs.borderConfig
                    )
                }
            )
        }
    }
}

@Composable
fun MuscleBarList(muscles: List<Pair<String, Float>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        muscles.forEach { (muscle, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = muscle,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(0.3f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .height(16.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(value / 100f)
                            .background(
                                if (value == 100f) Color.Green else Color.Blue,
                                RoundedCornerShape(8.dp)
                            )
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${value.toInt()}%",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (value == 100f) Color.Green else Color.Blue,
                    fontWeight = if (value == 100f) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.weight(0.2f)
                )
            }
        }
    }
}

@Composable
fun MuscleRadarChart(muscles: List<Pair<String, Float>>) {
    val labels = muscles.map { it.first }
    val values = muscles.map { it.second }
    val adjustedLabels = if (labels.size < 3) labels + List(3 - labels.size) { "Otros" } else labels
    val adjustedValues = if (values.size < 3) values + List(3 - values.size) { 0f } else values

    RadarChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .padding(16.dp),
        data = RadarData(
            labels = adjustedLabels,
            values = adjustedValues,
            maxValue = 100f
        ),
        gridColor = Color.LightGray,
        dataColor = Color.Green,
        fillColor = Color.Green.copy(alpha = 0.2f),
        labelColor = Color.DarkGray,
        labelDistanceFactor = 1.3f
    )
}

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