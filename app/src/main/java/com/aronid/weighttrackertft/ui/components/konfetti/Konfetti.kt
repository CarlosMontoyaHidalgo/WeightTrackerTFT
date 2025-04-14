package com.aronid.weighttrackertft.ui.components.konfetti

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@Composable
fun KonfettiComponent(
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    durationMs: Long = 3000L,
    colors: List<Int> = listOf(
        Color.Red.hashCode(),
        Color.Green.hashCode(),
        Color.Blue.hashCode(),
        Color.Yellow.hashCode()
    ),
    onFinish: () -> Unit = {}
) {
    val showKonfetti = remember { mutableStateOf(false) }

    LaunchedEffect(isActive) {
        if (isActive) {
            showKonfetti.value = true
            delay(durationMs)
            showKonfetti.value = false
            onFinish()
        }
    }

    if (showKonfetti.value) {
        KonfettiView(
            modifier = modifier,
            parties = listOf(
                Party(
                    speed = 0f,
                    maxSpeed = 30f,
                    angle = 270,
                    spread = 360,
                    colors = colors,
                    emitter = Emitter(duration = durationMs, TimeUnit.MILLISECONDS).max(300),
                    position = Position.Relative(0.5, 0.0)
                )
            )
        )
    }
}