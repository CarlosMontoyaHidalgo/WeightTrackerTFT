package com.aronid.weighttrackertft.ui.components.button.types

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.R

@Preview
@Composable
fun PreviewBackButton() {
    BackButton(
        onClick = {},
        modifier = Modifier,
        shape = RoundedCornerShape(8.dp),
        containerColor = Color.Transparent,
        contentColor = Color.White
    )
}


@Composable
fun BackButton(
    iconId: Int = R.drawable.ic_back,
    onClick: () -> Unit = { },
    modifier: Modifier,
    shape: Shape,
    containerColor: Color,
    contentColor: Color,
    iconSize: Dp = 40.dp,
) {

    OutlinedButton(
        onClick = {
            onClick()
        },
        modifier = modifier,
        enabled = true,
        elevation = null,
        shape = shape,
        border = null,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContentColor = Color.Gray,
            disabledContainerColor = Color.Gray
        ),
        content = {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = "Back",
                modifier = Modifier
                    .size(iconSize)
                    .fillMaxSize(),
                tint = contentColor
            )
        },
    )
}