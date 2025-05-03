package com.aronid.weighttrackertft.ui.components.searchBar.workouts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.aronid.weighttrackertft.R

@Composable
fun WorkoutTopBar(
    selectedWorkouts: SnapshotStateMap<String, Boolean>,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onFilterClick: () -> Unit,
    onClearFilterClick: () -> Unit,
    onDownloadClick: (() -> Unit)? = null,
    showCheckbox: Boolean
) {
    ConstraintLayout(Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {
        val (titleRef, editRef, downloadRef, deleteRef, filterRef) = createRefs()

        Text(
            text = "Workouts",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.constrainAs(titleRef) {
                start.linkTo(parent.start, margin = 16.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
        )

        // Botón de Editar (último a la derecha)
        Button(
            onClick = onEditClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.constrainAs(editRef) {
                end.linkTo(parent.end, margin = 8.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
        ) {
            Icon(
                imageVector = if (showCheckbox) Icons.Default.Done else Icons.Default.Edit,
                contentDescription = "Editar",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Botón de Descargar (solo visible si hay selección y callback no es null)
        if (showCheckbox && selectedWorkouts.values.any { it } && onDownloadClick != null) {
            Button(
                onClick = onDownloadClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.constrainAs(downloadRef) {
                    end.linkTo(editRef.start, margin = 4.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_download),
                    contentDescription = "Descargar",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Botón de Eliminar (si hay seleccionados)
        if (showCheckbox && selectedWorkouts.values.any { it }) {
            Button(
                onClick = onDeleteClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Red
                ),
                modifier = Modifier.constrainAs(deleteRef) {
                    end.linkTo(
                        if (onDownloadClick != null) downloadRef.start else editRef.start,
                        margin = 4.dp
                    )
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_trash),
                    contentDescription = "Eliminar",
                    tint = Color.Red
                )
            }
        }

        // Botón de Filtrar
        Button(
            onClick = onFilterClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.constrainAs(filterRef) {
                end.linkTo(
                    if (showCheckbox && selectedWorkouts.values.any { it }) deleteRef.start else editRef.start,
                    margin = 4.dp
                )
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Filtrar por fecha",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

}