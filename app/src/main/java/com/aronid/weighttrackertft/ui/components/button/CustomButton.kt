package com.aronid.weighttrackertft.ui.components.button

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.ui.theme.Black
import com.aronid.weighttrackertft.ui.theme.White

@Composable
fun CustomButton(
    text: String? = null,
    containerColor: Color = White,
    textColor: Color = Black,
    imageId: Int? = null,
    borderColor: Color? = null,
    borderWidth: Int = 1,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val buttonModifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 32.dp, vertical = 8.dp)
    val finalModifier = if (borderColor != null) {
        buttonModifier.then(
            Modifier.border(
                width = borderWidth.dp,
                color = borderColor,
                shape = MaterialTheme.shapes.extraLarge
            )
        )
    } else {
        buttonModifier
    }

    Button(
        onClick = onClick, modifier = finalModifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if(enabled) containerColor else Color.Gray,
            contentColor = if (enabled) textColor else Color.LightGray
        ),
        enabled = enabled
    ) {
        if (imageId != null) {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        if (!text.isNullOrEmpty()) {
            Text(text = text, color = textColor, modifier = Modifier.padding(8.dp))

        }
    }
}