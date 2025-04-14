package com.aronid.weighttrackertft.ui.components.charts.examples

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.yml.charts.common.components.Legends
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig

@Composable
fun PieChartExample() {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                modifier = Modifier.padding(12.dp),
                text = "Simple Pie Chart",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            SimplePiechart(context)
            Spacer(modifier = Modifier.height(20.dp))
        }
        item {
            Text(
                modifier = Modifier.padding(12.dp),
                text = "Pie Chart with Slice Labels",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            PiechartWithSliceLables(context)
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SimplePiechart(context: Context) {
    val pieChartData = DataUtils.getPieChartData()
    val pieChartConfig = PieChartConfig(
        labelVisible = true,
        activeSliceAlpha = 0.9f,
        isEllipsizeEnabled = true,
        sliceLabelEllipsizeAt = TextUtils.TruncateAt.MIDDLE,
        isAnimationEnable = true,
        chartPadding = 30,
        backgroundColor = Color.Black,
        showSliceLabels = false,
        animationDuration = 1500
    )
    Column(modifier = Modifier.height(500.dp)) {
        Spacer(modifier = Modifier.height(20.dp))
        Legends(legendsConfig = DataUtils.getLegendsConfigFromPieChartData(pieChartData, 3))
        PieChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            pieChartData = pieChartData,
            pieChartConfig = pieChartConfig
        ) { slice ->
            Toast.makeText(context, slice.label, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
private fun PiechartWithSliceLables(context: Context) {
    val pieChartData = DataUtils.getPieChartData2()
    val pieChartConfig = PieChartConfig(
        activeSliceAlpha = 0.9f,
        isEllipsizeEnabled = true,
        sliceLabelEllipsizeAt = TextUtils.TruncateAt.MIDDLE,
        sliceLabelTypeface = Typeface.defaultFromStyle(Typeface.ITALIC),
        isAnimationEnable = true,
        chartPadding = 20,
        showSliceLabels = true,
        labelVisible = true
    )
    Column(modifier = Modifier.height(500.dp)) {
        Legends(legendsConfig = DataUtils.getLegendsConfigFromPieChartData(pieChartData, 3))
        PieChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            pieChartData = pieChartData,
            pieChartConfig = pieChartConfig
        ) { slice ->
            Toast.makeText(context, slice.label, Toast.LENGTH_SHORT).show()
        }
    }
}
