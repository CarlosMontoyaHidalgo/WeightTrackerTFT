package com.aronid.weighttrackertft.ui.components.charts.barList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


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