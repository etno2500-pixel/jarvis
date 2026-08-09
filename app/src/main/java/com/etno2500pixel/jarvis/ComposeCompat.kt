package com.etno2500pixel.jarvis

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

typealias BorderStroke = androidx.compose.foundation.BorderStroke
typealias Offset = androidx.compose.ui.geometry.Offset
typealias Rect = androidx.compose.ui.geometry.Rect
typealias Stroke = androidx.compose.ui.graphics.drawscope.Stroke

fun Modifier.clip(shape: Shape): Modifier = androidx.compose.ui.draw.clip(this, shape)
