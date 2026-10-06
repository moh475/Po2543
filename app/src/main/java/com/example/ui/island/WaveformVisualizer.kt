package com.example.ui.island

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AppleGreen

@Composable
fun WaveformVisualizer(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true,
    barColor: Color = AppleGreen,
    barCount: Int = 4,
    maxHeight: Dp = 16.dp,
    barWidth: Dp = 3.dp,
    spacing: Dp = 2.dp
) {
    val transition = rememberInfiniteTransition(label = "waveform")

    val h1 by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = if (isPlaying) 1.0f else 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 420, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "h1"
    )

    val h2 by transition.animateFloat(
        initialValue = 0.6f,
        targetValue = if (isPlaying) 0.2f else 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 560, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "h2"
    )

    val h3 by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = if (isPlaying) 0.9f else 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "h3"
    )

    val h4 by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = if (isPlaying) 0.35f else 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 480, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "h4"
    )

    val heights = listOf(h1, h2, h3, h4)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val scale = heights[i % heights.size]
            val currentHeight = maxHeight * scale
            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(currentHeight.coerceAtLeast(3.dp))
                    .background(barColor, shape = RoundedCornerShape(2.dp))
            )
        }
    }
}
