package com.aronid.weighttrackertft.ui.components.exercise.exerciseProgressIndicator

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun ExerciseProgressIndicator(
    totalExercises: Int,
    currentExerciseIndex: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(24.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalExercises) { index ->
            val isActive = index == currentExerciseIndex
            val height by animateDpAsState(
                targetValue = if (isActive) 8.dp else 4.dp,
                animationSpec = tween(durationMillis = 300)
            )

            val color by animateColorAsState(
                targetValue = if (isActive) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                },
                animationSpec = tween(durationMillis = 300)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 2.dp)
                    .height(height)
                    .background(
                        color = color,
                        shape = RoundedCornerShape(50)
                    )
//                    .animateContentSize()
            )
        }
    }
}