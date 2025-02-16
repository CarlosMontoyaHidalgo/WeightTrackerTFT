package com.aronid.weighttrackertft.ui.components.button

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.utils.button.ButtonType
import com.aronid.weighttrackertft.utils.button.IconPosition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.utils.button.BorderConfig
import com.aronid.weighttrackertft.utils.button.IconConfig
import com.aronid.weighttrackertft.utils.button.LayoutConfig
import com.aronid.weighttrackertft.utils.button.StateConfig
import com.aronid.weighttrackertft.utils.button.TextConfig

@Composable
fun NewCustomButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    onClick: () -> Unit,
    buttonType: ButtonType,
    containerColor: Color,
    textConfig: TextConfig? = null,
    iconConfig: IconConfig? = null,
    layoutConfig: LayoutConfig,
    borderConfig: BorderConfig? = null,
    stateConfig: StateConfig,
) {
    val textColorOptionBlack = textConfig?.textColor ?: Color.Black
    val textColorOptionWhite = textConfig?.textColor ?: Color.White

    val baseModifier = if (layoutConfig.width != null) {
        Modifier.size(layoutConfig.width, layoutConfig.height)
    } else {
        Modifier
            .fillMaxWidth()
            .height(layoutConfig.height)
    }

    val buttonModifier = modifier
        .then(baseModifier).padding(layoutConfig.contentPadding)


    val content: @Composable () -> Unit = {
        if (stateConfig.isLoading) {
            CircularProgressIndicator(color = textColorOptionBlack)
        } else {
            ButtonContent(
                text = text,
                iconConfig = iconConfig,
                textSize = textConfig?.textSize ?: 16.sp,
                fontWeight = textConfig?.fontWeight ?: FontWeight.Bold
            )
        }
    }

    when (buttonType) {
        ButtonType.FILLED -> {
            Button(
                onClick = onClick,
                enabled = stateConfig.isEnabled,
                shape = borderConfig?.buttonShape ?: RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = textColorOptionWhite
                ),
                modifier = buttonModifier
            ) { content() }
        }

        ButtonType.TONAL_FILLED -> {
            FilledTonalButton(
                onClick = onClick,
                enabled = stateConfig.isEnabled,
                shape = borderConfig?.buttonShape ?: RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = textColorOptionWhite
                ), modifier = buttonModifier
            ) { content() }
        }

        ButtonType.ELEVATED -> {
            ElevatedButton(
                onClick = onClick,
                enabled = stateConfig.isEnabled,
                shape = borderConfig?.buttonShape ?: RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = textColorOptionWhite
                ),
                modifier = buttonModifier
            ) { content() }
        }

        ButtonType.OUTLINED -> {
            OutlinedButton(
                onClick = onClick,
                enabled = stateConfig.isEnabled,
                shape = borderConfig?.buttonShape ?: RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = textColorOptionWhite
                ),
                modifier = buttonModifier,
            ) { content() }
        }

        ButtonType.TEXT -> {
            TextButton(
                onClick = onClick,
                enabled = stateConfig.isEnabled,
                shape = borderConfig?.buttonShape ?: RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = textColorOptionWhite
                ),
                modifier = buttonModifier
            ) { content() }
        }
    }
}

@Composable
private fun ButtonContent(
    text: String?,
    iconConfig: IconConfig?,
    textSize: TextUnit,
    fontWeight: FontWeight
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        iconConfig?.let {
            if (text == null || it.iconPosition == IconPosition.START) {
                Image(
                    painter = painterResource(id = it.iconId),
                    contentDescription = it.iconContentDescription,
                    modifier = Modifier.size(it.iconSize),
                    colorFilter = it.iconTint?.let { tint ->
                        androidx.compose.ui.graphics.ColorFilter.tint(
                            tint
                        )
                    }
                )
            }
        }

        if (text != null) {
            Text(text = text, fontSize = textSize, fontWeight = fontWeight)
        }

       

        iconConfig?.let {
            if (text != null && it.iconPosition == IconPosition.END) {
                Spacer(modifier = Modifier.width(it.iconSpacing))
                Image(
                    painter = painterResource(id = it.iconId),
                    contentDescription = it.iconContentDescription,
                    modifier = Modifier.size(it.iconSize),
                    colorFilter = it.iconTint?.let { tint ->
                        androidx.compose.ui.graphics.ColorFilter.tint(
                            tint
                        )
                    }
                )
            }
        }

    }
}


