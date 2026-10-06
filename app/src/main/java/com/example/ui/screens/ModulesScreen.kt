package com.example.ui.screens

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.IslandStateManager
import com.example.model.IslandConfig
import com.example.model.IslandEvent
import com.example.model.IslandEventType
import com.example.model.IslandExpansionState
import com.example.service.IslandNotificationService
import com.example.ui.island.DynamicIslandView
import com.example.ui.island.NotificationStyleHelper
import com.example.ui.theme.AppleBlue
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.AppleOrange
import com.example.ui.theme.IslandCardDark
import com.example.ui.theme.IslandSurfaceElevated

@Composable
fun ModulesScreen(
    stateManager: IslandStateManager,
    config: IslandConfig,
    primaryEvent: IslandEvent? = null,
    secondaryEvent: IslandEvent? = null,
    expansionState: IslandExpansionState = IslandExpansionState.COMPACT,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isNotificationServiceEnabled = remember { isNotificationAccessGranted(context) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title & subtitle
        Column {
            Text(
                text = "تخصيص الإشعارات والتنبيهات",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "تحكم في تفعيل كل نوع إشعار، واختر لونه وأيقونته المفضلة في الجزيرة",
                fontSize = 12.sp,
                color = Color(0xFF8E8E93)
            )
        }

        // Live Preview Box at Top
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF090B0E)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262C3A))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "معاينة حية لتصميم الجزيرة بالألوان المختارة",
                    fontSize = 12.sp,
                    color = Color(0xFFA1A8B8),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF040608))
                        .border(1.dp, Color(0xFF191D26), RoundedCornerShape(16.dp)),
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

                Spacer(modifier = Modifier.height(10.dp))

                // Quick preview switcher chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PreviewTriggerChip("🎵 موسيقى", onClick = { stateManager.triggerMusicMock() })
                    PreviewTriggerChip("⚡ شحن", onClick = { stateManager.triggerChargingMock(true, 88) })
                    PreviewTriggerChip("💬 رسالة", onClick = { stateManager.triggerNotificationMock() })
                    PreviewTriggerChip("📞 مكالمة", onClick = { stateManager.triggerCallMock() })
                    PreviewTriggerChip("⏱️ مؤقت", onClick = { stateManager.triggerTimerMock(30) })
                    PreviewTriggerChip("🎧 بلوتوث", onClick = { stateManager.triggerBluetoothMock() })
                    PreviewTriggerChip("🪫 منخفضة", onClick = { stateManager.triggerBatteryLowMock(12) })
                }
            }
        }

        // Notification Access Warning if not enabled
        if (!isNotificationServiceEnabled) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = IslandSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3240))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AppleOrange.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = AppleOrange, modifier = Modifier.size(20.dp))
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text("وصول الإشعارات غير مفعل", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("لتلقي رسائل واتساب والأغاني الفعلية", color = Color(0xFF8E8E93), fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("تفعيل", fontSize = 12.sp)
                    }
                }
            }
        }

        // 1. Music & Media Player Customization Card
        NotificationCategoryCard(
            title = "مشغل الموسيقى والوسائط",
            subtitle = "أزرار التحكم، الموجات الصوتية، واسم الأغنية والفنان",
            isEnabled = config.musicEnabled,
            onToggleEnabled = { stateManager.updateConfig(config.copy(musicEnabled = it)) },
            selectedColor = config.musicColor,
            onColorSelected = { stateManager.updateConfig(config.copy(musicColor = it)) },
            iconOptions = NotificationStyleHelper.getMusicIcons(),
            selectedIconIndex = config.musicIconIndex,
            onIconSelected = { stateManager.updateConfig(config.copy(musicIconIndex = it)) },
            onTest = { stateManager.triggerMusicMock() },
            testLabel = "تجربة مشغل الموسيقى"
        )

        // 2. Incoming & Ongoing Calls Customization Card
        NotificationCategoryCard(
            title = "المكالمات الهاتفية",
            subtitle = "مدة المكالمة الحالية وأزرار الرد أو إنهاء المكالمة",
            isEnabled = config.callEnabled,
            onToggleEnabled = { stateManager.updateConfig(config.copy(callEnabled = it)) },
            selectedColor = config.callColor,
            onColorSelected = { stateManager.updateConfig(config.copy(callColor = it)) },
            iconOptions = NotificationStyleHelper.getCallIcons(),
            selectedIconIndex = config.callIconIndex,
            onIconSelected = { stateManager.updateConfig(config.copy(callIconIndex = it)) },
            onTest = { stateManager.triggerCallMock("سارة حسن") },
            testLabel = "تجربة إشعار مكالمة"
        )

        // 3. Battery & Charging Customization Card
        NotificationCategoryCard(
            title = "تنبيهات الشحن والبطارية",
            subtitle = "حلقة الشحن الدائرية مع نسبة الشحن المئوية",
            isEnabled = config.chargingEnabled,
            onToggleEnabled = { stateManager.updateConfig(config.copy(chargingEnabled = it)) },
            selectedColor = config.chargingColor,
            onColorSelected = { stateManager.updateConfig(config.copy(chargingColor = it)) },
            iconOptions = NotificationStyleHelper.getChargingIcons(),
            selectedIconIndex = config.chargingIconIndex,
            onIconSelected = { stateManager.updateConfig(config.copy(chargingIconIndex = it)) },
            onTest = { stateManager.triggerChargingMock(true, 85) },
            testLabel = "تجربة شحن البطارية"
        )

        // 4. Timer & Countdown Customization Card
        NotificationCategoryCard(
            title = "المؤقت وساعة الإيقاف",
            subtitle = "العد التنازلي التفاعلي المباشر في الجزيرة",
            isEnabled = config.timerEnabled,
            onToggleEnabled = { stateManager.updateConfig(config.copy(timerEnabled = it)) },
            selectedColor = config.timerColor,
            onColorSelected = { stateManager.updateConfig(config.copy(timerColor = it)) },
            iconOptions = NotificationStyleHelper.getTimerIcons(),
            selectedIconIndex = config.timerIconIndex,
            onIconSelected = { stateManager.updateConfig(config.copy(timerIconIndex = it)) },
            onTest = { stateManager.triggerTimerMock(30) },
            testLabel = "تجربة مؤقت 30 ثانية"
        )

        // 5. App Notifications (WhatsApp, Telegram) Customization Card
        NotificationCategoryCard(
            title = "إشعارات التطبيقات والرسائل",
            subtitle = "رسائل الواتساب وتيليجرام والإيميل مع زر الرد السريع",
            isEnabled = config.notificationsEnabled,
            onToggleEnabled = { stateManager.updateConfig(config.copy(notificationsEnabled = it)) },
            selectedColor = config.notificationsColor,
            onColorSelected = { stateManager.updateConfig(config.copy(notificationsColor = it)) },
            iconOptions = NotificationStyleHelper.getNotificationIcons(),
            selectedIconIndex = config.notificationsIconIndex,
            onIconSelected = { stateManager.updateConfig(config.copy(notificationsIconIndex = it)) },
            onTest = { stateManager.triggerNotificationMock("أحمد محمد", "جاهز نتقابل النهاردة يا بطل؟", "واتساب") },
            testLabel = "تجربة إشعار رسالة"
        )

        // 6. Bluetooth & Accessories Customization Card
        NotificationCategoryCard(
            title = "سماعات البلوتوث والملحقات",
            subtitle = "إشعار جميل عند اتصال سماعات AirPods وسماعات البلوتوث",
            isEnabled = config.bluetoothEnabled,
            onToggleEnabled = { stateManager.updateConfig(config.copy(bluetoothEnabled = it)) },
            selectedColor = config.bluetoothColor,
            onColorSelected = { stateManager.updateConfig(config.copy(bluetoothColor = it)) },
            iconOptions = NotificationStyleHelper.getBluetoothIcons(),
            selectedIconIndex = config.bluetoothIconIndex,
            onIconSelected = { stateManager.updateConfig(config.copy(bluetoothIconIndex = it)) },
            onTest = { stateManager.triggerBluetoothMock("AirPods Pro 2") },
            testLabel = "تجربة اتصال AirPods"
        )

        // 7. Low Battery Warning Customization Card
        NotificationCategoryCard(
            title = "تنبيه انخفاض طاقة البطارية",
            subtitle = "تحذير فوري عند وصول نسبة شحن البطارية إلى 15% أو أقل",
            isEnabled = config.batteryLowEnabled,
            onToggleEnabled = { stateManager.updateConfig(config.copy(batteryLowEnabled = it)) },
            selectedColor = config.batteryLowColor,
            onColorSelected = { stateManager.updateConfig(config.copy(batteryLowColor = it)) },
            iconOptions = NotificationStyleHelper.getBatteryLowIcons(),
            selectedIconIndex = config.batteryLowIconIndex,
            onIconSelected = { stateManager.updateConfig(config.copy(batteryLowIconIndex = it)) },
            onTest = { stateManager.triggerBatteryLowMock(14) },
            testLabel = "تجربة تنبيه انخفاض البطارية"
        )
    }
}

