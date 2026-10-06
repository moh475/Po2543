package com.example.ui.island

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.IslandConfig
import com.example.model.IslandEvent
import com.example.model.IslandEventType
import com.example.model.IslandExpansionState
import com.example.ui.theme.AppleBlue
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.AppleOrange
import com.example.ui.theme.ApplePurple
import com.example.ui.theme.AppleRed
import com.example.ui.theme.IslandBlack

@Composable
fun DynamicIslandView(
    config: IslandConfig,
    primaryEvent: IslandEvent?,
    secondaryEvent: IslandEvent?,
    expansionState: IslandExpansionState,
    onToggleExpansion: () -> Unit,
    onSetExpansion: (IslandExpansionState) -> Unit,
    onPlayPause: () -> Unit,
    onNextTrack: () -> Unit,
    onPrevTrack: () -> Unit,
    onDismissPrimary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    fun performHaptic() {
        if (config.hapticsEnabled) {
            try {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            } catch (_: Exception) {}
        }
    }

    // Dynamic width and height animated with bouncy spring physics
    val targetWidth = when (expansionState) {
        IslandExpansionState.COLLAPSED -> {
            if (primaryEvent == null) config.pillWidth.dp else (config.pillWidth + 20).dp
        }
        IslandExpansionState.COMPACT -> {
            if (secondaryEvent != null && config.dualPillEnabled) 180.dp else 215.dp
        }
        IslandExpansionState.EXPANDED -> 345.dp
    }

    val targetHeight = when (expansionState) {
        IslandExpansionState.COLLAPSED -> config.pillHeight.dp
        IslandExpansionState.COMPACT -> (config.pillHeight + 4).dp
        IslandExpansionState.EXPANDED -> {
            when (primaryEvent?.type) {
                IslandEventType.MUSIC -> 175.dp
                IslandEventType.CALL -> 150.dp
                IslandEventType.TIMER -> 155.dp
                IslandEventType.NOTIFICATION -> 140.dp
                IslandEventType.CHARGING -> 135.dp
                else -> 140.dp
            }
        }
    }

    val animatedWidth by animateDpAsState(
        targetValue = targetWidth,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "islandWidth"
    )

    val animatedHeight by animateDpAsState(
        targetValue = targetHeight,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "islandHeight"
    )

    val targetCornerRadius = when (expansionState) {
        IslandExpansionState.EXPANDED -> 36.dp
        else -> config.cornerRadius.dp
    }

    val animatedCornerRadius by animateDpAsState(
        targetValue = targetCornerRadius,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMedium),
        label = "islandCorner"
    )

    val borderModifier = if (config.isGlowEnabled) {
        Modifier.border(
            width = 0.75.dp,
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.22f),
                    Color.White.copy(alpha = 0.05f),
                    Color.Transparent
                )
            ),
            shape = RoundedCornerShape(animatedCornerRadius)
        )
    } else {
        Modifier
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Main Primary Island Pill
        Box(
            modifier = Modifier
                .width(animatedWidth)
                .height(animatedHeight)
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(animatedCornerRadius), spotColor = Color.Black)
                .clip(RoundedCornerShape(animatedCornerRadius))
                .background(IslandBlack)
                .then(borderModifier)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            performHaptic()
                            onToggleExpansion()
                        },
                        onLongPress = {
                            performHaptic()
                            onSetExpansion(IslandExpansionState.EXPANDED)
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        if (dragAmount < -15) {
                            performHaptic()
                            onSetExpansion(IslandExpansionState.COLLAPSED)
                        } else if (dragAmount > 15) {
                            performHaptic()
                            onSetExpansion(IslandExpansionState.EXPANDED)
                        }
                    }
                }
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = expansionState,
                transitionSpec = {
                    fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMedium)) togetherWith
                            fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMedium))
                },
                label = "islandContent"
            ) { state ->
                when (state) {
                    IslandExpansionState.COLLAPSED -> {
                        CollapsedContent(primaryEvent = primaryEvent, config = config)
                    }
                    IslandExpansionState.COMPACT -> {
                        CompactContent(primaryEvent = primaryEvent, config = config)
                    }
                    IslandExpansionState.EXPANDED -> {
                        ExpandedContent(
                            event = primaryEvent,
                            config = config,
                            onPlayPause = {
                                performHaptic()
                                onPlayPause()
                            },
                            onNextTrack = {
                                performHaptic()
                                onNextTrack()
                            },
                            onPrevTrack = {
                                performHaptic()
                                onPrevTrack()
                            },
                            onDismiss = {
                                performHaptic()
                                onDismissPrimary()
                            }
                        )
                    }
                }
            }
        }

        // Secondary Dual-Pill Bubble (When 2 activities run concurrently, e.g. Spotify + Timer)
        if (secondaryEvent != null && config.dualPillEnabled && expansionState != IslandExpansionState.EXPANDED) {
            Spacer(modifier = Modifier.width(8.dp))
            SecondaryBubble(
                event = secondaryEvent,
                config = config,
                size = config.pillHeight.dp,
                onClick = {
                    performHaptic()
                    onSetExpansion(IslandExpansionState.EXPANDED)
                }
            )
        }
    }
}

