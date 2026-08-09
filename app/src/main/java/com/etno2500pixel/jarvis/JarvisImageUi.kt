package com.etno2500pixel.jarvis

import android.app.Activity
import android.view.View
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource

@Composable
fun JarvisImageApp() {
    val view = LocalView.current
    DisposableEffect(view) {
        val activity = view.context as? Activity
        val immersiveFlags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        activity?.window?.decorView?.systemUiVisibility = immersiveFlags
        onDispose { }
    }

    val transition = rememberInfiniteTransition(label = "earth")
    val rotation by transition.animateFloat(
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
            contentDescription = "Erde",
            modifier = Modifier
                .size(maxHeight * 0.293f)
                .offset(
                    x = maxWidth * 0.278f,
                    y = maxHeight * 0.1725f
                )
                .graphicsLayer {
                    rotationY = rotation
                },
            contentScale = ContentScale.FillBounds
        )
    }
}
