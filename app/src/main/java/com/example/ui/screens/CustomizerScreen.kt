package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.ui.theme.AppleRed
import com.example.ui.theme.IslandCardDark

@Composable
fun CustomizerScreen(
    stateManager: IslandStateManager,
    config: IslandConfig,
    primaryEvent: IslandEvent?,
    secondaryEvent: IslandEvent?,
    expansionState: IslandExpansionState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "تخصيص الموضع والأبعاد",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "اضبط أبعاد الجزيرة لتلتف بدقة حول فتحة الكاميرا لهاتفك",
            fontSize = 12.sp,
            color = Color(0xFF8E8E93)
        )

        // Live Pin-Point Preview Box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF090B0E)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262C3A))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
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
                    modifier = Modifier.offset(x = xOffsetDp, y = (config.yOffset / 2).dp)
                )
            }
        }

        // Notch Alignment
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = IslandCardDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "موضع الكاميرا",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PositionChip(
                        title = "في الوسط",
                        isSelected = config.notchPosition == NotchPosition.CENTER,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            stateManager.updateConfig(
                                config.copy(notchPosition = NotchPosition.CENTER, xOffset = 0)
                            )
                        }
                    )
                    PositionChip(
                        title = "على اليسار",
                        isSelected = config.notchPosition == NotchPosition.LEFT,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            stateManager.updateConfig(
                                config.copy(notchPosition = NotchPosition.LEFT, xOffset = 0)
                            )
                        }
                    )
                    PositionChip(
                        title = "على اليمين",
                        isSelected = config.notchPosition == NotchPosition.RIGHT,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            stateManager.updateConfig(
                                config.copy(notchPosition = NotchPosition.RIGHT, xOffset = 0)
                            )
                        }
                    )
                }
            }
        }

        // Sliders Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = IslandCardDark)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // X Offset Slider
                SliderSettingItem(
                    label = "الإزاحة الأفقية (X Offset)",
                    valueText = "${config.xOffset} dp",
                    value = config.xOffset.toFloat(),
                    valueRange = -100f..100f,
                    onValueChange = { newVal ->
                        stateManager.updateConfig(config.copy(xOffset = newVal.toInt()))
                    }
                )

                // Y Offset Slider
                SliderSettingItem(
                    label = "المسافة من الأعلى (Y Offset)",
                    valueText = "${config.yOffset} dp",
                    value = config.yOffset.toFloat(),
                    valueRange = 0f..50f,
                    onValueChange = { newVal ->
                        stateManager.updateConfig(config.copy(yOffset = newVal.toInt()))
                    }
                )

                // Pill Width
                SliderSettingItem(
                    label = "عرض الجزيرة الافتراضي",
                    valueText = "${config.pillWidth} dp",
                    value = config.pillWidth.toFloat(),
                    valueRange = 80f..180f,
                    onValueChange = { newVal ->
                        stateManager.updateConfig(config.copy(pillWidth = newVal.toInt()))
                    }
                )

                // Pill Height
                SliderSettingItem(
                    label = "ارتفاع الجزيرة الافتراضي",
                    valueText = "${config.pillHeight} dp",
                    value = config.pillHeight.toFloat(),
                    valueRange = 26f..46f,
                    onValueChange = { newVal ->
                        stateManager.updateConfig(config.copy(pillHeight = newVal.toInt()))
                    }
                )

                // Corner Radius
                SliderSettingItem(
                    label = "استدارة الحواف (Corner Radius)",
                    valueText = "${config.cornerRadius} dp",
                    value = config.cornerRadius.toFloat(),
                    valueRange = 12f..30f,
                    onValueChange = { newVal ->
                        stateManager.updateConfig(config.copy(cornerRadius = newVal.toInt()))
                    }
                )
            }
        }

        // Visual Toggles & Effects
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = IslandCardDark)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ToggleSettingItem(
                    title = "انعكاس وتوهج الحواف الزجاجية",
                    subtitle = "إطار دقيق يعطي مظهر شاشة الآيفون",
                    checked = config.isGlowEnabled,
                    onCheckedChange = {
                        stateManager.updateConfig(config.copy(isGlowEnabled = it))
                    }
                )

                ToggleSettingItem(
                    title = "الاهتزاز اللمسي (Taptic Haptics)",
                    subtitle = "اهتزاز لطيف عند لمس أو توسيع الجزيرة",
                    checked = config.hapticsEnabled,
                    onCheckedChange = {
                        stateManager.updateConfig(config.copy(hapticsEnabled = it))
                    }
                )

                ToggleSettingItem(
                    title = "دعم الجزيرة المزدوجة (Dual Island)",
                    subtitle = "فصل الأنشطة المتزامنة إلى جزيرتين (كالموسيقى والمؤقت)",
                    checked = config.dualPillEnabled,
                    onCheckedChange = {
                        stateManager.updateConfig(config.copy(dualPillEnabled = it))
                    }
                )
            }
        }

        // Reset to Defaults Button
        OutlinedButton(
            onClick = {
                stateManager.updateConfig(
                    IslandConfig(
                        isEnabled = true,
                        notchPosition = NotchPosition.CENTER,
                        pillWidth = 125,
                        pillHeight = 35,
                        yOffset = 10,
                        xOffset = 0,
                        cornerRadius = 20,
                        isGlowEnabled = true,
                        hapticsEnabled = true,
                        dualPillEnabled = true
                    )
                )
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.RestartAlt, contentDescription = null, tint = AppleRed)
            Spacer(modifier = Modifier.width(8.dp))
            Text("إعادة ضبط الأبعاد للإعدادات الافتراضية", color = AppleRed)
        }
    }
}

@Composable
private fun PositionChip(
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

@Composable
private fun SliderSettingItem(
    label: String,
    valueText: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(text = valueText, color = AppleBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = AppleBlue,
                activeTrackColor = AppleBlue,
                inactiveTrackColor = Color(0xFF2C3240)
            )
        )
    }
}

@Composable
private fun ToggleSettingItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, color = Color(0xFF8E8E93), fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AppleGreen,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color(0xFF2C3240)
            )
        )
    }
}
