package com.aronid.weighttrackertft.ui.components.calendar

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SimpleDateFilterDialog(
    onDateRangeSelected: (Timestamp?, Timestamp?) -> Unit,
    onDismiss: () -> Unit
) {
    var startDateText by remember { mutableStateOf("") }
    var endDateText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtrar por fechas") },
        text = {
            Column {
                OutlinedTextField(
                    value = startDateText,
                    onValueChange = { startDateText = it },
                    label = { Text("Fecha inicial (dd/MM/yyyy)") },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = endDateText,
                    onValueChange = { endDateText = it },
                    label = { Text("Fecha final (dd/MM/yyyy)") },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    try {
                        val startDate = if (startDateText.isNotBlank()) {
                            val date = dateFormat.parse(startDateText)
                            Timestamp(date ?: throw IllegalArgumentException("Fecha inicial inválida"))
                        } else null

                        val endDate = if (endDateText.isNotBlank()) {
                            val date = dateFormat.parse(endDateText)
                            Timestamp(date ?: throw IllegalArgumentException("Fecha final inválida"))
                        } else null

                        if (startDate != null && endDate != null && startDate > endDate) {
                            errorMessage = "La fecha inicial debe ser anterior a la fecha final"
                            return@TextButton
                        }

                        Log.d("SimpleDateFilterDialog", "Selected start: $startDate, end: $endDate")
                        onDateRangeSelected(startDate, endDate)
                        onDismiss()
                    } catch (e: Exception) {
                        errorMessage = "Formato de fecha inválido. Usa dd/MM/yyyy"
                        Log.e("SimpleDateFilterDialog", "Error parsing dates: ${e.message}")
                    }
                }
            ) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}