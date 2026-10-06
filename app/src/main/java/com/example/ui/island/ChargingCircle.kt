package com.example.ui.island

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AppleGreen

@Composable
fun ChargingCircle(
    batteryPercent: Int,
    isCharging: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    strokeWidth: Dp = 2.5.dp,
    color: Color = AppleGreen
) {
    val transition = rememberInfiniteTransition(label = "pulse")
    val alpha by transition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "chargingAlpha"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            // Track background
            drawCircle(
                color = color.copy(alpha = 0.2f),
                style = Stroke(width = strokeWidth.toPx())
            )
            // Progress arc
            val sweepAngle = (batteryPercent / 100f) * 360f
            drawArc(
                color = if (isCharging) color.copy(alpha = alpha) else color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }

        if (isCharging) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = "جاري الشحن",
                tint = color,
                modifier = Modifier.size(size * 0.65f)
            )
        }
    }
}
