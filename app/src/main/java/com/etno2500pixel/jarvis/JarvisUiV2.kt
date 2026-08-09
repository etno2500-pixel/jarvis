package com.etno2500pixel.jarvis

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun JarvisAppV2(
    apiKey: String,
    saveKey: (String) -> Unit,
    onSend: (String, (String) -> Unit) -> Unit,
    onSpeak: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "earth")
    val earthRotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "earthRotation"
    )

    BoxWithConstraints(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.jarvis_hud),
            contentDescription = "JARVIS Oberfläche",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Image(
            painter = painterResource(R.drawable.jarvis_earth),
            contentDescription = "Rotierende Erde",
            modifier = Modifier
                .size(maxHeight * 0.293f)
                .offset(
                    x = maxWidth * 0.278f,
                    y = maxHeight * 0.1725f
                )
                .graphicsLayer {
                    rotationZ = earthRotation
                },
            contentScale = ContentScale.FillBounds,
            colorFilter = ColorFilter.tint(androidx.compose.ui.graphics.Color.White, BlendMode.Screen)
        )
    }
}
