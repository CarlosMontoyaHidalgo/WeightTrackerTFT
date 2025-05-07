package com.aronid.weighttrackertft.ui.components.calendar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.data.workout.WorkoutModel
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WorkoutCalendar(
    workouts: List<WorkoutModel>,
    onDayClick: (LocalDate, List<WorkoutModel>) -> Unit,
    viewModel: CalendarViewModel,
    locale: Locale = Locale.getDefault()
) {
    val currentDate = remember { LocalDate.now() }
    val firstDayOfWeek = DayOfWeek.MONDAY
    val daysOfWeek = remember { daysOfWeek(firstDayOfWeek) }

    val accountCreationDate by viewModel.accountCreationDate.collectAsState()
    val startDate =
        accountCreationDate?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
            ?: currentDate.minusYears(1)

    val startMonth = remember { YearMonth.from(maxOf(startDate, currentDate.minusMonths(1))) }
    val endMonth = remember { YearMonth.now().plusMonths(100) }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = YearMonth.from(maxOf(startDate, currentDate)),
        firstDayOfWeek = firstDayOfWeek
    )

    Column {
        HorizontalCalendar(
            state = state,
            dayContent = { day ->
                val dayWorkouts = workouts.filter {
                    val workoutDate = it.date?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())
                        ?.toLocalDate()
                    workoutDate != null && !workoutDate.isBefore(startDate) && workoutDate == day.date
                }
                WorkoutDay(
                    day = day,
                    workouts = dayWorkouts,
                    onClick = { onDayClick(day.date, dayWorkouts) }
                )
            },
            monthHeader = { calendarMonth ->
                MonthHeader(
                    month = calendarMonth.yearMonth.month,
                    year = calendarMonth.yearMonth.year,
                    daysOfWeek = daysOfWeek,
                    locale = locale,
                    showArrows = accountCreationDate != null,
                    state = state // Pass the state to MonthHeader
                )
            }
        )
    }
}

fun maxOf(date1: LocalDate, date2: LocalDate): LocalDate =
    if (date1.isAfter(date2)) date1 else date2

@Composable
fun WorkoutDay(day: CalendarDay, workouts: List<WorkoutModel>, onClick: (CalendarDay) -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(
                enabled = day.position == DayPosition.MonthDate,
                onClick = { onClick(day) }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (workouts.isNotEmpty()) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val strokeWidth = 4.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val center = Offset(size.width / 2, size.height / 2)
                val segmentAngle = 360f / workouts.size

                workouts.forEachIndexed { index, workout ->
                    val color = when (workout.workoutType.lowercase()) {
                        "mixto" -> Color(0xFF26549A)
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
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth)
                    )
                }
            }
        }

        Text(
            text = day.date.dayOfMonth.toString(),
            color = if (day.position == DayPosition.MonthDate) Color.Black else Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun MonthHeader(
    month: java.time.Month,
    year: Int,
    daysOfWeek: List<DayOfWeek>,
    modifier: Modifier = Modifier,
    locale: Locale,
    showArrows: Boolean,
    state: com.kizitonwose.calendar.compose.CalendarState // Added state parameter
) {
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showArrows) {
                IconButton(onClick = {
                    coroutineScope.launch {
                        state.scrollToMonth(YearMonth.of(year, month).minusMonths(1))
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowLeft,
                        contentDescription = "Previous month",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                Box(modifier = Modifier.padding(start = 16.dp)) // Placeholder to maintain layout
            }
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                val monthName = month.getDisplayName(TextStyle.FULL, locale)
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
                Text(
                    text = "$monthName $year",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
            }
            if (showArrows) {
                IconButton(onClick = {
                    coroutineScope.launch {
                        state.scrollToMonth(YearMonth.of(year, month).plusMonths(1))
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                        contentDescription = "Next month",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                Box(modifier = Modifier.padding(end = 16.dp)) // Placeholder to maintain layout
            }
        }
        MonthDaysOfWeekTitle(daysOfWeek = daysOfWeek, locale = locale)
    }
}

@Composable
private fun MonthDaysOfWeekTitle(daysOfWeek: List<DayOfWeek>, locale: Locale) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            val dayName = dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayName,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}