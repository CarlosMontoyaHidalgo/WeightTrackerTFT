package com.aronid.weighttrackertft.ui.components.alertDialog

import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.platform.LocalContext

@Composable
fun DeleteConfirmationDialog(
    showDialog: Boolean,
    selectedRoutines: SnapshotStateMap<String, Boolean>,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val context = LocalContext.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Eliminar rutinas") },
            text = { Text("¿Eliminar las ${selectedRoutines.size} rutinas seleccionadas?") },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm()
                        Toast.makeText(
                            context,
                            if (selectedRoutines.isEmpty()) "Rutinas eliminadas" else "Error al eliminar",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}