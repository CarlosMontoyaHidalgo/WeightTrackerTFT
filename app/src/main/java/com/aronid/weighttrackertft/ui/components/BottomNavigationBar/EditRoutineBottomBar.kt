package com.aronid.weighttrackertft.ui.components.BottomNavigationBar

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.exercises.ExerciseModel

@Composable
fun EditRoutineBottomBar(
    selectedExercises: List<ExerciseModel>,
    onAddAll: () -> Unit,
    onDeselectAll: () -> Unit,
    onExitEditMode: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 8.dp))
            .border(2.dp, Color.LightGray, RoundedCornerShape(topStart = 8.dp))
    ) {

        NavigationBarItem(
            selected = false,
            onClick = { if (selectedExercises.isNotEmpty()) onAddAll() },
            icon = { IconResource(R.drawable.ic_add, tint = Color.Black) },
            label = { Text("Añadir") },
            enabled = selectedExercises.isNotEmpty()
        )

        NavigationBarItem(
            selected = false,
            onClick = { if (selectedExercises.isNotEmpty()) onDeselectAll() },
            icon = { IconResource(R.drawable.ic_x, tint = Color.Black) },
            label = { Text("Deseleccionar") },
            enabled = selectedExercises.isNotEmpty()
        )

        NavigationBarItem(
            selected = false,
            onClick = { onExitEditMode() },
            icon = { IconResource(R.drawable.ic_x, tint = Color.Red) },
            label = { Text("Salir", color = Color.Red) },
            enabled = true
        )
    }
}

@Composable
fun IconResource(id: Int, tint: Color) {
    Icon(
        painter = painterResource(id),
        contentDescription = null,
        tint = tint
    )
}