/*Ejemplos de uso*/
@Preview(showBackground = true)
@Composable
fun PreviewNewCustomButtons() {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val defaultTextConfig =
                TextConfig(textColor = Color.White, textSize = 16.sp, fontWeight = FontWeight.Bold)
            val defaultLayoutConfig =
                LayoutConfig(width = null, height = 58.dp, contentPadding = 6.dp)
            val defaultStateConfig = StateConfig(isEnabled = true, isLoading = false)
            val defaultBorderConfig = BorderConfig(buttonShape = RoundedCornerShape(8.dp))

            // Botón FILLED
            NewCustomButton(
                text = "Filled Button",
                onClick = { /* Acción */ },
                buttonType = ButtonType.FILLED,
                containerColor = Color.Blue,
                textConfig = defaultTextConfig,
                layoutConfig = defaultLayoutConfig,
                stateConfig = defaultStateConfig,
                borderConfig = defaultBorderConfig
            )

            // Botón TONAL_FILLED
            NewCustomButton(
                text = "Tonal Filled Button",
                onClick = { /* Acción */ },
                buttonType = ButtonType.TONAL_FILLED,
                containerColor = Color.Gray,
                textConfig = defaultTextConfig,
                layoutConfig = defaultLayoutConfig,
                stateConfig = defaultStateConfig,
                borderConfig = defaultBorderConfig
            )

            // Botón ELEVATED
            NewCustomButton(
                text = "Elevated Button",
                onClick = { /* Acción */ },
                buttonType = ButtonType.ELEVATED,
                containerColor = Color.Green,
                textConfig = defaultTextConfig,
                layoutConfig = defaultLayoutConfig,
                stateConfig = defaultStateConfig,
                borderConfig = defaultBorderConfig
            )

            // Botón OUTLINED
            NewCustomButton(
                text = "Outlined Button",
                onClick = { /* Acción */ },
                buttonType = ButtonType.OUTLINED,
                containerColor = Color.Transparent, // Outlined suele ser transparente
                textConfig = defaultTextConfig.copy(textColor = Color.Blue),
                layoutConfig = defaultLayoutConfig,
                stateConfig = defaultStateConfig,
                borderConfig = defaultBorderConfig
            )

            // Botón TEXT
            NewCustomButton(
                text = "Text Button",
                onClick = { /* Acción */ },
                buttonType = ButtonType.TEXT,
                containerColor = Color.Transparent,
                textConfig = defaultTextConfig.copy(textColor = Color.Red),
                layoutConfig = defaultLayoutConfig,
                stateConfig = defaultStateConfig,
                borderConfig = defaultBorderConfig
            )

            // Botón solo con icono (sin texto)
            NewCustomButton(
                text = null,
                onClick = { /* Acción */ },
                buttonType = ButtonType.FILLED,
                containerColor = Color.Magenta,
                textConfig = defaultTextConfig,
                layoutConfig = defaultLayoutConfig,
                stateConfig = defaultStateConfig,
                iconConfig = IconConfig(
                    iconId = R.drawable.ic_launcher_foreground,
                    iconContentDescription = "Icon Only",
                    iconPosition = IconPosition.START,
                    iconSize = 24.dp,
                    iconSpacing = 8.dp
                ),
                borderConfig = BorderConfig(buttonShape = CircleShape)
            )

            // Botón con texto e icono a la derecha
            NewCustomButton(
                text = "Icon End",
                onClick = { /* Acción */ },
                buttonType = ButtonType.FILLED,
                containerColor = Color.Cyan,
                textConfig = defaultTextConfig.copy(textColor = Color.Black),
                layoutConfig = defaultLayoutConfig,
                stateConfig = defaultStateConfig,
                iconConfig = IconConfig(
                    iconId = R.drawable.ic_launcher_foreground,
                    iconPosition = IconPosition.END,
                    iconContentDescription = "Icon End",
                    iconSize = 24.dp,
                    iconSpacing = 8.dp
                ),
                borderConfig = defaultBorderConfig
            )

            NewCustomButton(
                onClick = { /* Acción */ },
                buttonType = ButtonType.FILLED,
                containerColor = Color.DarkGray,
                layoutConfig = LayoutConfig(width = 100.dp, height = 50.dp, contentPadding = 0.dp),
                stateConfig = StateConfig(isEnabled = true, isLoading = false),
                iconConfig = IconConfig(
                    iconId = R.drawable.ic_launcher_foreground,
                    iconContentDescription = "Small Icon",
                    iconSize = 50.dp,
                    iconSpacing = 0.dp,
                    iconTint = Color.White,
                    iconPosition = IconPosition.START
                ),
                borderConfig = BorderConfig(buttonShape = RectangleShape) // Forma cuadrada
            )
            NewCustomButton(
                onClick = { /* Acción */ },
                buttonType = ButtonType.FILLED,
                containerColor = Color.DarkGray,
                layoutConfig = LayoutConfig(width = 48.dp, height = 48.dp, contentPadding = 0.dp),
                stateConfig = StateConfig(isEnabled = true, isLoading = false),
                iconConfig = IconConfig(
                    iconId = R.drawable.ic_launcher_foreground,
                    iconContentDescription = "Small Icon",
                    iconSize = 24.dp,
                    iconSpacing = 0.dp,
                    iconTint = Color.White,
                    iconPosition = IconPosition.START
                ),
                borderConfig = BorderConfig(buttonShape = RectangleShape) // Forma cuadrada
            )

        }
    }
}
