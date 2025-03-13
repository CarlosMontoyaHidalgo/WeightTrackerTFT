package com.aronid.weighttrackertft.ui.components.dropdown.weightUnitSelector

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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.R

@Composable
fun WeightUnitSelector(viewModel: WeightUnitSelectorViewModel, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val units = listOf("Kilogramos", "Libras")
    val selectedUnit by viewModel.weightUnit.collectAsState(initial = "Kilogramos")
    var buttonWidth by remember { mutableStateOf(0.dp) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .clickable { expanded = true }
            .padding(12.dp)
            .onSizeChanged { size ->
                buttonWidth = with(Density(1f)) { size.width.toDp() }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = selectedUnit,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_down),
            contentDescription = "Expand unit options",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .shadow(4.dp)
            .width(buttonWidth)
    ) {
        units.forEach { unit ->
            DropdownMenuItem(
                text = { Text(unit, style = MaterialTheme.typography.bodyMedium) },
                onClick = {
                    viewModel.setWeightUnit(unit)
                    expanded = false
                }
            )
        }
    }
}