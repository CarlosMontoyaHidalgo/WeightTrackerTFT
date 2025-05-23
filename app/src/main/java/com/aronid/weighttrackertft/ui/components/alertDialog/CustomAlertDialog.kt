package com.aronid.weighttrackertft.ui.components.alertDialog

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.aronid.weighttrackertft.ui.components.button.MyElevatedButton

@Composable
fun CustomAlertDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    text: String,
    confirmButtonText: String,
    dismissButtonText: String,
    imageUrl: String? = null,
    customContent: (@Composable () -> Unit)? = null,
    @DrawableRes imageResId: Int? = null,  // Cambiado a resource ID
) {
    Log.d("CustomAlertDialog", "showDialog: $imageResId")
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = MaterialTheme.colorScheme.background,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            },
            text = {
                Column {
                    Text(text = text, color = MaterialTheme.colorScheme.onBackground)
                    /*if (imageUrl != null) {
                        Image(
                            painter = painterResource(/*imageUrl.toInt()*/ R.drawable.background),
                            contentDescription = ""
                        )
                    }*/

                    if (imageResId != null) {
                        Image(
                            painter = painterResource(imageResId),
                            contentDescription = title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    customContent?.invoke()
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(confirmButtonText)
                }
            },
            dismissButton = {
                MyElevatedButton(
                    text = dismissButtonText,
                    onClick = onDismiss,
                )
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        )
    }
}
