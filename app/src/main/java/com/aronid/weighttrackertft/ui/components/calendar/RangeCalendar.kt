package com.aronid.weighttrackertft.ui.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

//@Composable
//fun WorkoutRangeCalendar(viewModel: CalendarViewModel, onDateRangeSelected: (LocalDate, LocalDate) -> Unit, onDismiss: () -> Unit) {
//    val accountCreationDate by viewModel.accountCreationDate.collectAsState()
//    val currentDate = LocalDate.now()
//
//    val startDate =
//        accountCreationDate?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
//            ?: currentDate.minusYears(1)
//
//    val startMonth = YearMonth.from(startDate)
//    val endMonth = YearMonth.now().plusMonths(12)
//
//    val currentMonth = remember { YearMonth.now() }
//    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }
//
//    val state = rememberCalendarState(
//        startMonth = startMonth,
//        endMonth = endMonth,
//        firstVisibleMonth = currentMonth,
//        firstDayOfWeek = firstDayOfWeek
//    )
//
//    var selectedStart by remember { mutableStateOf<CalendarDay?>(null) }
//    var selectedEnd by remember { mutableStateOf<CalendarDay?>(null) }
//
//    HorizontalCalendar(
//        state = state,
//        dayContent = { day ->
//            val start = selectedStart
//            val end = selectedEnd
//            val isStart = start == day
//            val isEnd = end == day
//            val isInRange = start != null && end != null &&
//                    !day.date.isBefore(start.date) && !day.date.isAfter(end.date)
//            val isDisabled =
//                day.date.isBefore(startDate)
//
//            DayContent(
//                day = day,
//                isStart = isStart,
//                isEnd = isEnd,
//                isInRange = isInRange,
//                isDisabled = isDisabled,
//                onClick = {
//                    if (!isDisabled) {
//                        val currentStart = selectedStart
//                        val currentEnd = selectedEnd
//                        if (currentStart == null) {
//                            selectedStart = day
//                            selectedEnd = null
//                        } else if (currentEnd == null) {
//                            if (day.date.isAfter(currentStart.date)) {
//                                selectedEnd = day
//                            } else {
//                                selectedStart = day
//                                selectedEnd = null
//                            }
//                        } else {
//                            selectedStart = day
//                            selectedEnd = null
//                        }
//                    }
//                }
//            )
//        },
//        monthHeader = { month ->
//            MonthHeader(month.yearMonth)
//        }
//    )
//}
//
@Composable
fun DayContent(
    day: CalendarDay,
    isStart: Boolean,
    isEnd: Boolean,
    isInRange: Boolean,
    isDisabled: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isDisabled -> Color.LightGray
        isStart || isEnd -> MaterialTheme.colorScheme.primary
        isInRange -> Color(0x0F7492F3)
        else -> Color.Transparent
    }

    val textColor = when {
        isDisabled -> Color.Gray
        isStart || isEnd -> Color.White
        isInRange -> Color.Black
        else -> Color.Black
    }

    val shape = when {
        isStart -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
        isEnd -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
        else -> RoundedCornerShape(0.dp)
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = if (isStart || isEnd) Color.Black else Color.Transparent,
                shape = shape
            )
            .clickable(enabled = !isDisabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = textColor
        )
    }
}

@Composable
fun MonthHeader(yearMonth: YearMonth) {
    Text(
        text = yearMonth.month.getDisplayName(
            TextStyle.FULL,
            Locale.getDefault()
        ) + " " + yearMonth.year,
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun WorkoutRangeCalendar(
    viewModel: CalendarViewModel,
    onDateRangeSelected: (Timestamp?, Timestamp?) -> Unit,
    onDismiss: () -> Unit
) {
    val accountCreationDate by viewModel.accountCreationDate.collectAsState()
    val currentDate = LocalDate.now()

    val startDate = accountCreationDate?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
        ?: currentDate.minusYears(1)

    val startMonth = YearMonth.from(startDate)
    val endMonth = YearMonth.now().plusMonths(12)

    val currentMonth = remember { YearMonth.now() }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )

    var selectedStart by remember { mutableStateOf<CalendarDay?>(null) }
    var selectedEnd by remember { mutableStateOf<CalendarDay?>(null) }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Date Range") },
        text = {
            Box(modifier = Modifier.height(400.dp)) {
                HorizontalCalendar(
                    state = state,
                    dayContent = { day ->
                        val start = selectedStart
                        val end = selectedEnd
                        val isStart = start == day
                        val isEnd = end == day
                        val isSingle = isStart && end == null
                        val isInRange = start != null && end != null &&
                                !day.date.isBefore(start.date) && !day.date.isAfter(end.date)
                        val isDisabled = day.date.isBefore(startDate)

                        DayContent(
                            day = day,
                            isStart = isStart,
                            isEnd = isEnd,
                            isSingle = isSingle,
                            isInRange = isInRange,
                            isDisabled = isDisabled,
                            onClick = {
                                if (!isDisabled) {
                                    val currentStart = selectedStart
                                    val currentEnd = selectedEnd
                                    if (currentStart == null) {
                                        // First selection
                                        selectedStart = day
                                        selectedEnd = null
                                    } else if (currentEnd == null) {
                                        if (day == currentStart) {
                                            // Keep as single day selection
                                            selectedEnd = null
                                        } else if (day.date.isAfter(currentStart.date)) {
                                            // Set as range end
                                            selectedEnd = day
                                        } else {
                                            // New start before current start
                                            selectedStart = day
                                            selectedEnd = null
                                        }
                                    } else {
                                        // Reset to new single selection
                                        selectedStart = day
                                        selectedEnd = null
                                    }
                                }
                            }
                        )
                    },
                    monthHeader = { month -> MonthHeader(month.yearMonth) }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val startTimestamp = selectedStart?.date?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.let { Timestamp(Date.from(it)) }
                    val endTimestamp = selectedEnd?.date?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.let { Timestamp(Date.from(it)) }
                    onDateRangeSelected(startTimestamp, endTimestamp ?: startTimestamp) // Use startTimestamp if end is null
                    onDismiss()
                },
                enabled = selectedStart != null
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DayContent(
    day: CalendarDay,
    isStart: Boolean,
    isEnd: Boolean,
    isSingle: Boolean,
    isInRange: Boolean,
    isDisabled: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isDisabled -> Color.LightGray
        isSingle -> MaterialTheme.colorScheme.primary
        isStart || isEnd -> MaterialTheme.colorScheme.primary
        isInRange -> Color(0x0F7492F3)
        else -> Color.Transparent
    }

    val textColor = when {
        isDisabled -> Color.Gray
        isSingle -> Color.White
        isStart || isEnd -> Color.White
        isInRange -> Color.Black
        else -> Color.Black
    }

    val shape = when {
        isSingle -> CircleShape
        isStart -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
        isEnd -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
        else -> RoundedCornerShape(0.dp)
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = if (isStart || isEnd || isSingle) Color.Black else Color.Transparent,
                shape = shape
            )
            .clickable(enabled = !isDisabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = textColor
        )
    }
}