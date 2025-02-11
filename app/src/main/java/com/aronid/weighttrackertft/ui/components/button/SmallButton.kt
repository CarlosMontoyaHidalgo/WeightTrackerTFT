package com.aronid.weighttrackertft.ui.components.button

import androidx.compose.foundation.Image
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

@Composable
fun SmallButton(
    imageId: Int,
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    Button(onClick = onClick) {
        Image(painterResource(id = imageId), contentDescription = null)
    }
}