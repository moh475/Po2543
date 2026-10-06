package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.IslandStateManager
import com.example.model.IslandConfig
import com.example.model.IslandEvent
import com.example.model.IslandExpansionState
import com.example.model.NotchPosition
import com.example.service.DynamicIslandOverlayService
import com.example.ui.island.DynamicIslandView
import com.example.ui.theme.AppleBlue
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.AppleOrange
import com.example.ui.theme.ApplePurple
import com.example.ui.theme.AppleRed
import com.example.ui.theme.IslandCardDark
import com.example.ui.theme.IslandSurfaceElevated

@Composable
fun DashboardScreen(
    stateManager: IslandStateManager,
    config: IslandConfig,
    primaryEvent: IslandEvent?,
    secondaryEvent: IslandEvent?,
    expansionState: IslandExpansionState,
    onNavigateToSimulator: () -> Unit,
    onNavigateToCustomizer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasOverlayPermission by remember { mutableStateOf(Settings.canDrawOverlays(context)) }
    var isServiceRunning by remember { mutableStateOf(false) }

    // Recheck permission when screen resumes
    LaunchedEffect(Unit) {
        hasOverlayPermission = Settings.canDrawOverlays(context)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header & Interactive Mini Island Sandbox
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = IslandCardDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262C3A))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "الجزيرة التفاعلية",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (config.isEnabled) "نشطة وجاهزة للتفاعل" else "معطلة حالياً",
                            fontSize = 13.sp,
                            color = if (config.isEnabled) AppleGreen else Color.Gray
                        )
                    }

                    Switch(
                        checked = config.isEnabled,
                        onCheckedChange = { isChecked ->
                            stateManager.updateConfig(config.copy(isEnabled = isChecked))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AppleGreen,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color(0xFF2C3240)
                        ),
                        modifier = Modifier.testTag("island_main_toggle")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive live preview pill in the card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF090B0E))
                        .border(1.dp, Color(0xFF1F2430), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
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
                        onDismissPrimary = { primaryEvent?.let { stateManager.dismissEvent(it.id) } }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "💡 انقر على الجزيرة أعلاه لتجربة التوسيع والانكماش السلس",
                    fontSize = 12.sp,
                    color = Color(0xFFA1A8B8)
                )
            }
        }

        // Overlay Service Status & Controller
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = IslandSurfaceElevated)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (hasOverlayPermission) AppleGreen.copy(alpha = 0.2f) else AppleOrange.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (hasOverlayPermission) Icons.Default.Layers else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (hasOverlayPermission) AppleGreen else AppleOrange,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "الظهور فوق كل التطبيقات",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                text = if (hasOverlayPermission) "الخدمة العائمة مهيأة للعمل" else "يلزم تفعيل إذن الظهور فوق الشاشة",
                                fontSize = 12.sp,
                                color = Color(0xFF8E8E93)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (!hasOverlayPermission) {
                    Button(
                        onClick = {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            )
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("grant_overlay_button")
                    ) {
                        Text("منح إذن الظهور فوق التطبيقات", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val intent = Intent(context, DynamicIslandOverlayService::class.java)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    context.startForegroundService(intent)
                                } else {
                                    context.startService(intent)
                                }
                                isServiceRunning = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppleGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("start_service_button")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تشغيل فوق الشاشة")
                        }

                        OutlinedButton(
                            onClick = {
                                val intent = Intent(context, DynamicIslandOverlayService::class.java)
                                context.stopService(intent)
                                isServiceRunning = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("stop_service_button")
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null, tint = AppleRed, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("إيقاف", color = AppleRed)
                        }
                    }
                }
            }
        }

        // Quick Test Triggers Carousel
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = IslandCardDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "تجربة فورية وسريعة",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "انقر لتجربة سلوكيات الجزيرة فوراً",
                    fontSize = 12.sp,
                    color = Color(0xFF8E8E93)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionChip(
                        icon = Icons.Default.MusicNote,
                        iconTint = ApplePurple,
                        label = "موسيقى",
                        modifier = Modifier.weight(1f),
                        onClick = { stateManager.triggerMusicMock() }
                    )
                    QuickActionChip(
                        icon = Icons.Default.Bolt,
                        iconTint = AppleGreen,
                        label = "شحن 84%",
                        modifier = Modifier.weight(1f),
                        onClick = { stateManager.triggerChargingMock(true, 84) }
                    )
                    QuickActionChip(
                        icon = Icons.Default.Notifications,
                        iconTint = AppleBlue,
                        label = "واتساب",
                        modifier = Modifier.weight(1f),
                        onClick = { stateManager.triggerNotificationMock() }
                    )
                    QuickActionChip(
                        icon = Icons.Default.HourglassTop,
                        iconTint = AppleOrange,
                        label = "مؤقت",
                        modifier = Modifier.weight(1f),
                        onClick = { stateManager.triggerTimerMock(30) }
                    )
                }
            }
        }

        // Notch Cutout Presets
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = IslandCardDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "تحديد موقع الكاميرا الأمامية",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "اختر موضع ثقب الكاميرا ليطابق هاتفك بالضبط",
                    fontSize = 12.sp,
                    color = Color(0xFF8E8E93)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NotchPresetButton(
                        title = "في الوسط",
                        isSelected = config.notchPosition == NotchPosition.CENTER,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            stateManager.updateConfig(
                                config.copy(notchPosition = NotchPosition.CENTER, xOffset = 0)
                            )
                        }
                    )
                    NotchPresetButton(
                        title = "على اليسار",
                        isSelected = config.notchPosition == NotchPosition.LEFT,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            stateManager.updateConfig(
                                config.copy(notchPosition = NotchPosition.LEFT, xOffset = -90)
                            )
                        }
                    )
                    NotchPresetButton(
                        title = "على اليمين",
                        isSelected = config.notchPosition == NotchPosition.RIGHT,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            stateManager.updateConfig(
                                config.copy(notchPosition = NotchPosition.RIGHT, xOffset = 90)
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onNavigateToCustomizer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تخصيص المقاسات والأبعاد بالمليمتر")
                }
            }
        }

        // Quick Navigation to Full Interactive Simulator
        Button(
            onClick = onNavigateToSimulator,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1D263B)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = AppleBlue)
            Spacer(modifier = Modifier.width(8.dp))
            Text("فتح محاكي شاشة الآيفون التفاعلي بالكامل", color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun QuickActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF1E222D),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3240)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun NotchPresetButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) AppleBlue else Color(0xFF1E222D),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) AppleBlue else Color(0xFF2E3544)
        ),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
