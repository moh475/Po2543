package com.example.ui.island

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.RingVolume
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.model.IslandConfig
import com.example.model.IslandEventType

object NotificationStyleHelper {
    val PALETTE_COLORS = listOf(
        0xFF30D158L, // Apple Green
        0xFF0A84FFL, // Apple Blue
        0xFFBF5AF2L, // Apple Purple
        0xFFFF9F0AL, // Apple Orange
        0xFFFF453AL, // Apple Red
        0xFF64D2FFL, // Apple Teal
        0xFFFF375FL, // Apple Pink
        0xFFFFD60AL, // Golden Yellow
        0xFF00E676L  // Neon Mint
    )

    fun getMusicIcons(): List<Pair<String, ImageVector>> = listOf(
        "نوتة" to Icons.Default.MusicNote,
        "سماعة" to Icons.Default.Headphones,
        "ألبوم" to Icons.Default.Album,
        "موجات" to Icons.Default.GraphicEq
    )

    fun getCallIcons(): List<Pair<String, ImageVector>> = listOf(
        "هاتف" to Icons.Default.Call,
        "رنين" to Icons.Default.RingVolume,
        "شخص" to Icons.Default.Person,
        "محادثة" to Icons.Default.PhoneInTalk
    )

    fun getTimerIcons(): List<Pair<String, ImageVector>> = listOf(
        "ساعة رملية" to Icons.Default.HourglassTop,
        "منبه" to Icons.Default.Alarm,
        "مؤقت" to Icons.Default.Timer,
        "جدول" to Icons.Default.Schedule
    )

    fun getChargingIcons(): List<Pair<String, ImageVector>> = listOf(
        "برق" to Icons.Default.Bolt,
        "بطارية" to Icons.Default.BatteryChargingFull,
        "فلاش" to Icons.Default.FlashOn,
        "طاقة" to Icons.Default.Power
    )

    fun getBatteryLowIcons(): List<Pair<String, ImageVector>> = listOf(
        "تنبيه بطارية" to Icons.Default.BatteryAlert,
        "تحذير" to Icons.Default.Warning,
        "خطر" to Icons.Default.ErrorOutline,
        "تنبيه" to Icons.Default.PriorityHigh
    )

    fun getNotificationIcons(): List<Pair<String, ImageVector>> = listOf(
        "جرس" to Icons.Default.Notifications,
        "محادثة" to Icons.Default.Chat,
        "رسالة" to Icons.Default.Mail,
        "منتدى" to Icons.Default.Forum
    )

    fun getBluetoothIcons(): List<Pair<String, ImageVector>> = listOf(
        "سماعات" to Icons.Default.Headphones,
        "بلوتوث" to Icons.Default.Bluetooth,
        "مكبر صوت" to Icons.Default.Speaker,
        "أجهزة" to Icons.Default.DevicesOther
    )

    fun getColorForEvent(type: IslandEventType, config: IslandConfig): Color {
        val colorHex = when (type) {
            IslandEventType.MUSIC -> config.musicColor
            IslandEventType.CALL -> config.callColor
            IslandEventType.TIMER -> config.timerColor
            IslandEventType.CHARGING -> config.chargingColor
            IslandEventType.BATTERY_LOW -> config.batteryLowColor
            IslandEventType.NOTIFICATION -> config.notificationsColor
            IslandEventType.BLUETOOTH -> config.bluetoothColor
            IslandEventType.RINGER -> 0xFFFF453AL
            IslandEventType.NONE -> 0xFFFFFFFFL
        }
        return Color(colorHex)
    }

    fun getIconForEvent(type: IslandEventType, config: IslandConfig): ImageVector {
        return when (type) {
            IslandEventType.MUSIC -> getMusicIcons().getOrElse(config.musicIconIndex) { getMusicIcons()[0] }.second
            IslandEventType.CALL -> getCallIcons().getOrElse(config.callIconIndex) { getCallIcons()[0] }.second
            IslandEventType.TIMER -> getTimerIcons().getOrElse(config.timerIconIndex) { getTimerIcons()[0] }.second
            IslandEventType.CHARGING -> getChargingIcons().getOrElse(config.chargingIconIndex) { getChargingIcons()[0] }.second
            IslandEventType.BATTERY_LOW -> getBatteryLowIcons().getOrElse(config.batteryLowIconIndex) { getBatteryLowIcons()[0] }.second
            IslandEventType.NOTIFICATION -> getNotificationIcons().getOrElse(config.notificationsIconIndex) { getNotificationIcons()[0] }.second
            IslandEventType.BLUETOOTH -> getBluetoothIcons().getOrElse(config.bluetoothIconIndex) { getBluetoothIcons()[0] }.second
            IslandEventType.RINGER -> Icons.Default.VolumeOff
            IslandEventType.NONE -> Icons.Default.Notifications
        }
    }
}
