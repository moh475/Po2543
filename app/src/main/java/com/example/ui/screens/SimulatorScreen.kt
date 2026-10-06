package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.IslandStateManager
import com.example.model.IslandConfig
import com.example.model.IslandEvent
import com.example.model.IslandExpansionState
import com.example.model.NotchPosition
import com.example.ui.island.DynamicIslandView
import com.example.ui.theme.AppleBlue
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.AppleOrange
import com.example.ui.theme.ApplePink
import com.example.ui.theme.ApplePurple
import com.example.ui.theme.AppleRed
import com.example.ui.theme.IslandCardDark

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SimulatorScreen(
    stateManager: IslandStateManager,
    config: IslandConfig,
    primaryEvent: IslandEvent?,
    secondaryEvent: IslandEvent?,
    expansionState: IslandExpansionState,
    modifier: Modifier = Modifier
) {
    var isSilentMode by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "المحاكي المباشر للجزيرة التفاعلية",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "شاهد تفاعل الجزيرة مع شاشة الهاتف واختبر مختلف الإشعارات",
            fontSize = 12.sp,
            color = Color(0xFF8E8E93)
        )

        // Realistic Smartphone Screen Canvas
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            border = androidx.compose.foundation.BorderStroke(3.dp, Color(0xFF2C3240))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF151828),
                                Color(0xFF0F172A),
                                Color(0xFF05070C)
                            )
                        )
                    )
            ) {
                // Status Bar Top (9:41, Cellular, WiFi, Battery)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "9:41",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.SignalCellularAlt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Box(
                            modifier = Modifier
                                .width(22.dp)
                                .height(11.dp)
                                .border(1.dp, Color.White, RoundedCornerShape(3.dp))
                                .padding(1.5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .fillMaxSize()
                                    .background(Color.White, RoundedCornerShape(1.dp))
                            )
                        }
                    }
                }

                // Centered Wallpaper Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 110.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "الأربعاء، ٦ أكتوبر",
                        color = Color(0xFFA1A8B8),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "09:41",
                        color = Color.White,
                        fontSize = 46.sp,
                        fontWeight = FontWeight.Light
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "انقر على الجزيرة في الأعلى لتوسيعها ☝️",
                        color = Color(0xFF8E8E93),
                        fontSize = 12.sp
                    )
                }

                // Dynamic Island Placed on Screen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    val xOffsetDp = when (config.notchPosition) {
                        NotchPosition.LEFT -> (-90 + config.xOffset).dp
                        NotchPosition.RIGHT -> (90 + config.xOffset).dp
                        NotchPosition.CENTER -> config.xOffset.dp
                        NotchPosition.CUSTOM -> config.xOffset.dp
                    }

                    DynamicIslandView(
                        config = config,
                        primaryEvent = primaryEvent,
                        secondaryEvent = secondaryEvent,
                        expansionState = expansionState,
                        onToggleExpansion = { stateManager.toggleExpansion() },
                        onSetExpansion = { stateManager.setExpansion(it) },
                        onPlayPause = { stateManager.togglePlayPause() },
                        onNextTrack = { stateManager.nextTrack() },
                        onPrevTrack = { stateManager.prevTrack() },
                        onDismissPrimary = { primaryEvent?.let { stateManager.dismissEvent(it.id) } },
                        modifier = Modifier
                            .offset(x = xOffsetDp, y = config.yOffset.dp)
                            .testTag("simulator_dynamic_island")
                    )
                }
            }
        }

        // Control Deck for Event Triggers
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = IslandCardDark)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "لوحة التحكم في الأحداث والتنبيهات",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "انقر على أي حدث لارساله إلى الجزيرة فوراً وبثوانٍ معدودة",
                    fontSize = 12.sp,
                    color = Color(0xFF8E8E93)
                )

                Spacer(modifier = Modifier.height(14.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 2
                ) {
                    SimTriggerButton(
                        title = "أغنية Spotify",
                        subtitle = "The Weeknd - Blinding Lights",
                        icon = Icons.Default.MusicNote,
                        iconColor = ApplePurple,
                        onClick = { stateManager.triggerMusicMock() },
                        modifier = Modifier.weight(1f)
                    )

                    SimTriggerButton(
                        title = "شحن سريع 84%",
                        subtitle = "حلقة الشحن الخضراء",
                        icon = Icons.Default.Bolt,
                        iconColor = AppleGreen,
                        onClick = { stateManager.triggerChargingMock(true, 84) },
                        modifier = Modifier.weight(1f)
                    )

                    SimTriggerButton(
                        title = "رسالة واتساب",
                        subtitle = "إشعار محادثة فوري",
                        icon = Icons.Default.Notifications,
                        iconColor = Color(0xFF25D366),
                        onClick = {
                            stateManager.triggerNotificationMock(
                                sender = "أحمد محمد",
                                message = "يا صاحبي متى نتقابل في المقهى؟",
                                appName = "واتساب"
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )

                    SimTriggerButton(
                        title = "مكالمة واردة",
                        subtitle = "مكالمة جارية من سارة",
                        icon = Icons.Default.Call,
                        iconColor = AppleBlue,
                        onClick = { stateManager.triggerCallMock("سارة حسن") },
                        modifier = Modifier.weight(1f)
                    )

                    SimTriggerButton(
                        title = "مؤقت 30 ثانية",
                        subtitle = "عد تنازلي مباشر",
                        icon = Icons.Default.HourglassTop,
                        iconColor = AppleOrange,
                        onClick = { stateManager.triggerTimerMock(30) },
                        modifier = Modifier.weight(1f)
                    )

                    SimTriggerButton(
                        title = "AirPods Pro",
                        subtitle = "سماعات متصلة 98%",
                        icon = Icons.Default.Headphones,
                        iconColor = AppleBlue,
                        onClick = { stateManager.triggerBluetoothMock("AirPods Pro 2") },
                        modifier = Modifier.weight(1f)
                    )

                    SimTriggerButton(
                        title = "بطارية منخفضة 15%",
                        subtitle = "تنبيه أحمر تحذيري",
                        icon = Icons.Default.BatteryAlert,
                        iconColor = AppleRed,
                        onClick = { stateManager.triggerBatteryLowMock(15) },
                        modifier = Modifier.weight(1f)
                    )

                    SimTriggerButton(
                        title = if (isSilentMode) "وضع الرنين" else "الوضع الصامت",
                        subtitle = "أيقونة الصوت بالأعلى",
                        icon = if (isSilentMode) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        iconColor = if (isSilentMode) AppleGreen else AppleRed,
                        onClick = {
                            isSilentMode = !isSilentMode
                            stateManager.triggerRingerMock(if (isSilentMode) "صامت" else "عام")
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { stateManager.dismissAll() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262C3A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CleaningServices, contentDescription = null, tint = AppleRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("إخفاء وتفريغ كل إشعارات الجزيرة", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun SimTriggerButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF1B202D),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3446)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = Color(0xFF8E8E93),
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }
    }
}