@Composable
private fun NotificationCategoryCard(
    title: String,
    subtitle: String,
    isEnabled: Boolean,
    onToggleEnabled: (Boolean) -> Unit,
    selectedColor: Long,
    onColorSelected: (Long) -> Unit,
    iconOptions: List<Pair<String, ImageVector>>,
    selectedIconIndex: Int,
    onIconSelected: (Int) -> Unit,
    onTest: () -> Unit,
    testLabel: String,
    modifier: Modifier = Modifier
) {
    val currentColor = Color(selectedColor)
    val currentIcon = iconOptions.getOrElse(selectedIconIndex) { iconOptions[0] }.second

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = IslandCardDark),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isEnabled) currentColor.copy(alpha = 0.35f) else Color(0xFF262C3A)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with Icon, Name, and Toggle Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isEnabled) currentColor else Color(0xFF2A2E3D)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = currentIcon,
                        contentDescription = null,
                        tint = if (isEnabled) Color.White else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        color = Color(0xFF8E8E93),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggleEnabled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = currentColor,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color(0xFF2C3240)
                    )
                )
            }

            // Expandable Customization Section (Colors + Icons + Test)
            AnimatedVisibility(
                visible = isEnabled,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Color Palette Selector
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "لون الإشعار في الجزيرة",
                                color = Color(0xFFD1D5DB),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(currentColor)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            NotificationStyleHelper.PALETTE_COLORS.forEach { colorValue ->
                                val colorItem = Color(colorValue)
                                val isSelected = selectedColor == colorValue

                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(colorItem)
                                        .clickable { onColorSelected(colorValue) }
                                        .then(
                                            if (isSelected) {
                                                Modifier.border(2.5.dp, Color.White, CircleShape)
                                            } else {
                                                Modifier
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Icon Options Selector
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "أيقونة الإشعار",
                            color = Color(0xFFD1D5DB),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            iconOptions.forEachIndexed { index, (label, iconVector) ->
                                val isSelected = selectedIconIndex == index
                                Surface(
                                    onClick = { onIconSelected(index) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) currentColor.copy(alpha = 0.22f) else Color(0xFF1E222D),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) currentColor else Color(0xFF2C3240)
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = iconVector,
                                            contentDescription = label,
                                            tint = if (isSelected) currentColor else Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = label,
                                            fontSize = 10.sp,
                                            color = if (isSelected) Color.White else Color(0xFF8E8E93),
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Instant Test Action Button
                    Button(
                        onClick = onTest,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = currentColor.copy(alpha = 0.2f),
                            contentColor = currentColor
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(testLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PreviewTriggerChip(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1A1F2B),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2B3345))
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

private fun isNotificationAccessGranted(context: Context): Boolean {
    val enabledListeners = Settings.Secure.getString(
        context.contentResolver,
        "enabled_notification_listeners"
    ) ?: return false
    val myComponent = ComponentName(context, IslandNotificationService::class.java).flattenToString()
    return enabledListeners.contains(myComponent)
}
