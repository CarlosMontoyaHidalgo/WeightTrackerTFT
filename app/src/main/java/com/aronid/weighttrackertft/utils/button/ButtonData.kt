package com.aronid.weighttrackertft.utils.button

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class IconConfig(
    val iconId: Int,
    val iconContentDescription: String,
    val iconPosition: IconPosition = IconPosition.START,
    val iconSize: Dp = 24.dp,
    val iconSpacing: Dp = 8.dp,
    val iconTint: Color? = null
)

data class TextConfig(
    val textSize: TextUnit = 24.sp,
    val fontWeight: FontWeight = FontWeight.Normal,
    val textColor: Color? = null
)

data class LayoutConfig(
    val width: Dp? = null,
    val height: Dp = 50.dp,
    val contentPadding: Dp = 8.dp
)

data class BorderConfig(
    val borderColor: Color? = null,
    val borderWidth: Dp? = 1.dp,
    val buttonShape: Shape = RoundedCornerShape(24.dp)
)

data class StateConfig(
    val isLoading: Boolean = false,
    val isEnabled: Boolean = true
)