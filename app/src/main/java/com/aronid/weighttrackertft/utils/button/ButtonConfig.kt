package com.aronid.weighttrackertft.utils.button

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

fun getDefaultButtonConfig(isFormValid: Boolean): Triple<TextConfig, LayoutConfig, StateConfig> {
    val textConfig = TextConfig(
        textColor = Color.White,
        textSize = 16.sp,
        fontWeight = FontWeight.Bold
    )

    val layoutConfig = LayoutConfig(width = null, height = 70.dp, contentPadding = 6.dp)

    val stateConfig = StateConfig(
        isEnabled = isFormValid,
        isLoading = false
    )

    return Triple(textConfig, layoutConfig, stateConfig)
}

fun getDefaultBorderConfig(): BorderConfig {
    return BorderConfig(buttonShape = RoundedCornerShape(8.dp))
}
