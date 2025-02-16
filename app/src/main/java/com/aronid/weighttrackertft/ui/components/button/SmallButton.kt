package com.aronid.weighttrackertft.ui.components.button

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.R

@Composable
fun SmallButton(
    modifier: Modifier? = Modifier,
    imageId: Int,
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
    Button(onClick = onClick) {
        Image(painterResource(id = imageId), contentDescription = null)
    }
}
@Composable
fun CustomSmallIconButton(
    onClick: () -> Unit,
    iconSize: Dp = 24.dp,
    buttonSize: Dp = 40.dp,
    iconResId: Int = R.drawable.ic_launcher_foreground,
    contentDescription: String? = "Small Icon",
    iconTint: Color = Color.White,
    backgroundColor: Color = Color.Blue // Color de fondo (puedes cambiarlo a tu gusto)
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(buttonSize)
            .background(color = backgroundColor, shape = CircleShape)
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(iconSize)
        )
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewCustomSmallIconButton() {
    CustomSmallIconButton(onClick = { /* Acción de prueba */ })
}