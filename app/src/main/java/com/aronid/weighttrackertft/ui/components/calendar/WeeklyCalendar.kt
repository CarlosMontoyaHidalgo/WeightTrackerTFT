package com.aronid.weighttrackertft.ui.components.calendar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.data.workout.WorkoutModel
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.WeekDayPosition
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun WeeklyWorkoutCalendar(
    workouts: List<WorkoutModel>,
    onDayClick: (LocalDate, List<WorkoutModel>) -> Unit
) {
    val currentDate = remember { LocalDate.now() }
    val firstDayOfWeek = DayOfWeek.MONDAY

    val state = rememberWeekCalendarState(
        startDate = currentDate.minusWeeks(100),
        endDate = currentDate.plusWeeks(100),
        firstVisibleWeekDate = currentDate,
        firstDayOfWeek = firstDayOfWeek
    )

    Column {
        WeekCalendar(
            state = state,
            dayContent = { weekDay ->
                val dayWorkouts = workouts.filter {
                    it.date?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())
                        ?.toLocalDate() == weekDay.date
                }
                Day(
                    day = weekDay,
                    workouts = dayWorkouts,
                    onClick = { onDayClick(weekDay.date, dayWorkouts) },
                    modifier = Modifier
                        .size(width = 75.dp, height = 80.dp)
                        .padding(horizontal = 4.dp)
                        .background(Color.Transparent)
                        .border(4.dp, Color.Black, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun Day(
    day: WeekDay,
    workouts: List<WorkoutModel>,
    onClick: (WeekDay) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(
                enabled = day.position == WeekDayPosition.RangeDate,
                onClick = { onClick(day) }
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            color = if (day.position == WeekDayPosition.RangeDate) Color.Black else Color.Gray,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(50.dp)
                .aspectRatio(1f)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            if (workouts.isNotEmpty()) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val strokeWidth = 4.dp.toPx()
                    val diameter = size.minDimension - strokeWidth
                    val radius = diameter / 2
                    val center = Offset(size.width / 2, size.height / 2)
                    val segmentAngle = 360f / workouts.size

                    workouts.forEachIndexed { index, workout ->
                        val color = when (workout.workoutType) {
                            "legs" -> Color.Red
                            "biceps" -> Color.Blue
                            "triceps" -> Color.Green
                            else -> Color.Gray
                        }
                        drawArc(
                            color = color,
                            startAngle = index * segmentAngle,
                            sweepAngle = segmentAngle,
                            useCenter = false,
                            topLeft = center - Offset(radius, radius),
                            size = Size(diameter, diameter),
                            style = Stroke(width = strokeWidth)
                        )
                    }
                }
            }

            Text(
                text = day.date.dayOfMonth.toString(),
                color = if (day.position == WeekDayPosition.RangeDate) Color.Black else Color.Gray,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}
