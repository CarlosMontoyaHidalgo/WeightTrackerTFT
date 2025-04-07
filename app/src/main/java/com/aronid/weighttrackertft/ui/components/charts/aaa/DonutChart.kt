package com.aronid.weighttrackertft.ui.components.charts.aaa

import android.content.Context
import android.graphics.Typeface
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.common.components.Legends
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.utils.proportion

@Composable
fun DonutChartExample() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        SimpleDonutChart(context)
        Spacer(modifier = Modifier.height(32.dp))
        MultipleSmallDonutCharts(context)
    }
}

@Composable
private fun SimpleDonutChart(context: Context) {
    val data = DataUtils.getDonutChartData()
    val sumOfValues = data.totalLength
    val proportions = data.slices.proportion(sumOfValues)

    val pieChartConfig = PieChartConfig(
        labelVisible = true,
        strokeWidth = 120f,
        labelColor = Color.Black,
        activeSliceAlpha = 0.9f,
        isEllipsizeEnabled = true,
        labelTypeface = Typeface.defaultFromStyle(Typeface.BOLD),
        isAnimationEnable = true,
        chartPadding = 25,
        labelFontSize = 42.sp
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
    ) {
        Legends(legendsConfig = DataUtils.getLegendsConfigFromPieChartData(pieChartData = data, 3))
        DonutPieChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            pieChartData = data,
            pieChartConfig = pieChartConfig
        ) { slice ->
            Toast.makeText(context, slice.label, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
private fun MultipleSmallDonutCharts(context: Context) {
    val data = DataUtils.getDonutChartData()
    val sumOfValues = data.totalLength
    val proportions = data.slices.proportion(sumOfValues)

    val firstPieChartConfig = PieChartConfig(
        labelVisible = true,
        strokeWidth = 50f,
        labelColor = Color.Black,
        backgroundColor = Color.Yellow,
        activeSliceAlpha = 0.9f,
        isEllipsizeEnabled = true,
        labelTypeface = Typeface.defaultFromStyle(Typeface.BOLD),
        isAnimationEnable = true,
        chartPadding = 25,
        labelFontSize = 16.sp
    )

    val secondPieChartConfig = PieChartConfig(
        labelVisible = true,
        strokeWidth = 50f,
        labelColor = Color.Black,
        activeSliceAlpha = 0.9f,
        isEllipsizeEnabled = true,
        backgroundColor = Color.Black,
        labelTypeface = Typeface.defaultFromStyle(Typeface.BOLD),
        isAnimationEnable = true,
        chartPadding = 25,
        labelFontSize = 16.sp,
        isSumVisible = true,
        sumUnit = "unit",
        labelColorType = PieChartConfig.LabelColorType.SLICE_COLOR,
        labelType = PieChartConfig.LabelType.VALUE
    )

    val thirdPieChartConfig = PieChartConfig(
        labelVisible = true,
        strokeWidth = 50f,
        labelColor = Color.Black,
        activeSliceAlpha = 0.9f,
        backgroundColor = Color.LightGray,
        isEllipsizeEnabled = true,
        labelTypeface = Typeface.defaultFromStyle(Typeface.BOLD),
        isAnimationEnable = true,
        chartPadding = 25,
        labelFontSize = 16.sp
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        Legends(legendsConfig = DataUtils.getLegendsConfigFromPieChartData(pieChartData = data, 3))
        Spacer(modifier = Modifier.height(20.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            items(3) { index ->
                val config = when (index) {
                    0 -> firstPieChartConfig
                    1 -> secondPieChartConfig
                    else -> thirdPieChartConfig
                }
                DonutPieChart(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp),
                    pieChartData = data,
                    pieChartConfig = config
                ) { slice ->
                    Toast.makeText(context, slice.label, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DonutChartPreview() {
    DonutChartExample()
}