@Composable
private fun CollapsedContent(primaryEvent: IslandEvent?, config: IslandConfig) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (primaryEvent != null) {
            MiniEventIcon(event = primaryEvent, config = config, modifier = Modifier.padding(start = 4.dp))
            MiniEventStatus(event = primaryEvent, modifier = Modifier.padding(end = 4.dp))
        } else {
            // Idle empty camera cutout mode
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF18181B))
            )
        }
    }
}

@Composable
private fun CompactContent(primaryEvent: IslandEvent?, config: IslandConfig) {
    if (primaryEvent == null) {
        CollapsedContent(null, config)
        return
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left side: Icon or thumbnail
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            EventLeadVisual(event = primaryEvent, config = config)
            Text(
                text = primaryEvent.title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 85.dp)
            )
        }

        // Right side: Active status / waveform / percent
        Row(verticalAlignment = Alignment.CenterVertically) {
            EventTrailingVisual(event = primaryEvent)
        }
    }
}

@Composable
private fun SecondaryBubble(
    event: IslandEvent,
    config: IslandConfig,
    size: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    val eventColor = NotificationStyleHelper.getColorForEvent(event.type, config)
    val eventIcon = NotificationStyleHelper.getIconForEvent(event.type, config)

    Box(
        modifier = Modifier
            .size(size)
            .shadow(elevation = 12.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(IslandBlack)
            .border(0.6.dp, Color.White.copy(alpha = 0.2f), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (event.type == IslandEventType.MUSIC) {
            WaveformVisualizer(
                isPlaying = event.isPlaying,
                barCount = 3,
                maxHeight = size * 0.5f,
                barWidth = 2.dp,
                barColor = eventColor
            )
        } else {
            Icon(
                imageVector = eventIcon,
                contentDescription = null,
                tint = eventColor,
                modifier = Modifier.size(size * 0.55f)
            )
        }
    }
}

@Composable
private fun EventLeadVisual(event: IslandEvent, config: IslandConfig) {
    val eventColor = NotificationStyleHelper.getColorForEvent(event.type, config)
    val eventIcon = NotificationStyleHelper.getIconForEvent(event.type, config)

    when (event.type) {
        IslandEventType.MUSIC -> {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(eventColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = eventIcon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
        IslandEventType.CHARGING -> {
            ChargingCircle(
                batteryPercent = event.batteryPercent,
                isCharging = event.isCharging,
                size = 22.dp,
                strokeWidth = 2.dp,
                color = eventColor
            )
        }
        IslandEventType.NOTIFICATION -> {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(eventColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = eventIcon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
        IslandEventType.CALL -> {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(eventColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = eventIcon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
        IslandEventType.TIMER -> {
            Icon(
                imageVector = eventIcon,
                contentDescription = null,
                tint = eventColor,
                modifier = Modifier.size(20.dp)
            )
        }
        IslandEventType.BLUETOOTH -> {
            Icon(
                imageVector = eventIcon,
                contentDescription = null,
                tint = eventColor,
                modifier = Modifier.size(20.dp)
            )
        }
        IslandEventType.BATTERY_LOW -> {
            Icon(
                imageVector = eventIcon,
                contentDescription = null,
                tint = eventColor,
                modifier = Modifier.size(20.dp)
            )
        }
        IslandEventType.RINGER -> {
            Icon(
                imageVector = if (event.isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                contentDescription = null,
                tint = if (event.isMuted) AppleRed else AppleGreen,
                modifier = Modifier.size(20.dp)
            )
        }
        else -> {}
    }
}

@Composable
private fun EventTrailingVisual(event: IslandEvent) {
    when (event.type) {
        IslandEventType.MUSIC -> {
            WaveformVisualizer(
                isPlaying = event.isPlaying,
                barColor = AppleGreen,
                maxHeight = 16.dp,
                barCount = 4
            )
        }
        IslandEventType.CHARGING -> {
            Text(
                text = "${event.batteryPercent}%",
                color = AppleGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        IslandEventType.NOTIFICATION -> {
            Text(
                text = event.appName.ifEmpty { "واتساب" },
                color = Color(0xFFA1A8B8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
        IslandEventType.CALL -> {
            WaveformVisualizer(
                isPlaying = true,
                barColor = AppleGreen,
                maxHeight = 14.dp,
                barCount = 3
            )
        }
        IslandEventType.TIMER -> {
            Text(
                text = event.subtitle,
                color = AppleOrange,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        IslandEventType.BATTERY_LOW -> {
            Text(
                text = "${event.batteryPercent}%",
                color = AppleRed,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        IslandEventType.BLUETOOTH -> {
            Text(
                text = "متصل",
                color = AppleBlue,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        else -> {}
    }
}

@Composable
private fun MiniEventIcon(event: IslandEvent, config: IslandConfig, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        EventLeadVisual(event = event, config = config)
    }
}

@Composable
private fun MiniEventStatus(event: IslandEvent, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        EventTrailingVisual(event = event)
    }
}

@Composable
private fun ExpandedContent(
    event: IslandEvent?,
    config: IslandConfig,
    onPlayPause: () -> Unit,
    onNextTrack: () -> Unit,
    onPrevTrack: () -> Unit,
    onDismiss: () -> Unit
) {
    if (event == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("الجزيرة التفاعلية", color = Color.White, fontSize = 14.sp)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        when (event.type) {
            IslandEventType.MUSIC -> {
                ExpandedMusicCard(
                    event = event,
                    config = config,
                    onPlayPause = onPlayPause,
                    onNextTrack = onNextTrack,
                    onPrevTrack = onPrevTrack,
                    onDismiss = onDismiss
                )
            }
            IslandEventType.CHARGING -> {
                ExpandedChargingCard(event = event, config = config, onDismiss = onDismiss)
            }
            IslandEventType.NOTIFICATION -> {
                ExpandedNotificationCard(event = event, config = config, onDismiss = onDismiss)
            }
            IslandEventType.CALL -> {
                ExpandedCallCard(event = event, config = config, onDismiss = onDismiss)
            }
            IslandEventType.TIMER -> {
                ExpandedTimerCard(event = event, config = config, onDismiss = onDismiss)
            }
            IslandEventType.BLUETOOTH -> {
                ExpandedBluetoothCard(event = event, config = config, onDismiss = onDismiss)
            }
            IslandEventType.BATTERY_LOW -> {
                ExpandedBatteryLowCard(event = event, config = config, onDismiss = onDismiss)
            }
            else -> {
                ExpandedGenericCard(event = event, onDismiss = onDismiss)
            }
        }
    }
}

@Composable
private fun ExpandedMusicCard(
    event: IslandEvent,
    config: IslandConfig,
    onPlayPause: () -> Unit,
    onNextTrack: () -> Unit,
    onPrevTrack: () -> Unit,
    onDismiss: () -> Unit
) {
    val musicColor = NotificationStyleHelper.getColorForEvent(IslandEventType.MUSIC, config)
    val musicIcon = NotificationStyleHelper.getIconForEvent(IslandEventType.MUSIC, config)

    Column(modifier = Modifier.fillMaxSize()) {
        // Header: Album Art, Info, Waveform & Close
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album art cover
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(musicColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = musicIcon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = event.subtitle,
                    color = Color(0xFFA1A8B8),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            WaveformVisualizer(
                isPlaying = event.isPlaying,
                barColor = musicColor,
                barCount = 5,
                maxHeight = 22.dp,
                barWidth = 3.5.dp
            )

            IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "إغلاق",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Scrubber / Progress Bar
        Column {
            LinearProgressIndicator(
                progress = { event.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = Color.White,
                trackColor = Color(0xFF333844)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = event.durationText.ifEmpty { "1:15" }, color = Color(0xFF8E8E93), fontSize = 10.sp)
                Text(text = event.appName.ifEmpty { "Spotify" }, color = musicColor, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                Text(text = event.remainingText.ifEmpty { "-2:35" }, color = Color(0xFF8E8E93), fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Playback Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevTrack) {
                Icon(Icons.Default.SkipPrevious, contentDescription = "السابق", tint = Color.White, modifier = Modifier.size(28.dp))
            }

            Surface(
                onClick = onPlayPause,
                shape = CircleShape,
                color = Color.White,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (event.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "تشغيل / إيقاف",
                        tint = Color.Black,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            IconButton(onClick = onNextTrack) {
                Icon(Icons.Default.SkipNext, contentDescription = "التالي", tint = Color.White, modifier = Modifier.size(28.dp))
            }
        }
    }
}

@Composable
private fun ExpandedChargingCard(event: IslandEvent, config: IslandConfig, onDismiss: () -> Unit) {
    val chargeColor = NotificationStyleHelper.getColorForEvent(IslandEventType.CHARGING, config)

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChargingCircle(
            batteryPercent = event.batteryPercent,
            isCharging = event.isCharging,
            size = 64.dp,
            strokeWidth = 5.dp,
            color = chargeColor
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${event.batteryPercent}% مشحون",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (event.isCharging) "الشحن السريع نشط • متبقي 28 دقيقة" else "البطارية في حالة ممتازة",
                color = chargeColor,
                fontSize = 12.sp
            )
        }

        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.Gray, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun ExpandedNotificationCard(event: IslandEvent, config: IslandConfig, onDismiss: () -> Unit) {
    val notifColor = NotificationStyleHelper.getColorForEvent(IslandEventType.NOTIFICATION, config)
    val notifIcon = NotificationStyleHelper.getIconForEvent(IslandEventType.NOTIFICATION, config)

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(notifColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(notifIcon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = event.appName.ifEmpty { "إشعار جديد" },
                    color = notifColor,
                    fontSize = 11.sp
                )
            }

            IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.Gray, modifier = Modifier.size(18.dp))
            }
        }

        Text(
            text = event.subtitle,
            color = Color(0xFFEDEDED),
            fontSize = 13.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Surface(
                onClick = onDismiss,
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF232733)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Reply, contentDescription = null, tint = notifColor, modifier = Modifier.size(14.dp))
                    Text("رد سريع", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun ExpandedCallCard(event: IslandEvent, config: IslandConfig, onDismiss: () -> Unit) {
    val callColor = NotificationStyleHelper.getColorForEvent(IslandEventType.CALL, config)
    val callIcon = NotificationStyleHelper.getIconForEvent(IslandEventType.CALL, config)

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(callColor.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(callIcon, contentDescription = null, tint = callColor, modifier = Modifier.size(28.dp))
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(event.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(event.subtitle, color = callColor, fontSize = 12.sp)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(
                onClick = onDismiss,
                shape = CircleShape,
                color = AppleRed,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.CallEnd, contentDescription = "إنهاء المكالمة", tint = Color.White, modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}

@Composable
private fun ExpandedTimerCard(event: IslandEvent, config: IslandConfig, onDismiss: () -> Unit) {
    val timerColor = NotificationStyleHelper.getColorForEvent(IslandEventType.TIMER, config)
    val timerIcon = NotificationStyleHelper.getIconForEvent(IslandEventType.TIMER, config)

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(timerColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(timerIcon, contentDescription = null, tint = timerColor, modifier = Modifier.size(30.dp))
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text("مؤقت تنازلي", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(event.subtitle, color = timerColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Surface(
            onClick = onDismiss,
            shape = CircleShape,
            color = Color(0xFF2C3240),
            modifier = Modifier.size(38.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Close, contentDescription = "إيقاف", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun ExpandedBluetoothCard(event: IslandEvent, config: IslandConfig, onDismiss: () -> Unit) {
    val btColor = NotificationStyleHelper.getColorForEvent(IslandEventType.BLUETOOTH, config)
    val btIcon = NotificationStyleHelper.getIconForEvent(IslandEventType.BLUETOOTH, config)

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(btColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(btIcon, contentDescription = null, tint = btColor, modifier = Modifier.size(30.dp))
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(event.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(event.subtitle, color = btColor, fontSize = 12.sp)
        }

        IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.Gray)
        }
    }
}

@Composable
private fun ExpandedBatteryLowCard(event: IslandEvent, config: IslandConfig, onDismiss: () -> Unit) {
    val lowColor = NotificationStyleHelper.getColorForEvent(IslandEventType.BATTERY_LOW, config)
    val lowIcon = NotificationStyleHelper.getIconForEvent(IslandEventType.BATTERY_LOW, config)

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(lowColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(lowIcon, contentDescription = null, tint = lowColor, modifier = Modifier.size(30.dp))
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(event.title, color = lowColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(event.subtitle, color = Color(0xFFA1A8B8), fontSize = 12.sp)
        }

        IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.Gray)
        }
    }
}

@Composable
private fun ExpandedGenericCard(event: IslandEvent, onDismiss: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(event.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            if (event.subtitle.isNotEmpty()) {
                Text(event.subtitle, color = Color(0xFFA1A8B8), fontSize = 12.sp)
            }
        }
        IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.Gray)
        }
    }
}
