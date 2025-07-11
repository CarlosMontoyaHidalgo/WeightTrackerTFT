package com.aronid.weighttrackertft.ui.components.series.row

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SeriesRow(
    seriesNumber: Int,
    weight: String,
    reps: String,
    isCompleted: Boolean,
    requiresWeight: Boolean,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onToggleCompleted: () -> Unit,
    previousWeight: String = "",
    previousReps: String = ""
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Serie $seriesNumber",
            modifier = Modifier.weight(1f)
        )
        if (requiresWeight) {
            OutlinedTextField(
                value = weight,
                onValueChange = { newValue ->
                    onWeightChange(newValue)
                },
                label = {
                    Text(
                        if (previousWeight.isNotEmpty()) "KG (prev $previousWeight)"
                        else "KG"
                    )
                },
                modifier = Modifier.weight(1f),
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        OutlinedTextField(
            value = reps,
            onValueChange = { newValue ->
                onRepsChange(newValue)
            },
            label = {
                Text(
                    if (previousReps.isNotEmpty()) "Reps (prev $previousReps)"
                    else "Reps"
                )
            },
            modifier = Modifier.weight(1f),
            maxLines = 1,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Checkbox(
            checked = isCompleted,
            onCheckedChange = {
                if ((!requiresWeight || !weight.isNullOrEmpty()) && !reps.isNullOrEmpty())
                    onToggleCompleted()
            },
            enabled = (!requiresWeight || !weight.isNullOrEmpty()) && !reps.isNullOrEmpty()
        )
    }
}

@Preview
@Composable
fun SeriesRowPreview() {
    SeriesRow(
        seriesNumber = 1,
        weight = "",
        reps = "",
        isCompleted = false,
        onWeightChange = {},
        onRepsChange = {},
        onToggleCompleted = {},
        requiresWeight = true
    )
}

@Preview(name = "No reps")
@Composable
fun SeriesRowPreviewNoReps() {
    SeriesRow(
        seriesNumber = 1,
        weight = "",
        reps = "",
        isCompleted = false,
        onWeightChange = {},
        onRepsChange = {},
        onToggleCompleted = {},
        requiresWeight = false
    )
}

@Preview(name = "Con datos previos")
@Composable
fun SeriesRowWithHistoryPreview() {
    SeriesRow(
        seriesNumber = 1,
        weight = "60",
        reps = "12",
        previousWeight = "55",
        previousReps = "10",
        isCompleted = false,
        onWeightChange = {},
        onRepsChange = {},
        onToggleCompleted = {},
        requiresWeight = true
    )
}

@Preview(name = "Sin datos previos")
@Composable
fun SeriesRowWithoutHistoryPreview() {
    SeriesRow(
        seriesNumber = 1,
        weight = "",
        reps = "",
        isCompleted = false,
        onWeightChange = {},
        onRepsChange = {},
        onToggleCompleted = {},
        requiresWeight = true
    )
}