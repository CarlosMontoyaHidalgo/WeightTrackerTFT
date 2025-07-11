package com.aronid.weighttrackertft.ui.components.dropdown.weightReminder

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.R

@Composable
fun WeightReminderSelector(
    viewModel: WeightReminderViewModel,
    modifier: Modifier = Modifier
) {
    val reminderInterval by viewModel.reminderInterval.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(
        R.string.daily to 1,
        R.string.every_3_days to 3,
        R.string.weekly to 7,
        R.string.every_2_weeks to 14,
        R.string.every_month to 30
    )
    var buttonWidth by remember { mutableStateOf(0.dp) }


    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { expanded = true }
            .padding(12.dp)
            .onSizeChanged { size ->
                buttonWidth = with(Density(1f)) { size.width.toDp() }
            }
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource( when (reminderInterval) {
                1 -> R.string.daily
                3 -> R.string.every_3_days
                7 -> R.string.weekly
                14 -> R.string.every_2_weeks
                30 -> R.string.every_month
                else -> R.string.weekly
            }),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_down),
            contentDescription = "Expand reminder options",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp)
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .width(buttonWidth)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .padding(vertical = 4.dp)
    ) {
        options.forEach { (label, days) ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                },
                onClick = {
                    viewModel.setReminderInterval(days)
                    expanded = false
                },
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .fillMaxWidth()
            )
        }
    }
}